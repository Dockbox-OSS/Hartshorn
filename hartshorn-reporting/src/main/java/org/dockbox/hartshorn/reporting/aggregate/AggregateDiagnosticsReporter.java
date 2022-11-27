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

package org.dockbox.hartshorn.reporting.aggregate;

import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsReporter;
import org.dockbox.hartshorn.reporting.collect.DiagnosticsReportCollector;

import java.util.Set;

public class AggregateDiagnosticsReporter implements ConfigurableDiagnosticsReporter<AggregateReporterConfiguration> {

    private final AggregateReporterConfiguration configuration = new AggregateReporterConfiguration();

    @Override
    public void report(final DiagnosticsReportCollector collector) {
        final Set<DiagnosticsReporter> reporters = this.configuration().reporters();

        // Write reporters first, so these are always known even if a delegated reporter
        // fails while reporting.
        final String[] reporterNames = reporters.stream()
                .map(Object::getClass)
                .map(Class::getCanonicalName)
                .toArray(String[]::new);
        collector.property("reporters").write(reporterNames);

        for (final DiagnosticsReporter reporter : reporters) {
            // TODO: Better names for reporters (make them identifiable/categorizable?)
            collector.property(reporter.getClass().getSimpleName().toLowerCase()).write(reporter);
        }
    }

    @Override
    public AggregateReporterConfiguration configuration() {
        return this.configuration;
    }
}
