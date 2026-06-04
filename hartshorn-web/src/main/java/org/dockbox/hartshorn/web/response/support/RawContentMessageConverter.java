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

package org.dockbox.hartshorn.web.response.support;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * A {@link HttpMessageConverter} which allows writing raw content to the response, without any
 * additional processing. This converter does not assume any specific content type.
 *
 * @author Guus Lieben
 *
 * @since 0.7.0
 */
public class RawContentMessageConverter implements HttpMessageConverter<Object> {

    @Override
    public boolean supportsRequest(Class<?> type, HttpServletRequest request) {
        return false;
    }

    @Override
    public boolean supportsResponse(
            Class<?> type,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return type == String.class || type == ByteBuffer.class || type == byte[].class;
    }

    @Override
    public Option<Object> read(Class<?> type, HttpServletRequest request) {
        return Option.empty();
    }

    @Override
    public void write(HttpServletResponse response, Object result) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        switch (result) {
            case String string -> outputStream.write(string.getBytes());
            case ByteBuffer byteBuffer -> outputStream.write(byteBuffer);
            case byte[] bytes -> outputStream.write(bytes);
            default -> {
                throw new IllegalArgumentException("Unsupported content type: %s".formatted(
                        result.getClass()
                ));
            }
        }
    }
}
