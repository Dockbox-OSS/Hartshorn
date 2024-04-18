/*
 * Copyright 2019-2024 the original author or authors.
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

/**
 * A {@link TypeReferenceCollector} that provides a predefined set of {@link TypeReference}s. The provided set is
 * returned on every invocation of {@link #collect()}. This is useful when classpath scanning is not desired, but
 * standalone components are to be used.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public final class PredefinedSetTypeReferenceCollector implements TypeReferenceCollector {

    private final Set<TypeReference> references;

    private PredefinedSetTypeReferenceCollector(Set<TypeReference> references) {
        this.references = references;
    }

    @Override
    public Set<TypeReference> collect() {
        return this.references;
    }

    /**
     * Creates a new instance of {@link PredefinedSetTypeReferenceCollector} that provides the provided set of
     * {@link Class classes} as {@link TypeReference}s.
     *
     * @param references The classes to provide as {@link TypeReference}s
     * @return A new instance of {@link PredefinedSetTypeReferenceCollector}
     */
    public static PredefinedSetTypeReferenceCollector of(Set<Class<?>> references) {
        return new PredefinedSetTypeReferenceCollector(references.stream()
                .map(ClassReference::new)
                .collect(Collectors.toSet()));
    }

    /**
     * Creates a new instance of {@link PredefinedSetTypeReferenceCollector} that provides the provided set of
     * {@link Class classes} as {@link TypeReference}s.
     *
     * @param references The classes to provide as {@link TypeReference}s
     * @return A new instance of {@link PredefinedSetTypeReferenceCollector}
     */
    public static PredefinedSetTypeReferenceCollector of(Class<?>... references) {
        return new PredefinedSetTypeReferenceCollector(Stream.of(references)
                .map(ClassReference::new)
                .collect(Collectors.toSet()));
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        String[] typeNames = this.references.stream()
                .map(TypeReference::qualifiedName)
                .toArray(String[]::new);
        collector.property("references").writeStrings(typeNames);
    }
}
