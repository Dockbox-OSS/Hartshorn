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

/**
 * A {@link DiagnosticsReportCollector} is a {@link DiagnosticsPropertyCollector} that exposes a
 * {@link #report()} method to retrieve the collected properties as a {@link DiagnosticsReport}.
 *
 * <p>Nodes can be added to the report by calling {@link #property(String)}, and are expected to
 * have unique names within the report. If a node with the same name is added twice, the second
 * node will replace the first.
 *
 * <p>Additionally, {@link CategorizedDiagnosticsReporter} instances can be added to the report
 * by calling {@link #visit(CategorizedDiagnosticsReporter)}. The category of the reporter is used
 * as the name of the node in the report.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DiagnosticsReportCollector extends DiagnosticsPropertyCollector {

    /**
     * Adds the given {@link CategorizedDiagnosticsReporter} to the report. The category of the reporter is used as the
     * name of the node in the report.
     *
     * @param reporter the reporter to add to the report
     */
    void visit(CategorizedDiagnosticsReporter reporter);

    /**
     * Returns a {@link DiagnosticsReport} that contains the properties that have been collected by this instance. If no
     * properties have been collected, an empty report is returned. Additional properties may also be
     * added to the report.
     *
     * @return a report of the collected properties
     */
    DiagnosticsReport report();

    /**
     * Returns a {@link DiagnosticsReport} that contains the properties that have been collected by this instance, appended
     * with the properties of the given {@link Reportable}. Depending on the implementation, the properties of the given
     * {@link Reportable} may be added to the report, or may replace existing properties. Additional properties may also be
     * added to the report.
     *
     * @param reportable the reportable to add to the report
     * @return a report of the collected properties
     */
    DiagnosticsReport report(Reportable reportable);
}
