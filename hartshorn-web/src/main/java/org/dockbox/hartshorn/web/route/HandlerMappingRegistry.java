package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.web.HttpMethod;

import java.util.Map;

/**
 * Registry tracking {@link RequestHandler request handlers} for specific
 * {@link RouteMapping route mappings}. This registry is typically configured through
 * {@link HandlerMappingRegistrar registrars}, and is used by the web server to resolve incoming
 * requests to their corresponding handler.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMappingRegistry {

    /**
     * Registers the given {@link RequestHandler} for the given {@link RouteMapping}.
     *
     * @param mapping the request mapping to register the handler for
     * @param handler the request handler
     */
    void add(RouteMapping mapping, RequestHandler handler);

    /**
     * Returns all currently registered request handlers, identified by their corresponding route
     * mappings.
     *
     * @return all currently registered request handlers
     */
    Map<RouteMapping, RequestHandler> mappings();

    /**
     * Returns all currently registered request handlers for the given HTTP method, identified by
     * their corresponding path patterns.
     *
     * @param method the HTTP method to filter for
     * @return all currently registered request handlers for the given HTTP method
     */
    Map<String, RequestHandler> mappings(HttpMethod method);
}
