/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.objects.external.ExternalClass;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.util.collections.ArrayListHashBiMultiMap;
import org.dockbox.hartshorn.util.collections.BiMultiMap;
import org.dockbox.hartshorn.util.collections.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.stream.EntryStream;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A simple implementation of {@link ExternalClassRegistry} that uses an internal bi-directional
 * multi-map to track imported external classes and their names/aliases.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SimpleExternalClassRegistry implements ExternalClassRegistry {

    private final BiMultiMap<ExternalClass<?>, String> imports = new ArrayListHashBiMultiMap<>();

    @Override
    public Set<ExternalClass<?>> defineClasses(Map<String, TypeView<?>> imports) {
        return imports.entrySet().stream()
            .map(entry -> this.defineClass(entry.getValue(), entry.getKey()))
            .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public <T> ExternalClass<T> defineClass(TypeView<T> typeView) {
        return this.defineClass(typeView, typeView.name());
    }

    @Override
    public <T> ExternalClass<T> defineClass(TypeView<T> typeView, String as) {
        ExternalClass<T> externalClass = new ExternalClass<>(this, typeView, as);
        if (this.imports.containsValue(as)) {
            ExternalClass<?> existingClass =
                CollectionUtilities.first(this.imports.inverse().get(as));
            if (existingClass.type().equals(typeView)) {
                return TypeUtils.unchecked(existingClass, ExternalClass.class);
            }
            else {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.DUPLICATE_EXTERNAL_CLASS_NAME, as)
                    .virtualPosition()
                    .build();
            }
        }
        this.imports.put(externalClass, as);
        return externalClass;
    }

    @Override
    public boolean removeImported(String name) {
        return this.imports.removeValue(name) > 0;
    }

    @Override
    public boolean removeImported(Class<?> type) {
        return this.imports.removeIf((externalClass, alias) -> {
            TypeView<?> typeView = externalClass.type();
            return typeView.is(type);
        }) > 0;
    }

    @Override
    public boolean containsClassName(String name) {
        return this.imports.containsValue(name);
    }

    @Override
    public Option<ExternalClass<?>> getByClassNameOrAlias(String name) {
        ExternalClass<?> externalClass =
            CollectionUtilities.first(this.imports.inverse().get(name));
        return Option.of(externalClass);
    }

    @Override
    public Set<String> getNamesForClass(Class<?> type) {
        return this.imports.stream()
            .filterKeys(externalClass -> externalClass.type().is(type))
            .values()
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public Map<String, ExternalClass<?>> importedClasses() {
        return EntryStream.of(this.imports.inverse())
            .mapValues(CollectionUtilities::first)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
