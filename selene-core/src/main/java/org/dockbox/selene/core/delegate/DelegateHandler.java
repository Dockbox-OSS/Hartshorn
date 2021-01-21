package org.dockbox.selene.core.delegate;

import org.dockbox.selene.core.server.properties.DelegateProperty;
import org.dockbox.selene.core.util.SeleneUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class DelegateHandler<T> implements MethodHandler {

    private final Map<Method, DelegateProperty<T, ?>> handlers = SeleneUtils.emptyMap();
    private final T instance;

    public DelegateHandler(T instance) {
        this.instance = instance;
    }

    public void delegate(DelegateProperty<T, ?> property) {
        this.handlers.put(property.getTargetMethod(), property);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        if (this.handlers.containsKey(thisMethod)) {
            DelegateProperty<T, ?> property = this.handlers.get(thisMethod);
            switch (property.getTarget()) {
                case OVERWRITE:
                    return property.delegate(this.instance, args);
                case TAIL:
                    Object tailInstance = thisMethod.invoke(this.instance, args);
                    Object tailDelegate = property.delegate(this.instance, args);
                    return property.overwriteResult() ? tailDelegate : tailInstance;
                case HEAD:
                    Object headDelegate = property.delegate(this.instance, args);
                    Object headInstance = null;
                    if (!property.isCancelled()) headInstance = thisMethod.invoke(this.instance, args);
                    return property.overwriteResult() ? headDelegate : headInstance;
                default:
                    throw new IllegalArgumentException("Unknown delegate target: " + property.getTarget());
            }
        } else {
            return thisMethod.invoke(this.instance, args);
        }
    }

    public T proxy() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.instance.getClass());
        //noinspection unchecked
        return (T) factory.create(new Class<?>[0], new Object[0], this);
    }
}
