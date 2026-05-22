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
import org.dockbox.hartshorn.web.HttpStatus;
import org.dockbox.hartshorn.web.response.HttpMessageConverter;

/**
 * An {@link HttpMessageConverter} which does not support reading any content, and only supports
 * writing content of type {@code null}, which is written as an empty response with status code
 * {@link HttpStatus#NO_CONTENT 204 No Content}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class NoContentMessageConverter implements HttpMessageConverter<Object> {

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
        return type == null;
    }

    @Override
    public Option<Object> read(Class<?> type, HttpServletRequest request) {
        return Option.empty();
    }

    @Override
    public void write(HttpServletResponse response, Object result) {
        response.setStatus(HttpStatus.NO_CONTENT.code());
    }
}
