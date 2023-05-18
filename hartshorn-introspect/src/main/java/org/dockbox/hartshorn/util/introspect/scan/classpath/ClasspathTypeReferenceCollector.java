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

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;
import org.dockbox.hartshorn.util.introspect.scan.TypeCollectionException;
import org.dockbox.hartshorn.util.introspect.scan.TypeReference;
import org.dockbox.hartshorn.util.introspect.scan.TypeReferenceCollector;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ClasspathTypeReferenceCollector implements TypeReferenceCollector {

    private static final MultiMap<String, TypeReference> BATCH_CACHE = new ConcurrentSetMultiMap<>();

    private final String packageName;
    private final Set<TypeReference> cache = ConcurrentHashMap.newKeySet();

    protected ClasspathTypeReferenceCollector(final String packageName) {
        this.packageName = packageName;
    }

    public String packageName() {
        return this.packageName;
    }

    @Override
    public Set<TypeReference> collect() throws TypeCollectionException {
        if (!BATCH_CACHE.containsKey(this.packageName)) {
            BATCH_CACHE.putAll(this.packageName, this.createCache());
        }
        // Don't use local cache in batch mode, to avoid n*2 memory usage
        return Set.copyOf(BATCH_CACHE.get(this.packageName));
    }

    protected abstract Set<TypeReference> createCache() throws TypeCollectionException;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final ClasspathTypeReferenceCollector that)) return false;
        return this.packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.packageName);
    }
}
