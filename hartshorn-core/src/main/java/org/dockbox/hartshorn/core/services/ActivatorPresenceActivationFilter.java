package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

public class ActivatorPresenceActivationFilter implements ComponentActivationFilter {

    private final ApplicationContext applicationContext;

    public ActivatorPresenceActivationFilter(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> boolean doActivate(final TypeContext<T> component, final ComponentContainer container) {
        return container.activators().stream().allMatch(this.applicationContext::hasActivator);
    }
}
