package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.MediaType;
import tools.jackson.databind.ObjectMapper;

/**
 * An implementation of {@link GenericHttpMessageConverter} that uses Jackson's {@link ObjectMapper}
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
public class JacksonHttpMessageConverter implements GenericHttpMessageConverter {

    private final ObjectMapper objectMapper;
    private final MediaType contentType;

    public JacksonHttpMessageConverter(ObjectMapper objectMapper, MediaType contentType) {
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
