package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;

@Configuration(source = "main", owner = Hartshorn.class)
public class DemoConfigurationProvider {
    @Value("something.abc")
    private String abc;
}
