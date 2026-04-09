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

package test.org.dockbox.hartshorn.web.jetty;

import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.UseWebServer;
import org.dockbox.hartshorn.web.WebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@UseWebServer
@HartshornIntegrationTest(includeBasePackages = false)
public class MainTests {

    @Test
    @TestComponents(TestRouter.class)
    void sampleAssertion(@Inject WebServer server) throws IOException, InterruptedException {
        int port = server.port();
        String response;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + port + "/hello"))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        }
        Assertions.assertThat(response).isEqualTo("hello");
    }

    @Router
    public static class TestRouter {

        @GetRoute("/hello")
        public String hello() {
            return "hello";
        }
    }
}
