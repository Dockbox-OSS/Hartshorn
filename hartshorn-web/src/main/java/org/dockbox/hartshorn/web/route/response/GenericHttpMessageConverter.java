package org.dockbox.hartshorn.web.route.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Bi-directional converter for HTTP requests and responses. This interface allows reading content
 * from a {@link HttpServletRequest} and writing content to a {@link HttpServletResponse}. Unlike
 * {@link HttpMessageConverter}, this interface is not type-constrained, and therefore may use e.g.
 * generic DTO translation.
 *
 * @see HttpMessageConverter
 * @see ResponseHandler
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface GenericHttpMessageConverter {

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
     * content is incompatible with the target type.
     */
    Option<?> read(Class<?> type, HttpServletRequest request) throws Exception;

    /**
     * Writes the given result to the response. The implementation is responsible for setting the
     * appropriate content type and status code, if necessary.
     *
     * @param response the response to write the result to
     * @param result the result to write to the response
     *
     * @throws Exception if an error occurs while writing the result to the response
     */
    void write(HttpServletResponse response, Object result) throws Exception;
}
