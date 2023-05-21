package org.dockbox.hartshorn.component.factory;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.util.ApplicationException;

public abstract class ConstructorFactoryMethodInterceptor<T, R> implements MethodInterceptor<T, R> {

    protected <U> U processInstance(final ApplicationContext context, final U instance, final boolean enable) throws ApplicationException {
        U out = instance;
        out = context.get(ComponentPopulator.class).populate(out);
        if (enable) {
            out = context.get(ComponentPostConstructor.class).doPostConstruct(out);
        }
        return out;
    }
}
