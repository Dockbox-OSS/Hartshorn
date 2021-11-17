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

import org.dockbox.hartshorn.core.context.element.ExecutableElementContext;
import org.dockbox.hartshorn.core.context.element.ParameterContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.persistence.FileFormats;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.dockbox.hartshorn.web.annotations.RequestHeader;
import org.dockbox.hartshorn.web.annotations.http.HttpRequest;
import org.dockbox.hartshorn.web.processing.rules.BodyRequestParameterRule;
import org.dockbox.hartshorn.web.processing.rules.HeaderRequestParameterRule;
import org.dockbox.hartshorn.web.processing.HttpRequestParameterLoaderContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

public class RequestArgumentProcessorTests extends ApplicationAwareTest {

    public static Stream<Arguments> bodies() {
        return Stream.of(
                Arguments.of("{\"message\":\"Hello world!\"}", FileFormats.JSON),
                Arguments.of("message: 'Hello world!'", FileFormats.YAML),
                Arguments.of("<message>Hello world!</message>", FileFormats.XML),
                Arguments.of("message='Hello world!'", FileFormats.TOML),
                Arguments.of("message=Hello world!", FileFormats.PROPERTIES)
        );
    }

    @Test
    void testBodyIsNotParsedIfString() throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader("Unparsed body"));
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getReader()).thenReturn(reader);
        final ParameterContext<String> context = Mockito.mock(ParameterContext.class);
        Mockito.when(context.type()).thenReturn(TypeContext.of(String.class));

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.context(), request, null);

        final Exceptional<String> result = new BodyRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Unparsed body", result.get());
    }

    @ParameterizedTest
    @MethodSource("bodies")
    void testBodyIsParsedForAllFormats(final String body, final FileFormats fileFormat) throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader(body));
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getReader()).thenReturn(reader);

        final ParameterContext<Message> context = Mockito.mock(ParameterContext.class);
        Mockito.when(context.type()).thenReturn(TypeContext.of(Message.class));

        final ExecutableElementContext<?> declaring = Mockito.mock(ExecutableElementContext.class);
        final HttpRequest httpRequest = Mockito.mock(HttpRequest.class);
        Mockito.when(httpRequest.bodyFormat()).thenReturn(fileFormat);
        Mockito.when(declaring.annotation(HttpRequest.class)).thenReturn(Exceptional.of(httpRequest));
        // Different order due to generic return type
        Mockito.doReturn(declaring).when(context).declaredBy();

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.context(), request, null);

        final Exceptional<Message> result = new BodyRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Hello world!", result.get().message());
    }

    @Test
    void testStringHeadersCanBeProcessed() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader("string-header")).thenReturn("Header!");
        Mockito.when(request.getHeaders("string-header")).thenReturn(Collections.enumeration(List.of("Header!")));
        final ParameterContext<String> context = Mockito.mock(ParameterContext.class);
        Mockito.when(context.type()).thenReturn(TypeContext.of(String.class));
        final RequestHeader requestHeader = Mockito.mock(RequestHeader.class);
        Mockito.when(requestHeader.value()).thenReturn("string-header");
        Mockito.when(context.annotation(RequestHeader.class)).thenReturn(Exceptional.of(requestHeader));

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.context(), request, null);

        final Exceptional<String> result = new HeaderRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals("Header!", result.get());
    }

    @Test
    void testIntHeadersCanBeProcessed() {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getIntHeader("int-header")).thenReturn(1);
        Mockito.when(request.getHeaders("int-header")).thenReturn(Collections.enumeration(List.of("Header!")));
        final ParameterContext<Integer> context = Mockito.mock(ParameterContext.class);
        Mockito.when(context.type()).thenReturn(TypeContext.of(Integer.class));
        final RequestHeader requestHeader = Mockito.mock(RequestHeader.class);
        Mockito.when(requestHeader.value()).thenReturn("int-header");
        Mockito.when(context.annotation(RequestHeader.class)).thenReturn(Exceptional.of(requestHeader));

        final HttpRequestParameterLoaderContext loaderContext = new HttpRequestParameterLoaderContext(null, null, null, this.context(), request, null);

        final Exceptional<Integer> result = new HeaderRequestParameterRule().load(context, 0, loaderContext);
        Assertions.assertTrue(result.present());
        Assertions.assertEquals(1, result.get().intValue());
    }
}
