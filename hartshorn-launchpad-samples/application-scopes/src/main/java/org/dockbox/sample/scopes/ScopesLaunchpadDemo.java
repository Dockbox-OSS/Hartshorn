package org.dockbox.sample.scopes;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.annotations.configuration.Singleton;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ApplicationStarter;
import org.dockbox.hartshorn.launchpad.HartshornApplication;
import org.slf4j.Logger;

public class ScopesLaunchpadDemo {

    public static void main(String[] args) {
        HartshornApplication.create(args);
    }

    @Singleton
    public ApplicationStarter applicationStarter(Logger logger) {
        return applicationContext -> {
            globalScopeDemonstration(logger, applicationContext);
            customScopeDemonstration(logger, applicationContext);
            secondCustomScopeDemonstration(logger, applicationContext);
        };
    }

    private static void globalScopeDemonstration(
        Logger logger,
        ApplicationContext applicationContext
    ) {
        // Requesting a component without an explicit scope uses the default application scope
        String defaultMessage = applicationContext.get(String.class);
        logger.info("Default message: {}", defaultMessage);
        // Components can also be requested directly, and their dependencies will be resolved
        // from the global application scope unless otherwise specified
        ComponentWithMessage componentWithDefaultMessage =
            applicationContext.get(ComponentWithMessage.class);
        logger.info("Component with default message: {}", componentWithDefaultMessage.message());
    }

    private static void customScopeDemonstration(
        Logger logger,
        ApplicationContext applicationContext
    ) {
        // Creating a custom scope instance is simple, all you need is a unique instance of your scope.
        CustomMessageScope customMessageScope = new CustomMessageScope("a custom scope");

        // Requesting a component with an explicit scope uses that scope for resolution. If there
        // is no component configuration in that scope, the default application scope will be used
        // as a fallback. In this case, the String component has a configuration in the custom scope,
        // so that one will be used instead of the default application scope configuration.
        String scopedMessage = applicationContext.get(ComponentKey.builder(String.class)
            .scope(customMessageScope)
            .build());
        logger.info("Scoped message: {}", scopedMessage);
        // Similarly, requesting a component with an explicit scope will use that scope for resolution.
        // Any dependencies of that component will also be resolved from that scope, if possible.
        ComponentWithMessage componentWithScopedMessage =
            applicationContext.get(ComponentKey.builder(ComponentWithMessage.class)
                .scope(customMessageScope)
                .build());
        logger.info("Component with scoped message: {}", componentWithScopedMessage.message());
    }

    private static void secondCustomScopeDemonstration(
        Logger logger,
        ApplicationContext applicationContext
    ) {
        // Different instances of the same scope type are considered different scopes entirely. As such,
        // a new component configuration will be created for this new scope instance. This is especially
        // useful for things like request scopes, where each (possibly parallel) request should have its
        // own scope instance.
        CustomMessageScope secondCustomMessageScope =
            new CustomMessageScope("another custom scope");
        String anotherScopedMessage = applicationContext.get(ComponentKey.builder(String.class)
            .scope(secondCustomMessageScope)
            .build());
        logger.info("Another scoped message: {}", anotherScopedMessage);
    }
}
