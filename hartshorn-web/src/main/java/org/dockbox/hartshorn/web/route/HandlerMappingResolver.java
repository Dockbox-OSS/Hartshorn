package org.dockbox.hartshorn.web.route;

import jakarta.servlet.http.HttpServletRequest;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.web.HttpMethod;

/**
 * Resolver to look up {@link HandlerMapping handler mappings}, typically from a
 * {@link HandlerMappingRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMappingResolver {

    /**
     * Resolves a handler mapping for the given HTTP method and path. The implementation may use
     * the request to resolve the handler mapping, typically only using the {@link HttpMethod} and
     * path, but it is not limited to these. If no handler mapping can be resolved, an empty
     * {@link Option} should be returned.
     *
     * @param method the HTTP method of the request
     * @param path the path of the request
     * @param request the HTTP request, which may be used to resolve the handler mapping
     *
     * @return an {@link Option} containing the resolved handler mapping, or an empty {@link Option}
     * if no handler mapping could be resolved
     */
    Option<HandlerMapping> resolve(HttpMethod method, String path, HttpServletRequest request);
}
