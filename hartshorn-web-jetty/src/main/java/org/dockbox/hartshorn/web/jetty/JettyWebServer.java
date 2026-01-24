package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.util.stream.CollectorUtilities;
import org.dockbox.hartshorn.web.ServerException;
import org.dockbox.hartshorn.web.WebServer;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;

import java.util.Arrays;

public class JettyWebServer implements WebServer {

    private final Server jettyServer;

    public JettyWebServer(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    public Server jettyServer() {
        return jettyServer;
    }

    @Override
    public void start() throws ServerException {
        if (jettyServer.isStarted() || jettyServer.isStarting()) {
            throw new IllegalStateException("Cannot start a web server that is already running");
        }
        try {
            jettyServer.start();
        }
        catch (Exception e) {
            throw new ServerException("Failed to start Jetty server", e);
        }
    }

    @Override
    public void stop() throws ServerException {
        if (!jettyServer.isStarted()) {
            throw new IllegalStateException("Cannot stop a web server that is not running");
        }
        try {
            jettyServer.stop();
        }
        catch (Exception e) {
            throw new ServerException("Failed to stop Jetty server", e);
        }
    }

    @Override
    public boolean running() {
        return jettyServer.isStarted();
    }

    @Override
    public int port() {
        return Arrays.stream(jettyServer.getConnectors())
                .filter(NetworkConnector.class::isInstance)
                .map(NetworkConnector.class::cast)
                .collect(CollectorUtilities.toOption())
                .map(NetworkConnector::getLocalPort)
                .orElseThrow(() -> new IllegalStateException(
                        "No network connector available to determine port"
                ));
    }
}
