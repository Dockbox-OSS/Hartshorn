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

package org.dockbox.hartshorn.web.message;

import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;

/**
 * A response writer that uses an {@link ObjectMapper} to serialize objects to the response body.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ObjectMapperResponseWriter implements ResponseWriter<Object> {

    private final ObjectMapper objectMapper;
    private final String contentType;

    public ObjectMapperResponseWriter(
            ObjectMapper objectMapper,
            String contentType
    ) {
        this.objectMapper = objectMapper;
        this.contentType = contentType;
    }

    @Override
    public void write(WebResponse response, Object body) throws Exception {
        response.headers().set("Content-Type", this.contentType);
        byte[] data = this.objectMapper.writeValueAsBytes(body);
        response.write(ByteBuffer.wrap(data));
    }
}
