package org.dockbox.hartshorn.web.jetty.report;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.eclipse.jetty.server.NetworkConnector;

public class NetworkConnectorReporter implements Reportable {

    private final NetworkConnector connector;

    public NetworkConnectorReporter(NetworkConnector connector) {
        this.connector = connector;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("host").writeString(this.connector.getHost());
        collector.property("port").writeInt(this.connector.getPort());
        collector.property("localPort").writeInt(this.connector.getLocalPort());
        collector.property("idleTimeout").writeLong(this.connector.getIdleTimeout());
        collector.property("protocols").writeStrings(
                this.connector.getProtocols().toArray(String[]::new)
        );
    }
}
