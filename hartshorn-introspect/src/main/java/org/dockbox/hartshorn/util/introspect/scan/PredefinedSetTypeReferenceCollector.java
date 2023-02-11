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

package org.dockbox.hartshorn.util.introspect.scan;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredefinedSetTypeReferenceCollector implements TypeReferenceCollector {

    private final Set<TypeReference> references;

    private PredefinedSetTypeReferenceCollector(final Set<TypeReference> references) {
        this.references = references;
    }

    @Override
    public Set<TypeReference> collect() {
        return this.references;
    }

    public static PredefinedSetTypeReferenceCollector of(final Set<Class<?>> references) {
        return new PredefinedSetTypeReferenceCollector(references.stream()
                .map(ClassReference::new)
                .collect(Collectors.toSet()));
    }

    public static PredefinedSetTypeReferenceCollector of(final Class<?>... references) {
        return new PredefinedSetTypeReferenceCollector(Stream.of(references)
                .map(ClassReference::new)
                .collect(Collectors.toSet()));
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final String[] typeNames = this.references.stream()
                .map(TypeReference::qualifiedName)
                .toArray(String[]::new);
        collector.property("references").write(typeNames);
    }
}
