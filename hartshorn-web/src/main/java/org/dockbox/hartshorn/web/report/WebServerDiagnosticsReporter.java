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

package org.dockbox.hartshorn.web.report;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.WebServer;

/**
 * A diagnostics reporter for the {@link WebServer}, providing information about its state.
 *
 * <p>If the {@link WebServer} implements {@link Reportable}, additional details will be included.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class WebServerDiagnosticsReporter implements CategorizedDiagnosticsReporter {

    private final WebServer webServer;

    public WebServerDiagnosticsReporter(WebServer webServer) {
        this.webServer = webServer;
    }

    @Override
    public String category() {
        return "webserver";
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("port").writeInt(this.webServer.port());
        collector.property("running").writeBoolean(this.webServer.running());
        if (this.webServer instanceof Reportable reportable) {
            collector.property("details").writeDelegate(reportable);
        }
    }
}
