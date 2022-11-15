/*
 * Copyright 2019-2022 the original author or authors.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@HartshornTest(includeBasePackages = false)
@UsePersistence
public class RequestArgumentProcessorTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> bodies() {
        return Stream.of(
                Arguments.of("{\"message\":\"Hello world!\"}", MediaType.APPLICATION_JSON),
                Arguments.of("message: 'Hello world!'", MediaType.APPLICATION_YAML),
                Arguments.of("<message>Hello world!</message>", MediaType.APPLICATION_XML),
                Arguments.of("message='Hello world!'", MediaType.APPLICATION_TOML)
        );
    }

    @Test
    void testBodyIsNotParsedIfString() throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader("Unparsed body"));
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getReader()).thenReturn(reader);

        final ParameterView<String> parameter = Mockito.mock(ParameterView.class);
        Mockito.when(parameter.type()).thenReturn(this.applicationContext.environment().introspect(String.class));

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.applicationContext, request, null);

        final Option<String> result = new BodyRequestParameterRule().load(parameter, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Unparsed body", result.get());
    }

    @ParameterizedTest
    @MethodSource("bodies")
    void testBodyIsParsedForAllFormats(final String body, final MediaType mediaType) throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader(body));
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getReader()).thenReturn(reader);

        final ParameterView<Message> context = Mockito.mock(ParameterView.class);
        Mockito.when(context.type()).thenReturn(this.applicationContext.environment().introspect(Message.class));

        final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(httpRequest.consumes()).thenReturn(mediaType);

        final ElementAnnotationsIntrospector annotationsIntrospector = Mockito.mock(ElementAnnotationsIntrospector.class);
        Mockito.when(annotationsIntrospector.get(HttpRequest.class)).thenReturn(Option.of(httpRequest));

        final ExecutableElementView<?> element = Mockito.mock(ExecutableElementView.class);
        Mockito.when(element.annotations()).thenReturn(annotationsIntrospector);

        // Different order due to generic return type
        Mockito.doReturn(element).when(context).declaredBy();

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.applicationContext, request, null);

        final Option<Message> result = new BodyRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Hello world!", result.get().message());
    }

    @Test
    void testStringHeadersCanBeProcessed() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("string-header")).thenReturn("Header!");
        Mockito.when(request.getHeaders("string-header")).thenReturn(Collections.enumeration(List.of("Header!")));

        final RequestHeader requestHeader = Mockito.mock(RequestHeader.class);
        Mockito.when(requestHeader.value()).thenReturn("string-header");

        final ElementAnnotationsIntrospector annotationsIntrospector = Mockito.mock(ElementAnnotationsIntrospector.class);
        Mockito.when(annotationsIntrospector.get(RequestHeader.class)).thenReturn(Option.of(requestHeader));

        final ParameterView<String> context = Mockito.mock(ParameterView.class);
        Mockito.when(context.type()).thenReturn(this.applicationContext.environment().introspect(String.class));
        Mockito.when(context.annotations()).thenReturn(annotationsIntrospector);

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.applicationContext, request, null);

        final Option<String> result = new HeaderRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Header!", result.get());
    }

    @Test
    void testIntHeadersCanBeProcessed() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getIntHeader("int-header")).thenReturn(1);
        Mockito.when(request.getHeaders("int-header")).thenReturn(Collections.enumeration(List.of("Header!")));

        final RequestHeader requestHeader = Mockito.mock(RequestHeader.class);
        Mockito.when(requestHeader.value()).thenReturn("int-header");

        final ElementAnnotationsIntrospector annotationsIntrospector = Mockito.mock(ElementAnnotationsIntrospector.class);
        Mockito.when(annotationsIntrospector.get(RequestHeader.class)).thenReturn(Option.of(requestHeader));

        final ParameterView<Integer> parameter = Mockito.mock(ParameterView.class);
        Mockito.when(parameter.type()).thenReturn(this.applicationContext.environment().introspect(Integer.class));
        Mockito.when(parameter.annotations()).thenReturn(annotationsIntrospector);

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.applicationContext, request, null);

        final Option<Integer> result = new HeaderRequestParameterRule().load(parameter, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals(1, result.get().intValue());
    }
}
