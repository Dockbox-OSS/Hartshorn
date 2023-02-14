/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReflectionTypeFieldsIntrospector<T> implements TypeFieldsIntrospector<T> {

    private static final Set<String> EXCLUDED_FIELDS = Set.of(
            /*
             * This field is a synthetic field which is added by IntelliJ IDEA when running tests with
             * coverage.
             */
            "__$lineHits$__"
    );

    private final Map<String, FieldView<T, ?>> fields = new ConcurrentHashMap<>();

    private final Introspector introspector;
    private final TypeView<T> type;

    public ReflectionTypeFieldsIntrospector(final Introspector introspector, final TypeView<T> type) {
        this.introspector = introspector;
        this.type = type;
    }

    private void collect() {
        if (this.fields.isEmpty()) {
            for (final Field declared : this.type.type().getDeclaredFields()) {
                if (EXCLUDED_FIELDS.contains(declared.getName()))
                    continue;

                this.fields.put(declared.getName(), (FieldView<T, ?>) this.introspector.introspect(declared));
            }
            if (!(this.type.superClass().isVoid() || Object.class.equals(this.type.superClass().type()))) {
                for (final FieldView<?, ?> field : this.type.superClass().fields().all()) {
                    this.fields.put(field.name(), (FieldView<T, ?>) field);
                }
            }
        }
    }

    @Override
    public Option<FieldView<T, ?>> named(final String name) {
        this.collect();
        if (this.fields.containsKey(name))
            return Option.of(this.fields.get(name));
        else if (!this.type.superClass().isVoid())
            return this.type.superClass().fields().named(name)
                    .map(field -> (FieldView<T, ?>) field);
        return Option.empty();
    }

    @Override
    public List<FieldView<T, ?>> all() {
        this.collect();
        return List.copyOf(this.fields.values());
    }

    @Override
    public List<FieldView<T, ?>> annotatedWith(final Class<? extends Annotation> annotation) {
        return this.all().stream()
                .filter(field -> field.annotations().has(annotation))
                .toList();
    }

    @Override
    public <F> List<FieldView<T, ? extends F>> typed(final Class<F> type) {
        return this.all().stream()
                .filter(field -> field.type().is(type))
                .map(field -> (FieldView<T, ? extends F>) field)
                .collect(Collectors.toList());
    }

    @Override
    public <F> List<FieldView<T, ? extends F>> typed(final GenericType<F> type) {
        return this.all().stream()
                .filter(field -> field.type().is(type.asClass().get()))
                .filter(field -> {
                    final TypeView<?> genericType = field.genericType();
                    final List<TypeView<?>> typeParameters = genericType.typeParameters().all();
                    final Option<Class<F>> classType = type.asClass();
                    if (classType.absent()) return false;

                    final TypeView<F> targetGenericType = this.introspector.introspect(classType.get());
                    final List<TypeView<?>> targetTypeParameters = targetGenericType.typeParameters().all();
                    if (targetTypeParameters.size() != typeParameters.size()) return false;

                    for (int i = 0; i < typeParameters.size(); i++) {
                        final TypeView<?> typeParameter = typeParameters.get(i);
                        final TypeView<?> targetTypeParameter = targetTypeParameters.get(i);
                        if (!typeParameter.is(targetTypeParameter.type())) return false;
                    }
                    return true;
                })
                .map(field -> (FieldView<T, ? extends F>) field)
                .collect(Collectors.toList());
    }
}
