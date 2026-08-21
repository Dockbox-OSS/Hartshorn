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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MediaType;
import org.dockbox.hartshorn.web.MediaTypes;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;
import tools.jackson.databind.ObjectMapper;

/**
 * An implementation of {@link HttpMessageConverter} that uses Jackson's {@link ObjectMapper}
 * to read and write content. Depending on the implementation of the {@link ObjectMapper}, this
 * converter may support a wide range of content types, such as JSON, XML, YAML, etc.
 *
 * <p>This implementation does not look at the {@code Content-Type} header, and therefore may
 * produce unexpected results if the content type of the request is incompatible with the configured
 * {@link ObjectMapper}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class JacksonHttpMessageConverter implements HttpMessageConverter<Object> {

    private final ObjectMapper objectMapper;
    private final MediaType contentType;

    public JacksonHttpMessageConverter(ObjectMapper objectMapper, MediaType contentType) {
        this.objectMapper = objectMapper;
        this.contentType = contentType;
    }

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
        String contentType = response.getContentType();
        if (contentType == null) {
            return true;
        }
        MediaType mediaType = MediaTypes.parse(contentType);
        return this.contentType.isCompatibleWith(mediaType);
    }

    @Override
    public Option<Object> read(Class<?> type, HttpServletRequest request) throws Exception {
        return Option.of(objectMapper.readValue(request.getInputStream(), type));
    }

    @Override
    public void write(HttpServletResponse response, Object result) throws Exception {
        this.objectMapper.writeValue(response.getOutputStream(), result);
        response.setContentType(this.contentType.toString());
    }
}
