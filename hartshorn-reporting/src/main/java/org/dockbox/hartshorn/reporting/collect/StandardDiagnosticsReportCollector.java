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

package org.dockbox.hartshorn.reporting.collect;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.Reportable;

/**
 * A standard implementation of {@link DiagnosticsReportCollector} which uses a {@link NodeDiagnosticsReport} as the
 * backing data structure.
 *
 * @see NodeDiagnosticsReport
 * @see DiagnosticsReportCollector
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class StandardDiagnosticsReportCollector implements DiagnosticsReportCollector {

    private final NodeDiagnosticsReport report = new NodeDiagnosticsReport();

    @Override
    public NodeDiagnosticsReport report() {
        return this.report;
    }

    @Override
    public DiagnosticsReport report(Reportable reportable) {
        this.property("timestamp").writeString(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        reportable.report(this);
        return this.report;
    }

    @Override
    public DiagnosticsPropertyWriter property(String name) {
        return new StandardDiagnosticsPropertyWriter(name, this, this.report().root());
    }

    @Override
    public void visit(CategorizedDiagnosticsReporter reporter) {
        this.property(reporter.category()).writeDelegate(reporter);
    }
}
