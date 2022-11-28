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

package org.dockbox.hartshorn.application.scan;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.DiagnosticsReporter;

import java.util.Set;
import java.util.stream.Collectors;

public class AggregateTypeReferenceCollector implements TypeReferenceCollector{

    private final Set<TypeReferenceCollector> collectors;

    public AggregateTypeReferenceCollector(final Set<TypeReferenceCollector> collectors) {
        this.collectors = collectors;
    }

    public AggregateTypeReferenceCollector(final TypeReferenceCollector... collectors) {
        this(Set.of(collectors));
    }

    @Override
    public Set<TypeReference> collect() {
        return this.collectors.stream()
                .flatMap(c -> c.collect().stream())
                .collect(Collectors.toSet());
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final DiagnosticsReporter[] reporters = this.collectors.stream()
                .map(referenceCollector -> (DiagnosticsReporter) referenceCollector::report)
                .toArray(DiagnosticsReporter[]::new);
        collector.property("collectors").write(reporters);
    }
}
