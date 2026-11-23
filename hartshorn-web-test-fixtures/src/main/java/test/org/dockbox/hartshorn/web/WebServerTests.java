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
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.web.GetRoute;
import org.dockbox.hartshorn.web.Router;
import org.dockbox.hartshorn.web.WebServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class WebServerTests extends AbstractWebInteractionTest {

    protected abstract Class<? extends WebServer> expectedInstanceType();

    @Test
    @TestComponents(TestRouter.class)
    void testBasicRequestResponseFlow(
            @Inject WebServer server
    ) throws IOException, InterruptedException {
        assertThat(server).isInstanceOf(this.expectedInstanceType());
        assertRequest("hello", HttpResponse.BodyHandlers.ofString()).hasBody("hello");
    }

    @Router
    public static class TestRouter {

        @GetRoute("/hello")
        public String hello() {
            return "hello";
        }
    }
}
