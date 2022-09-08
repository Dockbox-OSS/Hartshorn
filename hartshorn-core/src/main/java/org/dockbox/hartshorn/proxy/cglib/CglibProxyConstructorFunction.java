package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;

import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;

public class CglibProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> type;
    private final Enhancer enhancer;

    public CglibProxyConstructorFunction(final Class<T> type, final Enhancer enhancer) {
        this.type = type;
        this.enhancer = enhancer;
    }

    @Override
    public T create() throws ApplicationException {
        final Object instance = this.enhancer.create();
        return this.type.cast(instance);
    }

    @Override
    public T create(final ConstructorContext<T> constructor, final Object[] args) throws ApplicationException {
        final Class<?>[] parameterTypes = constructor.constructor().getParameterTypes();
        final Object instance = this.enhancer.create(parameterTypes, args);
        return this.type.cast(instance);
    }
}
