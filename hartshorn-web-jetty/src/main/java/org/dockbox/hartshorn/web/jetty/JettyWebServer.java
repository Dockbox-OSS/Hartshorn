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

package org.dockbox.hartshorn.web.jetty;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.stream.StreamGatherers;
import org.dockbox.hartshorn.web.ServerException;
import org.dockbox.hartshorn.web.WebServer;
import org.dockbox.hartshorn.web.jetty.report.NetworkConnectorReporter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;

import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link WebServer} using Jetty as the underlying server.
 *
 * @param jettyServer the backing Jetty server instance
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record JettyWebServer(Server jettyServer) implements WebServer, Reportable {

    @Override
    public void start() throws ServerException {
        if (this.jettyServer.isStarted() || this.jettyServer.isStarting()) {
            throw new IllegalStateException("Cannot start a web server that is already running");
        }
        try {
            this.jettyServer.start();
        }
        catch (Exception e) {
            throw new ServerException("Failed to start Jetty server", e);
        }
    }

    @Override
    public void stop() throws ServerException {
        if (!this.jettyServer.isStarted()) {
            throw new IllegalStateException("Cannot stop a web server that is not running");
        }
        try {
            this.jettyServer.stop();
        }
        catch (Exception e) {
            throw new ServerException("Failed to stop Jetty server", e);
        }
    }

    @Override
    public boolean running() {
        return this.jettyServer.isStarted();
    }

    @Override
    public int port() {
        return getNetworkConnectors().stream()
            .collect(Option.collector())
            .map(NetworkConnector::getLocalPort)
            .orElseThrow(() -> new IllegalStateException(
                "No network connector available to determine port"
            ));
    }

    private List<NetworkConnector> getNetworkConnectors() {
        return Arrays.stream(this.jettyServer.getConnectors())
                .gather(StreamGatherers.filterByType(NetworkConnector.class))
                .toList();
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        collector.property("connectors").writeDelegates(getNetworkConnectors().stream()
                .map(NetworkConnectorReporter::new)
                .toArray(Reportable[]::new));

        Handler handler = this.jettyServer.getHandler();
        if (handler instanceof Reportable reportable) {
            collector.property("handler").writeDelegate(reportable);
        }
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("jettyServer", this.jettyServer)
                .describe();
    }
}
