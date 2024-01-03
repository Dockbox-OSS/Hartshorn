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

package org.dockbox.hartshorn.reporting;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.reporting.aggregate.AggregateDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.aggregate.AggregateReporterConfiguration;
import org.dockbox.hartshorn.reporting.application.ApplicationDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.collect.StandardDiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.component.ComponentDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.component.ComponentProcessorDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.system.SystemDiagnosticsReporter;

@Service
@RequiresActivator(UseReporting.class)
public class ReportingProviders {

    @Binds
    public Reportable applicationReportable(ApplicationContext applicationContext) {
        ConfigurableDiagnosticsReporter<AggregateReporterConfiguration> reporter = new AggregateDiagnosticsReporter();
        reporter.configuration().add(new SystemDiagnosticsReporter());
        reporter.configuration().add(new ApplicationDiagnosticsReporter(applicationContext));
        reporter.configuration().add(new ComponentDiagnosticsReporter(applicationContext));
        reporter.configuration().add(new ComponentProcessorDiagnosticsReporter(applicationContext));

        return reporter;
    }

    @Binds
    public DiagnosticsPropertyCollector diagnosticsPropertyCollector() {
        return new StandardDiagnosticsReportCollector();
    }

    @Binds
    public DiagnosticsReportCollector diagnosticsReportCollector() {
        return new StandardDiagnosticsReportCollector();
    }
}
