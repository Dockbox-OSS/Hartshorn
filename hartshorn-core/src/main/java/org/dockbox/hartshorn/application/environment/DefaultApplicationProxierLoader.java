package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.application.ApplicationConfigurer;
import org.dockbox.hartshorn.discovery.DiscoveryService;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ApplicationProxierLoader;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.introspect.Introspector;

public final class DefaultApplicationProxierLoader {

    private DefaultApplicationProxierLoader() {
        throw new UnsupportedOperationException();
    }

    public static ContextualInitializer<Introspector, ApplicationProxier> create(Customizer<Configurer> customizer) {
        return introspector -> {
            // Call, but ignore the result of the customizer for now
            customizer.configure(new Configurer());
            ApplicationProxierLoader loader = DiscoveryService.instance().discover(ApplicationProxierLoader.class);
            return loader.create(introspector);
        };
    }

    public static class Configurer extends ApplicationConfigurer {
        // No-op, may be used in the future
    }
}
