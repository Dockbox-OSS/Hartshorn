/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.application.scan.classpath;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.scan.TypeReference;
import org.dockbox.hartshorn.application.scan.TypeReferenceCollector;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetMultiMap;

public abstract class ClasspathTypeReferenceCollector implements TypeReferenceCollector {

    private static final MultiMap<String, TypeReference> BATCH_CACHE = new ConcurrentSetMultiMap<>();

    private final ApplicationEnvironment environment;
    private final String packageName;
    private final Set<TypeReference> cache = ConcurrentHashMap.newKeySet();

    public ClasspathTypeReferenceCollector(final ApplicationEnvironment environment, final String packageName) {
        this.environment = environment;
        this.packageName = packageName;
    }

    public String packageName() {
        return this.packageName;
    }

    protected ApplicationEnvironment environment() {
        return this.environment;
    }

    @Override
    public Set<TypeReference> collect() {
        if (this.environment.isBatchMode() && BATCH_CACHE.containsKey(this.packageName)) {
            // Don't use local cache in batch mode, to avoid n*2 memory usage
            return Set.copyOf(BATCH_CACHE.get(this.packageName));
        }

        if (this.cache.isEmpty()) {
            this.cache.addAll(this.createCache());

            if (this.environment.isBatchMode()) {
                BATCH_CACHE.putAll(this.packageName, this.cache);
            }
        }
        return Collections.unmodifiableSet(this.cache);
    }

    protected abstract Set<TypeReference> createCache();

}
