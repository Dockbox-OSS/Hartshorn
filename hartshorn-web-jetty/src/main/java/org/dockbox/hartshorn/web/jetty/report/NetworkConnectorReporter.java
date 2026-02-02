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

package org.dockbox.hartshorn.web.jetty.report;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.eclipse.jetty.server.NetworkConnector;

/**
 * A reporter for Jetty {@link NetworkConnector} instances.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
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
