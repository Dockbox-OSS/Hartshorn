package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.config.ConfigurationManager;
import org.dockbox.hartshorn.di.InjectConfiguration;
import org.dockbox.hartshorn.di.Key;
import org.dockbox.hartshorn.di.binding.Providers;
import org.dockbox.hartshorn.di.context.ApplicationContext;

public class JUnitInjectOverrides extends InjectConfiguration {
    @Override
    public void collect(final ApplicationContext context) {
        this.hierarchy(Key.of(ConfigurationManager.class)).add(0, Providers.bound(JUnitConfigurationManager.class));
    }
}
