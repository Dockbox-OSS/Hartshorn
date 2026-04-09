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

package test.org.dockbox.hartshorn.web.route;

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.HartshornAssertions;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;
import org.dockbox.hartshorn.web.message.RequestAttributes;
import org.dockbox.hartshorn.web.route.HandlerMapping;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.HandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.RequestHandler;
import org.dockbox.hartshorn.web.route.RouteMapping;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistrar;
import org.dockbox.hartshorn.web.route.SimpleHandlerMappingRegistry;
import org.dockbox.hartshorn.web.route.support.PatternMatchingHandlerMappingResolver;
import org.dockbox.hartshorn.web.route.support.StandardPathPatternMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import java.util.Map;

@HartshornIntegrationTest(includeBasePackages = false)
public class HandlerMappingResolverTests {

    private static final RequestHandler REQUEST_HANDLER = (req, res) -> {};

    @Inject
    private Logger logger;

    private HttpServletRequest request;

    @BeforeEach
    public void setup() {
        this.request = Mockito.spy(HttpServletRequest.class);
    }

    @Test
    void mappingForNonParameterizedMappingCanBeResolved() {
        Option<HandlerMapping> mapping = attemptMappingResolution("/api/hello", "/api/hello");
        HartshornAssertions.assertThat(mapping)
                .present()
                .value()
                .extracting(HandlerMapping::handler)
                .isSameAs(REQUEST_HANDLER);
        verifyAttributes(Map.of());
    }

    @Test
    void mappingForParameterizedMappingCanBeResolved() {
        Option<HandlerMapping> mapping = attemptMappingResolution("/api/{name}", "/api/guus");
        HartshornAssertions.assertThat(mapping)
                .present()
                .value()
                .extracting(HandlerMapping::handler)
                .isSameAs(REQUEST_HANDLER);
        verifyAttributes(Map.of("name", "guus"));
    }

    @Test
    void mappingForAbsentMappingCannotBeResolved() {
        Option<HandlerMapping> mapping = attemptMappingResolution("/api/hello", "/api/goodbye");
        HartshornAssertions.assertThat(mapping).absent();
        Mockito.verify(request, Mockito.never()).setAttribute(
                Mockito.anyString(),
                Mockito.anyMap()
        );
    }

    private void verifyAttributes(Map<String, String> reference) {
        Mockito.verify(request, Mockito.times(1))
                .setAttribute(
                        Mockito.eq(RequestAttributes.REQUEST_PATH_PARAMETERS),
                        Mockito.argThat(value -> {
                    if (value instanceof Map<?, ?> params && params.size() == reference.size()) {
                        for (String key : reference.keySet()) {
                            if (!reference.get(key).equals(params.get(key))) {
                                return false;
                            }
                        }
                        return true;
                    }
                    return false;
                }));
    }

    Option<HandlerMapping> attemptMappingResolution(String pattern, String path) {
        HandlerMappingRegistry registry = new SimpleHandlerMappingRegistry();
        HandlerMappingRegistrar registrar = new SimpleHandlerMappingRegistrar(registry, logger);
        registrar.add(RouteMapping.of(HttpMethod.GET, pattern), REQUEST_HANDLER);

        PatternMatchingHandlerMappingResolver resolver = new PatternMatchingHandlerMappingResolver(
                new StandardPathPatternMatcher(),
                registry
        );
        return resolver.resolve(HttpMethod.GET, path, request);
    }
}
