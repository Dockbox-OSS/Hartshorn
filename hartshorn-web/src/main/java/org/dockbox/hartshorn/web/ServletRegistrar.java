package org.dockbox.hartshorn.web;

import jakarta.servlet.Servlet;
import org.dockbox.hartshorn.web.spec.PathSpec;

/**
 * A registrar for servlets, allowing for the manual registration of servlets outside of standard
 * routing.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ServletRegistrar {

    /**
     * Registers a servlet with the given path specification.
     *
     * @param pathSpec the path specification for the servlet
     * @param servlet the servlet to register
     */
    void register(PathSpec pathSpec, Servlet servlet);
}
