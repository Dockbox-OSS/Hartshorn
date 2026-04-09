package org.dockbox.hartshorn.web;

import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.test.RequiresTestApplication;

/**
 * Configuration for web servers active within a test context.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Configuration
@RequiresTestApplication
public class WebServerTestConfiguration {

    /**
     * Port provider that always provides a dynamic selection hint to the server. As this isn't an
     * exact port in itself, it remains up to the server implementation to resolve an available port
     * for the server to run on.
     *
     * @return a {@link ServerPortProvider} that always returns
     * {@link ServerPortProvider#DYNAMIC_SELECTION}
     */
    @Singleton
    @Priority(Priority.SUPPORT_PRIORITY + 128)
    public ServerPortProvider dynamicPortProvider() {
        return () -> ServerPortProvider.DYNAMIC_SELECTION;
    }
}
