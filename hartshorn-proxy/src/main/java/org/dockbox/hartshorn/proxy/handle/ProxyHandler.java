/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.proxy.handle;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.proxy.ProxyAttribute;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.AccessLevel;
import lombok.Getter;

public class ProxyHandler<T> implements MethodHandler {

    private final Multimap<Method, ProxyAttribute<T, ?>> handlers = ArrayListMultimap.create();
    private final T instance;

    @Getter(AccessLevel.PROTECTED)
    private final Class<T> type;

    public ProxyHandler(T instance) {
        this.instance = instance;
        //noinspection unchecked
        this.type = (Class<T>) instance.getClass();
    }

    public ProxyHandler(T instance, Class<T> type) {
        this.instance = instance;
        this.type = type;
    }

    @SafeVarargs
    public final void delegate(ProxyAttribute<T, ?>... properties) {
        for (ProxyAttribute<T, ?> property : properties) this.delegate(property);
    }

    public void delegate(ProxyAttribute<T, ?> property) {
        this.handlers.put(property.target(), property);
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        // The handler listens for all methods, while not all methods are proxied
        if (this.handlers.containsKey(thisMethod)) {
            Collection<ProxyAttribute<T, ?>> properties = this.handlers.get(thisMethod);
            Object returnValue = null;
            // Sort the list so all properties are prioritised. The phase at which the property will be
            // delegated does not matter here, as out-of-phase properties are not performed.
            List<ProxyAttribute<T, ?>> toSort = new ArrayList<>(properties);
            toSort.sort(Comparator.comparingInt(ProxyAttribute::priority));

            // Phase is sorted in execution order (HEAD, OVERWRITE, TAIL)
            for (Phase phase : Phase.values())
                returnValue = this.enterPhase(phase, toSort, args, thisMethod, proceed, self, returnValue);

            return returnValue;
        }
        else {
            // If no handler is known, default to the original method. This is delegated to the instance
            // created, as it is typically created through Hartshorn's injectors and therefore DI dependent.
            Method target = thisMethod;
            if (this.instance == null)
                target = proceed;

            if (target != null) {
                return target.invoke(this.instance, args);
            } else {
                final StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                final String name = element.getMethodName();
                throw new AbstractMethodError("Cannot invoke method '" + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
            }
        }
    }

    private Object enterPhase(
            Phase at,
            Iterable<ProxyAttribute<T, ?>> properties,
            Object[] args,
            Method thisMethod,
            Method proceed,
            Object self,
            Object returnValue
    ) throws InvocationTargetException, IllegalAccessException {
        // Used to ensure the target is performed if there is no OVERWRITE phase hook
        boolean target = true;
        for (ProxyAttribute<T, ?> property : properties) {
            if (at == property.phase()) {
                Object result = property.delegate(this.instance, proceed, self, args);
                if (property.overwriteResult() && !Void.TYPE.equals(thisMethod.getReturnType())) {
                    // A proxy returning null typically indicates the use of a non-returning function, for
                    // annotation  properties this is handled internally, however proxy types should carry
                    // the annotation value to ensure no results will be overwritten. Null values may cause
                    // the initial target return value to be used instead if no other phase hook changes the
                    // final return value.
                    if (null == result) Hartshorn.log().warn("Proxy method for '" + thisMethod.getName() + "' returned null while overwriting results!");
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
        if (this.type().isInterface()) {
            return new ProxyInterfaceHandler<>(this).proxy();
        }
        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.type());

        //noinspection unchecked
        return (T) factory.create(new Class<?>[0], new Object[0], this);
    }
}
