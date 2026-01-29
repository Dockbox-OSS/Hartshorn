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

import org.dockbox.hartshorn.web.HttpStatusCode;
import org.dockbox.hartshorn.web.message.MutableHttpMessageHeaders;
import org.dockbox.hartshorn.web.message.ResponseWriter;
import org.dockbox.hartshorn.web.message.WebResponse;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Response;

import java.nio.ByteBuffer;

/**
 * An implementation of {@link WebResponse} for Jetty's {@link Response}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JettyWebResponse implements WebResponse {

    private final Response response;

    public JettyWebResponse(Response response) {
        this.response = response;
    }

    /**
     * Gets the underlying Jetty {@link Response}.
     *
     * @return The underlying Jetty response.
     */
    public Response underlyingResponse() {
        return this.response;
    }

    @Override
    public void write(ByteBuffer data) throws Exception {
        Content.Sink.write(this.response, true, data);
    }

    @Override
    public <T> void write(ResponseWriter<T> writer, T body) throws Exception {
        writer.write(this, body);
    }

    @Override
    public void status(HttpStatusCode status) {
        this.response.setStatus(status.code());
    }

    @Override
    public HttpStatusCode statusCode() {
        return HttpStatusCode.of(this.response.getStatus());
    }

    @Override
    public MutableHttpMessageHeaders headers() {
        return new JettyHttpMessageHeaders(this.response.getHeaders());
    }

    @Override
    public boolean committed() {
        return this.response.isCommitted();
    }
}
