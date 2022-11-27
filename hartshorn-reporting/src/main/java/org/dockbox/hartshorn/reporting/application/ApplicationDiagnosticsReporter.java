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

package org.dockbox.hartshorn.reporting.application;

import org.dockbox.hartshorn.application.Hartshorn;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.reporting.ConfigurableDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsReporter;
import org.dockbox.hartshorn.reporting.collect.DiagnosticsReportCollector;

import java.util.Properties;

public class ApplicationDiagnosticsReporter implements ConfigurableDiagnosticsReporter<ApplicationReportingConfiguration> {

    private final ApplicationReportingConfiguration configuration = new ApplicationReportingConfiguration();

    private final ApplicationContext applicationContext;

    public ApplicationDiagnosticsReporter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void report(final DiagnosticsReportCollector collector) {
        if (this.configuration.includeVersion()) {
            collector.property("version").write(Hartshorn.VERSION);
        }

        if (this.configuration.includeJarLocation()) {

        }
        if (this.configuration.includeApplicationProperties()) {
            final Properties properties = this.applicationContext.properties();
            final DiagnosticsReporter reporter = new PropertiesReporter(properties);
            collector.property("properties").write(reporter);
        }
        if (this.configuration.includeServiceActivators()) {


        }
        if (this.configuration.includeObservers()) {

        }
        if (this.configuration.includeContexts()) {

        }
    }

    @Override
    public ApplicationReportingConfiguration configuration() {
        return this.configuration;
    }
}
