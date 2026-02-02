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

package org.dockbox.hartshorn.web.jetty.route;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.web.filter.RequestFilterChain;
import org.dockbox.hartshorn.web.jetty.message.JettyWebRequest;
import org.dockbox.hartshorn.web.jetty.message.JettyWebResponse;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

/**
 * A Jetty-specific request handler that integrates with the Hartshorn web framework's
 * request filter chain.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyRequestHandler extends Handler.Abstract implements Reportable {

    private final RequestFilterChain chain;

    @Inject
    public JettyRequestHandler(RequestFilterChain chain) {
        this.chain = chain;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        WebRequest webRequest = new JettyWebRequest(request);
        WebResponse webResponse = new JettyWebResponse(response);
        boolean accepted = this.chain.accept(webRequest, webResponse);
        if (accepted) {
            callback.succeeded();
        }
        else {
            callback.failed(new IllegalStateException(
                "Request was not handled by the filter chain"
            ));
        }
        return accepted;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        if (chain instanceof Reportable reportable) {
            collector.property("filterChain").writeDelegate(reportable);
        }
    }
}
