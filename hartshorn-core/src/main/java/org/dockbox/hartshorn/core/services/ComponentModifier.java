package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;

public abstract class ComponentModifier<A extends Annotation> implements InjectionModifier<A> {

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance, final Attribute<?>... properties) {
        return context.locator().container(type).present() && this.modifies(context, type, instance, properties);
    }

    protected abstract <T> boolean modifies(ApplicationContext context, TypeContext<T> type, @Nullable T instance, Attribute<?>... properties);
}
