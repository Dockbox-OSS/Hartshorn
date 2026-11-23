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

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.ServletRegistrar;
import org.dockbox.hartshorn.web.WebServerConfiguration;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.support.RequestRoutingServlet;
import org.dockbox.hartshorn.web.spec.PathSpec;
import org.dockbox.hartshorn.web.spec.parser.PathParser;
import org.dockbox.hartshorn.web.support.servlet.StaticResourceServlet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServletRegistrationTests {

    @Test
    void emptyRouteRegistryDoesNotRegisterServlets() {
        WebServerConfiguration configuration = new WebServerConfiguration();
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        Map<PathSpec, Servlet> registrations = new HashMap<>();

        configuration.contextCustomizer(registry).configure(registrations::put);

        Assertions.assertThat(registrations).isEmpty();
    }

    @Test
    void routeRegistryRegistersOneServletPerPathAndDispatchesMethods() throws Exception {
        WebServerConfiguration configuration = new WebServerConfiguration();
        PathParser pathParser = configuration.pathParser();
        PathSpec sharedPath = pathParser.parse("/api/test");
        PathSpec otherPath = pathParser.parse("/api/other");
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        RequestHandler getHandler = Mockito.mock(RequestHandler.class);
        RequestHandler postHandler = Mockito.mock(RequestHandler.class);
        RequestHandler otherHandler = Mockito.mock(RequestHandler.class);
        registry.add(HttpMethod.GET, sharedPath, getHandler);
        registry.add(HttpMethod.POST, sharedPath, postHandler);
        registry.add(HttpMethod.GET, otherPath, otherHandler);
        Map<PathSpec, Servlet> registrations = new HashMap<>();

        configuration.contextCustomizer(registry).configure(registrations::put);

        Assertions.assertThat(registrations).hasSize(2);
        Assertions.assertThat(registrations.get(sharedPath))
                .isInstanceOf(RequestRoutingServlet.class);
        Assertions.assertThat(registrations.get(otherPath))
                .isInstanceOf(RequestRoutingServlet.class);

        HttpServletRequest getRequest = request("GET", "/api/test");
        HttpServletResponse getResponse = Mockito.mock(HttpServletResponse.class);
        registrations.get(sharedPath).service(getRequest, getResponse);
        Mockito.verify(getHandler).handle(getRequest, getResponse);

        HttpServletRequest postRequest = request("POST", "/api/test");
        HttpServletResponse postResponse = Mockito.mock(HttpServletResponse.class);
        registrations.get(sharedPath).service(postRequest, postResponse);
        Mockito.verify(postHandler).handle(postRequest, postResponse);

        HttpServletRequest otherRequest = request("GET", "/api/other");
        HttpServletResponse otherResponse = Mockito.mock(HttpServletResponse.class);
        registrations.get(otherPath).service(otherRequest, otherResponse);
        Mockito.verify(otherHandler).handle(otherRequest, otherResponse);
    }

    @Test
    void staticServletRegistrationAddsWildcardAndUsesConfiguredLocation() throws Exception {
        WebServerConfiguration configuration = new WebServerConfiguration();
        ResourceLookup resourceLookup = Mockito.mock(ResourceLookup.class);
        Mockito.when(resourceLookup.lookup("classpath:public/hello.txt")).thenReturn(Set.of());
        Map<PathSpec, Servlet> registrations = new HashMap<>();

        Customizer<ServletRegistrar> customizer = configuration.contextStaticPathCustomizer(
                resourceLookup,
                "/assets",
                configuration.pathParser(),
                "classpath:public/"
        );
        customizer.configure(registrations::put);

        Assertions.assertThat(registrations).hasSize(1);
        PathSpec pathSpec = registrations.keySet().iterator().next();
        Assertions.assertThat(pathSpec.pattern()).isEqualTo("assets/*");
        Servlet servlet = registrations.get(pathSpec);
        Assertions.assertThat(servlet).isInstanceOf(StaticResourceServlet.class);

        HttpServletRequest request = request("GET", "/assets");
        Mockito.when(request.getPathInfo()).thenReturn("/hello.txt");
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        servlet.service(request, response);

        Mockito.verify(resourceLookup).lookup("classpath:public/hello.txt");
        Mockito.verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void staticPathWithWildcardIsNotExpandedTwice() {
        WebServerConfiguration configuration = new WebServerConfiguration();
        Map<PathSpec, Servlet> registrations = new HashMap<>();

        configuration.contextStaticPathCustomizer(
                path -> Set.of(),
                "/assets/*",
                configuration.pathParser(),
                "classpath:public"
        ).configure(registrations::put);

        Assertions.assertThat(registrations.keySet())
                .singleElement()
                .extracting(PathSpec::pattern)
                .isEqualTo("assets/*");
    }

    private static HttpServletRequest request(String method, String path) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn(method);
        Mockito.when(request.getServletPath()).thenReturn(path);
        return request;
    }
}