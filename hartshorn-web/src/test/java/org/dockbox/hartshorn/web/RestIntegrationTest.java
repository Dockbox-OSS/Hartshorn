/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.web;

import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;

import java.io.IOException;
import java.util.function.Function;

@UseHttpServer
@HartshornTest
public abstract class RestIntegrationTest {

    protected static final String ADDRESS = "http://localhost:" + HttpWebServerInitializer.DEFAULT_PORT;

    protected CloseableHttpResponse request(final String uri, final HttpMethod method, final String body, final Header... headers) throws IOException {
        final CloseableHttpClient client = HttpClients.createDefault();
        final Function<String, HttpUriRequest> requestProvider = switch (method) {
            case GET -> HttpGet::new;
            case HEAD -> HttpHead::new;
            case POST -> HttpPost::new;
            case PUT -> HttpPut::new;
            case DELETE -> HttpDelete::new;
            case OPTIONS -> HttpOptions::new;
            case TRACE -> HttpTrace::new;
            case PATCH -> HttpPatch::new;
            default -> throw new IllegalArgumentException("Method %s is unsupported".formatted(method));
        };
        final HttpUriRequest request = requestProvider.apply(ADDRESS + uri);
        if (body != null && request instanceof HttpEntityEnclosingRequest entityEnclosingRequest)
            entityEnclosingRequest.setEntity(new StringEntity(body));

        for (final Header header : headers) {
            request.addHeader(header);
        }

        return client.execute(request);
    }

}
