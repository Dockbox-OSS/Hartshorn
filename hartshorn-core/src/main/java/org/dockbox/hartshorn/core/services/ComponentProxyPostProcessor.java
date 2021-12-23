package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

@AutomaticActivation
public class ComponentProxyPostProcessor implements ComponentPostProcessor<Service> {

    @Override
    public Class<Service> activator() {
        return Service.class;
    }

    @Override
    public <T> T process(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        try {
            final ProxyHandler<T> handler = context.environment().manager().handler(type, instance);
            return handler.proxy(context, instance);
        } catch (final ApplicationException e) {
            return ExceptionHandler.unchecked(e);
        }
    }

    @Override
    public <T> boolean modifies(final ApplicationContext context, final TypeContext<T> type, @Nullable final T instance) {
        final Exceptional<ComponentContainer> container = context.locator().container(type);
        return container.present() && container.get().permitsProxying();
    }

    @Override
    public ProcessingOrder order() {
        return ProcessingOrder.FIRST;
    }
}
