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

package test.org.dockbox.hartshorn.web.route.rules;

import jakarta.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderContext;
import org.dockbox.hartshorn.util.introspect.util.ParameterLoaderRule;
import org.dockbox.hartshorn.util.introspect.util.RuleBasedParameterLoader;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.web.Header;
import org.dockbox.hartshorn.web.message.RequestAttributes;
import org.dockbox.hartshorn.web.PathParameter;
import org.dockbox.hartshorn.web.QueryParameter;
import org.dockbox.hartshorn.web.route.rules.HeaderValueParameterLoaderRule;
import org.dockbox.hartshorn.web.route.rules.PathParameterValueParameterLoaderRule;
import org.dockbox.hartshorn.web.route.rules.QueryParameterValueParameterLoaderRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@HartshornIntegrationTest(includeBasePackages = false)
public class ParameterLoaderTests {

    private static final String DEFAULT_KEY = "Message";
    private static final String DEFAULT_VALUE = "Hello World";

    @Inject
    private Introspector introspector;

    @Inject
    private ConversionService conversionService;

    @Test
    void presentPathParametersCanBeLoaded() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute(
                Mockito.eq(RequestAttributes.REQUEST_PATH_PARAMETERS)
        )).thenReturn(Map.of(DEFAULT_KEY, DEFAULT_VALUE));

        var rule = new PathParameterValueParameterLoaderRule<>(request, conversionService);
        Object argument = attemptLoadParameter("pathParameterConsumer", rule);
        Assertions.assertThat(argument).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void absentPathParametersResultInError() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getAttribute(
                Mockito.eq(RequestAttributes.REQUEST_PATH_PARAMETERS)
        )).thenReturn(Map.of());

        var rule = new PathParameterValueParameterLoaderRule<>(request, conversionService);
        Assertions.assertThatCode(() -> {
                    attemptLoadParameter("pathParameterConsumer", rule);
                }).isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Path parameters do not support default values.");
    }

    @Test
    void presentHeaderValueCanBeLoaded() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaders(DEFAULT_KEY)).thenReturn(
                Collections.enumeration(List.of(DEFAULT_VALUE))
        );

        var rule = new HeaderValueParameterLoaderRule<>(request, conversionService);
        Object argument = attemptLoadParameter("headerConsumer", rule);
        Assertions.assertThat(argument).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void absentHeaderValueResultsInEmptyValue() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeaders(DEFAULT_KEY)).thenReturn(Collections.emptyEnumeration());

        var rule = new HeaderValueParameterLoaderRule<>(request, conversionService);
        Object argument = attemptLoadParameter("headerConsumer", rule);
        Assertions.assertThat(argument).isEqualTo("");
    }

    @Test
    void presentQueryValueCanBeLoaded() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameterValues(DEFAULT_KEY))
                .thenReturn(new String[]{DEFAULT_VALUE});

        var rule = new QueryParameterValueParameterLoaderRule<>(request, conversionService);
        Object argument = attemptLoadParameter("queryConsumer", rule);
        Assertions.assertThat(argument).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void absentQueryValueResultsInEmptyValue() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameterValues(DEFAULT_KEY)).thenReturn(new String[0]);

        var rule = new QueryParameterValueParameterLoaderRule<>(request, conversionService);
        Object argument = attemptLoadParameter("queryConsumer", rule);
        Assertions.assertThat(argument).isEqualTo("");
    }

    private Object attemptLoadParameter(
            String methodName,
            ParameterLoaderRule<ParameterLoaderContext> rule
    ) {
        MethodView<ParameterLoaderTests, ?> method = introspector.introspect(this).methods()
                .named(methodName, String.class)
                .get();

        var loader = new RuleBasedParameterLoader<>(ParameterLoaderContext.class);
        loader.add(rule);

        ParameterLoaderContext context = new ParameterLoaderContext(method, this);
        return loader.loadArgument(context, 0);
    }

    void pathParameterConsumer(@PathParameter(DEFAULT_KEY) String name) {
    }

    void headerConsumer(@Header(DEFAULT_KEY) String name) {
    }

    void queryConsumer(@QueryParameter(DEFAULT_KEY) String name) {
    }
}
