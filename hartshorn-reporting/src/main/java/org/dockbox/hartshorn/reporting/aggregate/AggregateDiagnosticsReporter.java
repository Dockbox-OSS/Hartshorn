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

package org.dockbox.hartshorn.reporting.aggregate;

import java.util.Set;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;

/**
 * A diagnostics reporter that aggregates multiple other reporters, which can be used to report all diagnostics at
 * once. Delegate reporters are expected to be {@link CategorizedDiagnosticsReporter categorized}, so that they can
 * be grouped by category.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AggregateDiagnosticsReporter implements ConfigurableDiagnosticsReporter<AggregateReporterConfiguration> {

    private final AggregateReporterConfiguration configuration = new AggregateReporterConfiguration();

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        Set<CategorizedDiagnosticsReporter> reporters = this.configuration().reporters();

        collector.property("reporters").writeDelegate(reporterCollector -> {
            for (CategorizedDiagnosticsReporter reporter : reporters) {
                reporterCollector.property(reporter.category()).writeStrings(reporter.getClass().getCanonicalName());
            }
        });

        for (CategorizedDiagnosticsReporter reporter : reporters) {
            collector.property(reporter.category()).writeDelegate(reporter);
        }
    }

    @Override
    public AggregateReporterConfiguration configuration() {
        return this.configuration;
    }
}
