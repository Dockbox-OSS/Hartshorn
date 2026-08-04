/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.reporting.aggregate;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A diagnostics reporter that aggregates multiple other reporters, which can be used to report all
 * diagnostics at once. Delegate reporters are expected to be
 * {@link CategorizedDiagnosticsReporter categorized}, so that they can be grouped by category.
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
public class AggregateDiagnosticsReporter
        implements ConfigurableDiagnosticsReporter<AggregateReporterConfiguration> {

    private final AggregateReporterConfiguration configuration;

    public AggregateDiagnosticsReporter(AggregateReporterConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        Set<CategorizedDiagnosticsReporter> reporters = this.configuration().reporters();
        Map<String, CategorizedDiagnosticsReporter> collectorsByCategory = reporters
                .stream()
                .collect(Collectors.toMap(
                        CategorizedDiagnosticsReporter::category,
                        Function.identity()
                ));
        TreeSet<String> sortedCategories = new TreeSet<>(collectorsByCategory.keySet());

        collector.property("reporters").writeDelegate(reporterCollector -> {
            for (String category : sortedCategories) {
                var reporter = collectorsByCategory.get(category);
                reporterCollector.property(category)
                        .writeString(reporter.getClass().getCanonicalName());
            }
        });

        for (String category : sortedCategories) {
            var reporter = collectorsByCategory.get(category);
            collector.property(category).writeDelegate(reporter);
        }
    }

    @Override
    public AggregateReporterConfiguration configuration() {
        return this.configuration;
    }
}
