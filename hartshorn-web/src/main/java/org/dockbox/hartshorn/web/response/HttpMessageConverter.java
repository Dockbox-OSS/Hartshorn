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

package org.dockbox.hartshorn.web.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Bi-directional converter for HTTP requests and responses. This interface allows reading content
 * from a {@link HttpServletRequest} and writing content to a {@link HttpServletResponse}. The type
 * of content that can be read and written is determined by the implementation, and may carry
 * additional constraints besides the type itself, such as required content types or annotations on
 * the target type.
 *
 * @param <T> the type of content that can be read and written by this converter
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HttpMessageConverter<T> {

    /**
     * Determines whether this converter can read content of the given type from the given request.
     *
     * @param type the target type to read content into
     * @param request the request to read content from
     *
     * @return {@code true} if this converter can read content of the given type from the given
     * request, {@code false} otherwise
     */
    boolean supportsRequest(Class<?> type, HttpServletRequest request);

    /**
     * Determines whether this converter can write content of the given type to the given response.
     *
     * @param type the type of the content to write
     * @param request the request associated with the response to write to
     * @param response the response to write to
     *
     * @return {@code true} if this converter can write content of the given type to the given
     * response, {@code false} otherwise
     */
    boolean supportsResponse(Class<?> type, HttpServletRequest request, HttpServletResponse response);

    /**
     * Attempts to read the content of the request to an instance of the given target type. If the
     * content is empty, an empty {@link Option} may be returned. If the content of the request is
     * incompatible with the given target type, it remains up to the implementation to decide
     * whether to throw an exception or to return an empty {@link Option}.
     *
     * @param type the target type
     * @param request the request to read content from
     *
     * @return an instance of the target type, or an empty {@link Option}
     *
     * @throws Exception if an error occurs while reading the content of the request, or if the
     * content is incompatible
     */
    Option<T> read(Class<? extends T> type, HttpServletRequest request) throws Exception;

    /**
     * Writes the given result to the response. The implementation is responsible for setting the
     * appropriate content type and status code, if necessary.
     *
     * @param response the response to write the result to
     * @param result the result to write to the response
     *
     * @throws Exception if an error occurs while writing the result to the response
     */
    void write(HttpServletResponse response, T result) throws Exception;
}
