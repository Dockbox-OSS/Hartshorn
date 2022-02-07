package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.element.TypeContext;

public interface ComponentActivationFilter {
    <T> boolean doActivate(TypeContext<T> component, ComponentContainer container);
}
