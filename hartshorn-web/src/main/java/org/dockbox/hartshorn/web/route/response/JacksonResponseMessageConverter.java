package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MimeType;
import tools.jackson.databind.ObjectMapper;

public class JacksonResponseMessageConverter implements GlobalResponseMessageConverter {

    private final ObjectMapper objectMapper;
    private final MimeType contentType;

    public JacksonResponseMessageConverter(ObjectMapper objectMapper, MimeType contentType) {
        this.objectMapper = objectMapper;
        this.contentType = contentType;
    }

    @Override
    public Option<?> read(Class<?> type, HttpServletRequest request) throws Exception {
        return Option.of(objectMapper.readValue(request.getInputStream(), type));
    }

    @Override
    public void write(HttpServletResponse response, Object result) throws Exception {
        this.objectMapper.writeValue(response.getOutputStream(), result);
        response.setContentType(this.contentType.toString());
    }
}
