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

package test.org.dockbox.hartshorn.web.response;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.assertj.core.api.Assertions;
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;
import org.dockbox.hartshorn.web.response.SimpleResponseHandler;
import org.dockbox.hartshorn.web.response.support.JacksonHttpMessageConverter;
import org.dockbox.hartshorn.web.response.support.NoContentMessageConverter;
import org.dockbox.hartshorn.web.response.support.RawContentMessageConverter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseHandlerTests {

    @Test
    void singleCompatibleConverterWritesResult() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Object result = new Object();
        HttpMessageConverter<Object> converter = converter(true);
        HttpMessageConverter<Object> incompatibleConverter = converter(false);
        HttpMessageConverter<Object> fallbackConverter = converter(false);
        SimpleResponseHandler handler = new SimpleResponseHandler(
                fallbackConverter,
                List.of(converter, incompatibleConverter)
        );

        handler.handleResponse(request, response, result);

        Mockito.verify(converter).supportsResponse(Object.class, request, response);
        Mockito.verify(converter).write(response, result);
        Mockito.verify(incompatibleConverter).supportsResponse(Object.class, request, response);
        Mockito.verifyNoInteractions(fallbackConverter);
    }

    @Test
    void fallbackConverterIsUsedWhenNoConfiguredConverterMatches() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        String result = "fallback";
        HttpMessageConverter<Object> configuredConverter = converter(false);
        HttpMessageConverter<Object> fallbackConverter = converter(true);
        SimpleResponseHandler handler = new SimpleResponseHandler(
                fallbackConverter,
                List.of(configuredConverter)
        );

        handler.handleResponse(request, response, result);

        Mockito.verify(configuredConverter).supportsResponse(String.class, request, response);
        Mockito.verify(fallbackConverter).supportsResponse(String.class, request, response);
        Mockito.verify(fallbackConverter).write(response, result);
    }

    @Test
    void incompatibleFallbackIsReported() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpMessageConverter<Object> configuredConverter = converter(false);
        HttpMessageConverter<Object> fallbackConverter = converter(false);
        SimpleResponseHandler handler = new SimpleResponseHandler(
                fallbackConverter,
                List.of(configuredConverter)
        );

        Assertions.assertThatThrownBy(() -> handler.handleResponse(request, response, "result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Fallback converter is not compatible")
                .hasMessageContaining(String.class.getName());

        Mockito.verify(configuredConverter).supportsResponse(String.class, request, response);
        Mockito.verify(fallbackConverter).supportsResponse(String.class, request, response);
        Mockito.verify(configuredConverter, Mockito.never()).write(response, "result");
        Mockito.verify(fallbackConverter, Mockito.never()).write(response, "result");
    }

    @Test
    void multipleCompatibleConvertersAreRejected() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        HttpMessageConverter<Object> firstConverter = converter(true);
        HttpMessageConverter<Object> secondConverter = converter(true);
        HttpMessageConverter<Object> fallbackConverter = converter(true);
        SimpleResponseHandler handler = new SimpleResponseHandler(
                fallbackConverter,
                List.of(firstConverter, secondConverter)
        );

        Assertions.assertThatThrownBy(() -> handler.handleResponse(request, response, "result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Multiple compatible converters")
                .hasMessageContaining(String.class.getName());

        Mockito.verify(firstConverter).supportsResponse(String.class, request, response);
        Mockito.verify(secondConverter).supportsResponse(String.class, request, response);
        Mockito.verifyNoInteractions(fallbackConverter);
    }

    @Test
    void converterWriteFailureIsPropagated() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        IOException failure = new IOException("failed to write response");
        HttpMessageConverter<Object> converter = converter(true);
        Mockito.doThrow(failure).when(converter).write(response, "result");
        SimpleResponseHandler handler = new SimpleResponseHandler(converter(false), List.of(converter));

        Assertions.assertThatThrownBy(() -> handler.handleResponse(request, response, "result"))
                .isSameAs(failure);
    }

    @Test
    void nullResultUsesNoContentConverter() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        SimpleResponseHandler handler = new SimpleResponseHandler(
                new NoContentMessageConverter(),
                List.of(new RawContentMessageConverter())
        );

        handler.handleResponse(request, response, null);

        Mockito.verify(response).setStatus(HttpStatus.NO_CONTENT.code());
    }

    @Test
    void rawStringIsWrittenAsBytes() throws Exception {
        CaptureServletOutputStream outputStream = new CaptureServletOutputStream();
        HttpServletResponse response = response(outputStream);
        SimpleResponseHandler handler = rawContentHandler();

        handler.handleResponse(request(), response, "raw response");

        assertThat(outputStream.bytes())
                .isEqualTo("raw response".getBytes());
    }

    @Test
    void rawByteArrayIsWrittenUnchanged() throws Exception {
        byte[] content = { 1, 2, 3 };
        CaptureServletOutputStream outputStream = new CaptureServletOutputStream();
        SimpleResponseHandler handler = rawContentHandler();

        handler.handleResponse(request(), response(outputStream), content);

        assertThat(outputStream.bytes()).isEqualTo(content);
    }

    @Test
    void rawByteBufferWritesOnlyRemainingBytes() throws Exception {
        ByteBuffer content = ByteBuffer.wrap(new byte[] { 0, 1, 2, 0 }, 1, 2);
        CaptureServletOutputStream outputStream = new CaptureServletOutputStream();
        SimpleResponseHandler handler = rawContentHandler();

        handler.handleResponse(request(), response(outputStream), content);

        assertThat(outputStream.bytes()).isEqualTo(new byte[] { 1, 2 });
        assertThat(content.position()).isEqualTo(3);
    }

    @Test
    void jacksonConverterWritesJsonAndContentType() throws Exception {
        CaptureServletOutputStream outputStream = new CaptureServletOutputStream();
        HttpServletResponse response = response(outputStream);
        JacksonHttpMessageConverter converter = new JacksonHttpMessageConverter(
                JsonMapper.builder().build(),
                new MediaType("application", "json")
        );
        SimpleResponseHandler handler = new SimpleResponseHandler(
                new NoContentMessageConverter(),
                List.of(converter)
        );

        handler.handleResponse(request(), response, Map.of("message", "hello"));

        assertThat(outputStream.bytes())
                .isEqualTo("{\"message\":\"hello\"}".getBytes(StandardCharsets.UTF_8));
        Mockito.verify(response).setContentType("application/json");
    }

    private static HttpMessageConverter<Object> converter(boolean supportsResponse) {
        HttpMessageConverter<Object> converter = Mockito.mock(HttpMessageConverter.class);
        Mockito.when(converter.supportsResponse(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(supportsResponse);
        return converter;
    }

    private static SimpleResponseHandler rawContentHandler() {
        return new SimpleResponseHandler(
                new NoContentMessageConverter(),
                List.of(new RawContentMessageConverter())
        );
    }

    private static HttpServletRequest request() {
        return Mockito.mock(HttpServletRequest.class);
    }

    private static HttpServletResponse response(CaptureServletOutputStream outputStream)
            throws IOException {
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getOutputStream()).thenReturn(outputStream);
        return response;
    }

    private static class CaptureServletOutputStream extends ServletOutputStream {

        private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }

        @Override
        public void write(int value) {
            this.outputStream.write(value);
        }

        public byte[] bytes() {
            return this.outputStream.toByteArray();
        }
    }
}