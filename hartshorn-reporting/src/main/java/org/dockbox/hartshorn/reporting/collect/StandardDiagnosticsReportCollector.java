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

package org.dockbox.hartshorn.reporting.collect;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyWriter;
import org.dockbox.hartshorn.reporting.DiagnosticsReport;
import org.dockbox.hartshorn.reporting.DiagnosticsReportCollector;
import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.Reportable;

public class StandardDiagnosticsReportCollector implements DiagnosticsReportCollector {

    private final NodeDiagnosticsReport report = new NodeDiagnosticsReport();

    public NodeDiagnosticsReport report() {
        return this.report;
    }

    @Override
    public DiagnosticsReport report(final Reportable reportable) {
        reportable.report(this);
        return this.report;
    }

    @Override
    public DiagnosticsPropertyWriter property(final String name) {
        return new StandardDiagnosticsPropertyWriter(this.normalize(name), this, this.report().root());
    }

    @Override
    public void visit(final CategorizedDiagnosticsReporter reporter) {
        this.property(reporter.category()).write(reporter);
    }

    private String normalize(final String name) {
        return name.toLowerCase().replace(" ", "_");
    }

}
