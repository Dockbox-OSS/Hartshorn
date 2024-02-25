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

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import java.util.Objects;
import java.util.Set;

import org.dockbox.hartshorn.util.collections.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;

/**
 * A {@link TypeReferenceCollector} that collects {@link TypeReference}s from a classpath. This class is abstract, as
 * it does not provide a mechanism for collecting {@link TypeReference}s. Instead, it provides a caching mechanism that
 * is shared between all instances of this class. This caching mechanism is used to prevent the same classpath from
 * being scanned multiple times.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public abstract class ClasspathTypeReferenceCollector implements TypeReferenceCollector {

    private static final MultiMap<String, TypeReference> BATCH_CACHE = new ConcurrentSetMultiMap<>();
    private final String packageName;

    protected ClasspathTypeReferenceCollector(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Returns the name of the package that is scanned by this instance.
     *
     * @return The name of the package that is being scanned.
     */
    public String packageName() {
        return this.packageName;
    }

    @Override
    public Set<TypeReference> collect() throws TypeCollectionException {
        if (!BATCH_CACHE.containsKey(this.packageName)) {
            Set<TypeReference> cache = this.createCache();
            BATCH_CACHE.putAll(this.packageName, cache);
        }
        // Don't use local cache in batch mode, to avoid n*2 memory usage
        return Set.copyOf(BATCH_CACHE.get(this.packageName));
    }

    /**
     * Collects {@link TypeReference}s from the classpath. Implementations of this method are expected to return a
     * {@link Set} of {@link TypeReference}s that are found on the classpath. Implementations are not expected to
     * cache the result of this method, as the result is cached internally by this class.
     *
     * @return A {@link Set} of {@link TypeReference}s that are found on the classpath.
     * @throws TypeCollectionException If the collection of {@link TypeReference}s fails.
     */
    protected abstract Set<TypeReference> createCache() throws TypeCollectionException;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClasspathTypeReferenceCollector collector)) {
            return false;
        }
        return this.packageName.equals(collector.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.packageName);
    }
}
