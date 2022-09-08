package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;

import java.lang.reflect.InvocationTargetException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> type;
    private final ProxyFactory factory;
    private final MethodHandler methodHandler;

    public JavassistProxyConstructorFunction(final Class<T> type, final ProxyFactory factory, final MethodHandler methodHandler) {
        this.type = type;
        this.factory = factory;
        this.methodHandler = methodHandler;
    }

    @Override
    public T create() throws ApplicationException {
        try {
            return this.type.cast(this.factory.create(new Class<?>[0], new Object[0], this.methodHandler));
        } catch (final RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public T create(final ConstructorContext<T> constructor, final Object[] args) throws ApplicationException {
        try {
            final Class<?>[] parameterTypes = constructor.constructor().getParameterTypes();
            return this.type.cast(this.factory.create(parameterTypes, args, this.methodHandler));
        } catch (final RuntimeException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }
}
