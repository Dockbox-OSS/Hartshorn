package org.dockbox.sample.scopes;

import org.dockbox.hartshorn.inject.annotations.configuration.Configuration;
import org.dockbox.hartshorn.inject.annotations.configuration.Scoped;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;

@Configuration
public class ScopedMessageConfiguration {

    @Singleton
    public String defaultMessage() {
        return "Hello, World!";
    }

    @Singleton
    @Scoped(CustomMessageScope.class)
    public String scopedMessage(CustomMessageScope scope) {
        return "Hello from %s!".formatted(scope.target());
    }
}
