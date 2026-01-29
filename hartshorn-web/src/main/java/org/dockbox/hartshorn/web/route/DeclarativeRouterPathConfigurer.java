package org.dockbox.hartshorn.web.route;

import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.web.rest.RestRouter;

public class DeclarativeRouterPathConfigurer implements RouterPathRegistrar {

    private final ComponentRegistry componentRegistry;
    private final ConversionService conversionService;
    private final InjectionCapableApplication application;

    public DeclarativeRouterPathConfigurer(
            ComponentRegistry componentRegistry,
            ConversionService conversionService,
            InjectionCapableApplication application
    ) {
        this.componentRegistry = componentRegistry;
        this.conversionService = conversionService;
        this.application = application;
    }

    @Override
    public void register(RouterPathConfigurer target) {
        for (ComponentContainer<?> container : componentRegistry.containers()) {
            if (container.type().annotations().has(RestRouter.class)) {
                RestRouterPathRegistrar registrar = new RestRouterPathRegistrar(
                        this.application,
                        this.conversionService,
                        container.type()
                );
                registrar.registerPaths(target);
            }
        }
    }
}
