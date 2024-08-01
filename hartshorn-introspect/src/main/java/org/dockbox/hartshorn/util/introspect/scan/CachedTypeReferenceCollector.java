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

/**
 * A {@link TypeReferenceCollector} that caches the result of a delegate collector. The cache is populated on the
 * first invocation of {@link #collect()}, and is reused for all subsequent invocations.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class CachedTypeReferenceCollector implements TypeReferenceCollector {

    private final TypeReferenceCollector collector;
    private Set<TypeReference> cache;

    public CachedTypeReferenceCollector(TypeReferenceCollector collector) {
        this.collector = collector;
    }

    @Override
    public Set<TypeReference> collect() throws TypeCollectionException {
        if (this.cache == null) {
            this.cache = this.collector.collect();
        }
        return this.cache;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        this.collector.report(collector);
    }
}
