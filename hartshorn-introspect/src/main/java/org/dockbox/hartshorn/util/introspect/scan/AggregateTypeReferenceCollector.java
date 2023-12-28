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
import org.dockbox.hartshorn.reporting.Reportable;

import java.util.HashSet;
import java.util.Set;

/**
 * An {@link AggregateTypeReferenceCollector} that collects {@link TypeReference}s from multiple
 * {@link TypeReferenceCollector}s. The provided collectors are invoked in no particular order.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class AggregateTypeReferenceCollector implements TypeReferenceCollector{

    private final Set<TypeReferenceCollector> collectors;

    public AggregateTypeReferenceCollector(Set<TypeReferenceCollector> collectors) {
        this.collectors = collectors;
    }

    public AggregateTypeReferenceCollector(TypeReferenceCollector... collectors) {
        this(Set.of(collectors));
    }

    @Override
    public Set<TypeReference> collect() throws TypeCollectionException {
        Set<TypeReference> references = new HashSet<>();
        for (TypeReferenceCollector collector : this.collectors) {
            references.addAll(collector.collect());
        }
        return references;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("collectors").write(this.collectors.toArray(Reportable[]::new));
    }
}
