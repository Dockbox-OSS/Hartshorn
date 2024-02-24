package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ContextAwareComponentSupplier<T> extends NonTypeAwareProvider<T> {

    @Override
    default Option<ObjectContainer<T>> provide(ApplicationContext context, ComponentRequestContext requestContext) throws ApplicationException {
        return Option.of(new ComponentObjectContainer<>(this.get(requestContext)));
    }

    T get(ComponentRequestContext context) throws ApplicationException;
}
