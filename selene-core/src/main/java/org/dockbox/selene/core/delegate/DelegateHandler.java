package org.dockbox.selene.core.delegate;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.properties.DelegateProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class DelegateHandler<T> implements MethodHandler {

    private final Multimap<Method, DelegateProperty<T, ?>> handlers = ArrayListMultimap.create();
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
            Collection<DelegateProperty<T, ?>> properties = this.handlers.get(thisMethod);
            Object returnValue = null;
            List<DelegateProperty<T, ?>> toSort = new ArrayList<>(properties);
            toSort.sort(Comparator.comparingInt(DelegateProperty::getPriority));

            for (Phase phase : Phase.values())
                returnValue = this.enterPhase(phase, toSort, args, thisMethod, returnValue);

            return returnValue;
        } else {
            return thisMethod.invoke(this.instance, args);
        }
    }

    private Object enterPhase(Phase at, Iterable<DelegateProperty<T, ?>> properties, Object[] args, Method thisMethod, Object returnValue) throws InvocationTargetException, IllegalAccessException {
        boolean target = true;
        for (DelegateProperty<T, ?> property : properties) {
            if (at == property.getTarget()) {
                Object result = property.delegate(this.instance, args);
                if (property.overwriteResult()) {
                    if (null == result) Selene.log().warn("Proxy method for '" + thisMethod.getName() + "' returned null while overwriting results!");
                    returnValue = result;
                }
                // If at least one overwrite is present,
                if (Phase.OVERWRITE == at) target = false;
            }
        }
        if (Phase.OVERWRITE == at && target) {
            Object result = thisMethod.invoke(this.instance, args);
            if (null == returnValue) returnValue = result;
        }
        return returnValue;
    }

    public T proxy() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.instance.getClass());
        //noinspection unchecked
        return (T) factory.create(new Class<?>[0], new Object[0], this);
    }

}
