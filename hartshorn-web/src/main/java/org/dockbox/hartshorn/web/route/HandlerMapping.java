package org.dockbox.hartshorn.web.route;

/**
 * A {@link RouteMapping} directly attached to a known {@link RequestHandler}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface HandlerMapping extends RouteMapping {

    /**
     * The {@link RequestHandler} attached to this mapping.
     *
     * @return the request handler
     */
    RequestHandler handler();
}
