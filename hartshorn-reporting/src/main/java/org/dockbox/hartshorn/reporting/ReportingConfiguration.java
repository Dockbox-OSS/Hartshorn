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
import org.dockbox.hartshorn.component.Configuration;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.CompositeMember;
import org.dockbox.hartshorn.component.processing.Prototype;
import org.dockbox.hartshorn.component.processing.Singleton;
import org.dockbox.hartshorn.inject.Strict;
import org.dockbox.hartshorn.inject.SupportPriority;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.reporting.aggregate.AggregateDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.aggregate.AggregateReporterConfiguration;
import org.dockbox.hartshorn.reporting.application.ApplicationDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.collect.StandardDiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.component.ComponentDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.component.ComponentProcessorDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.system.SystemDiagnosticsReporter;

/**
 * Default configuration for reporting. This provides default implementations for common reporters that may be used
 * throughout the application.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresActivator(UseReporting.class)
public class ReportingConfiguration {

    /**
     * Configures a global {@link Reportable} capable of reporting on the entire application. This reporter aggregates
     * all {@link CategorizedDiagnosticsReporter categorized reporters} that are registered in the application context.
     *
     * @param diagnosticsReporters All reporters that are registered in the application context
     *
     * @return a global reporter that aggregates all reporters that are registered in the application context
     *
     * @see AggregateDiagnosticsReporter
     */
    @Prototype
    @SupportPriority
    public Reportable applicationReportable(@Strict(false) ComponentCollection<CategorizedDiagnosticsReporter> diagnosticsReporters) {
        ConfigurableDiagnosticsReporter<AggregateReporterConfiguration> reporter = new AggregateDiagnosticsReporter();
        reporter.configuration().addAll(diagnosticsReporters);
        return reporter;
    }

    /**
     * Configures a {@link CategorizedDiagnosticsReporter reporter} that reports on the system. This reporter is
     * capable of reporting on the JVM, including process, OS, memory information and various related elements.
     *
     * @return a reporter that reports on the system
     *
     * @see SystemDiagnosticsReporter
     */
    @Singleton
    @CompositeMember
    @SupportPriority
    public CategorizedDiagnosticsReporter systemDiagnosticsReporter() {
        return new SystemDiagnosticsReporter();
    }

    /**
     * Configures a {@link CategorizedDiagnosticsReporter reporter} that reports on the application. This reporter is
     * capable of reporting on the application version, JAR location, properties and various related elements.
     *
     * @param applicationContext the application context
     * @return a reporter that reports on the application
     *
     * @see ApplicationDiagnosticsReporter
     */
    @Singleton
    @CompositeMember
    @SupportPriority
    public CategorizedDiagnosticsReporter applicationDiagnosticsReporter(ApplicationContext applicationContext) {
        return new ApplicationDiagnosticsReporter(applicationContext);
    }

    /**
     * Configures a {@link CategorizedDiagnosticsReporter reporter} that reports on all components that are registered
     * in the application context. This reporter is capable of reporting on all components, including their dependencies
     * and various related elements.
     *
     * @param applicationContext the application context
     * @return a reporter that reports on all components that are registered in the application context
     *
     * @see ComponentDiagnosticsReporter
     */
    @Singleton
    @CompositeMember
    @SupportPriority
    public CategorizedDiagnosticsReporter componentDiagnosticsReporter(ApplicationContext applicationContext) {
        return new ComponentDiagnosticsReporter(applicationContext);
    }

    /**
     * Configures a {@link CategorizedDiagnosticsReporter reporter} that reports on all pre- and post-processors that
     * are registered in the application context. This reporter is capable of reporting on all processors, including
     * their name and priority.
     *
     * @param applicationContext the application context
     * @return a reporter that reports on all pre- and post-processors that are registered in the application context
     *
     * @see ComponentProcessorDiagnosticsReporter
     */
    @Singleton
    @CompositeMember
    @SupportPriority
    public CategorizedDiagnosticsReporter componentProcessorDiagnosticsReporter(ApplicationContext applicationContext) {
        return new ComponentProcessorDiagnosticsReporter(applicationContext);
    }

    /**
     * Configures a {@link DiagnosticsReportCollector} that is capable of collecting all diagnostics and writing them
     * to a {@link DiagnosticsReport}.
     *
     * @return a diagnostics report collector
     *
     * @see StandardDiagnosticsReportCollector
     */
    @Singleton
    @SupportPriority
    public DiagnosticsReportCollector diagnosticsReportCollector() {
        return new StandardDiagnosticsReportCollector();
    }
}
