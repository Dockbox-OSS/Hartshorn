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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;

/**
 * A {@link TypeReferenceCollectorContext} is a {@link org.dockbox.hartshorn.context.Context} that is used
 * to track {@link TypeReferenceCollector} which are used by the application to discover types on the classpath.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class TypeReferenceCollectorContext extends DefaultContext implements Reportable {

    private final Set<TypeReferenceCollector> collectors = ConcurrentHashMap.newKeySet();

    private TypeReferenceCollector aggregate;

    /**
     * Registers a {@link TypeReferenceCollector} with this context.
     *
     * @param collector The collector to register
     */
    public synchronized void register(TypeReferenceCollector collector) {
        this.collectors.add(collector);
        this.aggregate = null;
    }

    /**
     * Creates a new {@link AggregateTypeReferenceCollector} that combines all registered collectors.
     *
     * @return A new {@link AggregateTypeReferenceCollector}
     */
    public synchronized TypeReferenceCollector collector() {
        if (this.aggregate == null) {
            AggregateTypeReferenceCollector collector = new AggregateTypeReferenceCollector(this.collectors);
            this.aggregate = new CachedTypeReferenceCollector(collector);
        }
        return this.aggregate;
    }

    /**
     * Returns all registered collectors.
     *
     * @return A {@link Set} of {@link TypeReferenceCollector}s
     */
    public Set<TypeReferenceCollector> collectors() {
        return Collections.unmodifiableSet(this.collectors);
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        Reportable[] reporters = this.collectors.stream()
                .map(referenceCollector -> (Reportable) trcCollector -> {
                    trcCollector.property("type").writeString(referenceCollector.getClass().getName());
                    referenceCollector.report(trcCollector);
                }).toArray(Reportable[]::new);
        collector.property("collectors").writeDelegates(reporters);
    }
}
