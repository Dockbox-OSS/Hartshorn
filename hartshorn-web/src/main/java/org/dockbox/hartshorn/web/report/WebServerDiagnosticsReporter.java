package org.dockbox.hartshorn.web.report;

import org.dockbox.hartshorn.reporting.CategorizedDiagnosticsReporter;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.WebServer;

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
