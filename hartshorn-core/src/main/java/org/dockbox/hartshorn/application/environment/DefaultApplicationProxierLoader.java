package org.dockbox.hartshorn.application.environment;

import org.dockbox.hartshorn.discovery.DiscoveryService;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.proxy.ApplicationProxierLoader;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyInitializer;
import org.dockbox.hartshorn.util.introspect.Introspector;

public class DefaultApplicationProxierLoader {

    public static LazyInitializer<Introspector, ApplicationProxier> create(final Customizer<Configurer> customizer) {
        return introspector -> {
            // Call, but ignore the result of the customizer for now
            customizer.configure(new Configurer());
            final ApplicationProxierLoader loader = DiscoveryService.instance().discover(ApplicationProxierLoader.class);
            return loader.create(introspector);
        };
    }

    public static class Configurer {
        // No-op, may be used in the future
    }
}
