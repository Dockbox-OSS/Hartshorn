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

package org.dockbox.hartshorn.web.jetty.message;

import org.dockbox.hartshorn.web.message.WebRequestClient;
import org.eclipse.jetty.server.Request;

/**
 * An implementation of {@link WebRequestClient} for Jetty server requests.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
class JettyWebRequestClient implements WebRequestClient {

    private final Request request;

    JettyWebRequestClient(Request request) {
        this.request = request;
    }

    @Override
    public String address() {
        return Request.getRemoteAddr(this.request);
    }

    @Override
    public int port() {
        return Request.getRemotePort(this.request);
    }
}
