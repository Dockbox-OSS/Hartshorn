package org.dockbox.hartshorn.web.route;

import org.slf4j.Logger;

public class SimpleHandlerMappingRegistrar implements HandlerMappingRegistrar {

    private final HandlerMappingRegistry registry;
    private final Logger logger;

    public SimpleHandlerMappingRegistrar(HandlerMappingRegistry registry, Logger logger) {
        this.registry = registry;
        this.logger = logger;
    }

    @Override
    public HandlerMappingRegistrar add(RouteMapping mapping, RequestHandler handler) {
        logger.info(
                "Registering handler for {} {}: {}",
                mapping.method(),
                mapping.pathPattern(),
                handler
        );
        this.registry.add(mapping, handler);
        return this;
    }
}
