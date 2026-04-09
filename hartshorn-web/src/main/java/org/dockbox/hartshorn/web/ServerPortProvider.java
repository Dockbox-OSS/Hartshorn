package org.dockbox.hartshorn.web;

/**
 * Provides the port number on which the web server should run. This interface can be implemented
 * to allow for dynamic port configuration, such as reading from environment variables or
 * configuration files.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ServerPortProvider {

    /**
     * Constant indicating that the port selection should remain dynamic, allowing the web server to
     * select an available port at runtime.
     */
    int DYNAMIC_SELECTION = -1;

    /**
     * Returns the port number on which the web server should run, or {@value #DYNAMIC_SELECTION} if
     * port selection should remain dynamic.
     *
     * @return the port number on which the web server should run, or {@value DYNAMIC_SELECTION}
     */
    int port();
}
