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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.ArrayListMultiMap;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.Getter;

public class JavassistProxyHandler<T> extends DefaultContext implements ProxyHandler<T>, MethodHandler {

    private final MultiMap<Method, MethodProxyContext<T, ?>> handlers = new ArrayListMultiMap<>();
    private final T instance;
    private T proxyInstance;

    @Getter
    private final TypeContext<T> type;

    public JavassistProxyHandler(final T instance) {
        this(instance, (Class<T>) instance.getClass());
    }

    public JavassistProxyHandler(final T instance, final Class<T> type) {
        this(instance, TypeContext.of(type));
    }

    public JavassistProxyHandler(final T instance, final TypeContext<T> type) {
        this.instance = instance;
        this.type = type;
    }

    @SafeVarargs
    public final void delegate(final MethodProxyContext<T, ?>... properties) {
        for (final MethodProxyContext<T, ?> property : properties) this.delegate(property);
    }

    @Override
    public void delegate(final MethodProxyContext<T, ?> property) {
        if (Modifier.isFinal(property.target().getModifiers()))
            throw new ApplicationException("Cannot proxy final method " + property.target().getName()).runtime();
        
        this.handlers.put(property.target(), property);
    }

    @Override
    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        // The handler listens for all methods, while not all methods are proxied
        if (this.handlers.containsKey(thisMethod)) {
            final Collection<MethodProxyContext<T, ?>> properties = this.handlers.get(thisMethod);
            Object returnValue = null;
            // Sort the list so all properties are prioritised. The phase at which the property will be
            // delegated does not matter here, as out-of-phase properties are not performed.
            final List<MethodProxyContext<T, ?>> toSort = new ArrayList<>(properties);
            toSort.sort(Comparator.comparingInt(MethodProxyContext::priority));

            // Phase is sorted in execution order (HEAD, OVERWRITE, TAIL)
            for (final Phase phase : Phase.values())
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
                try {
                    return target.invoke(this.instance, args);
                } catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
            else {
                final StackTraceElement element = Thread.currentThread().getStackTrace()[3];
                final String name = element.getMethodName();
                final String className = this.type == null ? "" : this.type.name() + ".";
                throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
            }
        }
    }

    private Object enterPhase(
            final Phase at,
            final Iterable<MethodProxyContext<T, ?>> properties,
            final Object[] args,
            final Method thisMethod,
            final Method proceed,
            final Object self,
            Object returnValue
    ) throws InvocationTargetException, IllegalAccessException, ApplicationException {
        // Used to ensure the target is performed if there is no OVERWRITE phase hook
        boolean target = true;
        for (final MethodProxyContext<T, ?> property : properties) {
            if (at == property.phase()) {
                final MethodContext<?, ?> methodContext = proceed == null ? null : MethodContext.of(proceed);
                final Object result = property.delegate(this.instance, methodContext, self, args);
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
            final Object result = thisMethod.invoke(this.instance, args);
            if (null == returnValue) returnValue = result;
        }
        return returnValue;
    }

    @Override
    public T proxy() throws ApplicationException {
        return this.proxy(null);
    }

    @Override
    public T proxy(final T existing) throws ApplicationException {
        if (this.type().isInterface()) {
            return new JavaInterfaceProxyHandler<>(this).proxy();
        }

        final ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.type().type());
        factory.setFilter(JavassistProxyHandler.this.handlers::containsKey);

        try {
            final T proxy = (T) factory.create(new Class<?>[0], new Object[0], this);
            // New proxy instances
            if (existing != null) this.restoreFields(existing, proxy);
            this.proxyInstance(proxy);
            return proxy;
        }
        catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public Exceptional<T> proxyInstance() {
        return Exceptional.of(this.proxyInstance);
    }

    void proxyInstance(T proxyInstance) {
        this.proxyInstance = proxyInstance;
    }

    private void restoreFields(final T existing, final T proxy) {
        TypeContext<T> context = TypeContext.of(existing);
        if (context.isProxy()) {
            context = JavassistProxyUtil.handler(this.type(), existing).type();
        }
        for (final FieldContext<?> field : context.fields()) {
            field.set(proxy, field.get(existing).orNull());
        }
    }
}
