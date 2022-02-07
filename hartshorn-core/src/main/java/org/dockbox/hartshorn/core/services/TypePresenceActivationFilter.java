package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.element.TypeContext;

public class TypePresenceActivationFilter implements ComponentActivationFilter {

    @Override
    public <T> boolean doActivate(final TypeContext<T> component, final ComponentContainer container) {
        for (final String requiredType : container.requiredTypes()) {
            if (TypeContext.lookup(requiredType).isVoid()) return false;
        }
        return true;
    }
}
