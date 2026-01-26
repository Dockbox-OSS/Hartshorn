package org.dockbox.hartshorn.web.message;

import tools.jackson.databind.ObjectMapper;

import java.nio.ByteBuffer;

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
