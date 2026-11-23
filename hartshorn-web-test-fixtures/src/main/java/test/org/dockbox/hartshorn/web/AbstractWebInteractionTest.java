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

package test.org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.HartshornAssertions;
import org.dockbox.hartshorn.test.HttpResponseAssert;
import org.dockbox.hartshorn.test.annotations.TestProperties;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.WebServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@UseWebServer
@HartshornIntegrationTest(includeBasePackages = false)
@TestProperties("hartshorn.web.logging.enabled=true")
public class AbstractWebInteractionTest {

    @Inject
    protected WebServer server;

    protected WebServer server() {
        return this.server;
    }

    protected URI getServerBaseUri() {
        return URI.create("http://0.0.0.0:%d/".formatted(this.server().port()));
    }

    protected URI getServerUri(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return URI.create(this.getServerBaseUri().toString() + path);
    }

    protected <T> HttpResponseAssert<T> assertRequest(
            Customizer<HttpRequest.Builder> customizer,
            HttpResponse.BodyHandler<T> handler
    ) throws IOException, InterruptedException {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest.Builder builder = HttpRequest.newBuilder();
            customizer.configure(builder);
            return HartshornAssertions.assertThat(client.send(builder.build(), handler));
        }
    }

    protected <T> HttpResponseAssert<T> assertRequest(
            String path,
            HttpResponse.BodyHandler<T> handler
    ) throws IOException, InterruptedException {
        return this.assertRequest(builder -> builder.uri(this.getServerUri(path)), handler);
    }

    protected <T> HttpResponseAssert<T> assertRequest(
            HttpMethod method,
            String path,
            HttpResponse.BodyHandler<T> handler
    ) throws IOException, InterruptedException {
        return this.assertRequest(
                builder -> builder.uri(this.getServerUri(path)).method(method.name(),
                        HttpRequest.BodyPublishers.noBody()
                ), handler);
    }
}
