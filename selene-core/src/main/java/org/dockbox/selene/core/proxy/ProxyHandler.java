package org.dockbox.selene.core.proxy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.server.properties.ProxyProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class ProxyHandler<T> implements MethodHandler {

    private final Multimap<Method, ProxyProperty<T, ?>> handlers = ArrayListMultimap.create();
    private final T instance;

    public ProxyHandler(T instance) {
        this.instance = instance;
    }

    public void delegate(ProxyProperty<T, ?> property) {
        this.handlers.put(property.getTargetMethod(), property);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        // The handler listens for all methods, while not all methods are proxied
        if (this.handlers.containsKey(thisMethod)) {
            Collection<ProxyProperty<T, ?>> properties = this.handlers.get(thisMethod);
            Object returnValue = null;
            // Sort the list so all properties are prioritised. The phase at which the property will be delegated does
            // not matter here, as out-of-phase properties are not performed.
            List<ProxyProperty<T, ?>> toSort = new ArrayList<>(properties);
            toSort.sort(Comparator.comparingInt(ProxyProperty::getPriority));

            // Phase is sorted in execution order (HEAD, OVERWRITE, TAIL)
            for (Phase phase : Phase.values())
                returnValue = this.enterPhase(phase, toSort, args, thisMethod, returnValue);

            return returnValue;
        } else {
            // If no handler is known, default to the original method. This is delegated to the instance created, as it
            // is typically created through Selene's injectors and therefore DI dependent.
            return thisMethod.invoke(this.instance, args);
        }
    }

    private Object enterPhase(Phase at, Iterable<ProxyProperty<T, ?>> properties, Object[] args, Method thisMethod, Object returnValue) throws InvocationTargetException, IllegalAccessException {
        // Used to ensure the target is performed if there is no OVERWRITE phase hook
        boolean target = true;
        for (ProxyProperty<T, ?> property : properties) {
            if (at == property.getTarget()) {
                Object result = property.delegate(this.instance, args);
                if (property.overwriteResult()) {
                    // A proxy returning null typically indicates the use of a non-returning function, for annotation
                    // properties this is handled internally, however proxy types should carry the annotation value to
                    // ensure no results will be overwritten. Null values may cause the initial target return value to
                    // be used instead if no other phase hook changes the final return value.
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
