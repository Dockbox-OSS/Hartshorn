/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.web;

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
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.testsuite.TestProperties;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.HttpWebServer;
import org.dockbox.hartshorn.web.annotations.UseHttpServer;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.util.function.Function;

import jakarta.inject.Inject;

@UseHttpServer
@HartshornTest(includeBasePackages = false)
@TestProperties("--hartshorn.web.port=0")
@TestComponents(TestController.class)
public abstract class RestIntegrationTest {

    @Inject
    private HttpWebServer server;
    protected static final String ADDRESS = "http://localhost:";

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
        final HttpUriRequest request = requestProvider.apply(ADDRESS + this.getPort() + uri);
        if (body != null && request instanceof HttpEntityEnclosingRequest entityEnclosingRequest)
            entityEnclosingRequest.setEntity(new StringEntity(body));

        for (final Header header : headers) {
            request.addHeader(header);
        }

        return client.execute(request);
    }

    protected int getPort() {
        return this.server.port();
    }

    @AfterEach
    public void tearDown() throws ApplicationException {
        // As the runtime doesn't exit when the test is finished due to Jetty remaining active in test
        // environments, we need to manually stop the server. Instead of directly stopping the application,
        // we only stop the server. This way the application shutdown hook is still executed without side
        // effects.
        this.server.stop();
    }

}
