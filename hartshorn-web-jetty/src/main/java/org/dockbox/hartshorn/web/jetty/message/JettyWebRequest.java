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

import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.HttpMessageHeaders;
import org.dockbox.hartshorn.web.message.HttpMessageQuery;
import org.dockbox.hartshorn.web.message.HttpMessageBody;
import org.dockbox.hartshorn.web.message.WebRequest;
import org.dockbox.hartshorn.web.message.WebRequestClient;
import org.dockbox.hartshorn.web.message.WebRequestPathParameters;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.Fields;

/**
 * An implementation of {@link WebRequest} for Jetty's {@link Request}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyWebRequest implements WebRequest {

    private final Request request;

    public JettyWebRequest(Request request) {
        this.request = request;
    }

    /**
     * Returns the underlying Jetty {@link Request}.
     *
     * @return The underlying Jetty request.
     */
    public Request underlyingRequest() {
        return this.request;
    }

    @Override
    public HttpMethod method() {
        String method = this.request.getMethod();
        return HttpMethod.fromString(method).orElse(null);
    }

    @Override
    public HttpMessageHeaders headers() {
        return new JettyHttpMessageHeaders(this.request.getHeaders());
    }

    @Override
    public HttpMessageQuery query() {
        Fields queryParameters = Request.extractQueryParameters(this.request);
        return new JettyHttpMessageQuery(queryParameters);
    }

    @Override
    public HttpMessageBody body() {
        return new JettyHttpMessageBody(this.request);
    }

    @Override
    public WebRequestClient client() {
        return new JettyWebRequestClient(this.request);
    }

    @Override
    public WebRequestPathParameters pathParameters() {
        throw new UnsupportedOperationException(
            "Path parameters are not supported in JettyWebRequest, "
                + "should be wrapped by PathParamAwareWebRequest."
        );
    }

    @Override
    public String path() {
        return Request.getPathInContext(this.request);
    }
}
