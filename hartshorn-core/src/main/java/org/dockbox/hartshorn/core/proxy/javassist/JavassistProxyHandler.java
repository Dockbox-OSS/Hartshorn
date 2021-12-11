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

package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.CustomMultiMap;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.JavaInterfaceProxyHandler;
import org.dockbox.hartshorn.core.proxy.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import lombok.Getter;

public class JavassistProxyHandler<T> extends DefaultContext implements ProxyHandler<T>, MethodHandler {

    private final MultiMap<Method, MethodProxyContext<T, ?>> handlers = new CustomMultiMap<>(CopyOnWriteArrayList::new);
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
            ExceptionHandler.unchecked(new ApplicationException("Cannot proxy final method " + property.target().getName()));

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

            for (final MethodProxyContext<T, ?> property : properties) {
                final MethodContext<?, ?> methodContext = proceed == null ? null : MethodContext.of(proceed);
                final Object result = property.delegate(this.instance, methodContext, self, args);
                if (property.overwriteResult() && !Void.TYPE.equals(thisMethod.getReturnType()))
                    returnValue = result;
            }

            if (null == returnValue && this.instance != null) {
                returnValue = thisMethod.invoke(this.instance, args);
            }

            return returnValue;
        }
        else {
            // If no handler is known, default to the original method. This is delegated to the instance
            // created, as it is typically created through Hartshorn's injectors and therefore DI dependent.
            Method target = thisMethod;
            if (this.instance == null && thisMethod == null)
                target = proceed;

            if (target != null) {
                try {
                    if (this.instance != null) {
                        return target.invoke(this.instance, args);
                    }
                    else if (thisMethod.isDefault()) {
                        return MethodHandles.lookup().findSpecial(
                                this.type().type(),
                                thisMethod.getName(),
                                MethodType.methodType(thisMethod.getReturnType(), thisMethod.getParameterTypes()),
                                this.type().type()
                        ).bindTo(self).invokeWithArguments(args);
                    }
                    else if (!(self instanceof Proxy)){
                        return target.invoke(self, args);
                    }
                    else {
                        throw new IllegalArgumentException("Cannot invoke method " + thisMethod.getName() + " on proxy " + self.getClass().getName());
                    }
                }
                catch (final InvocationTargetException e) {
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
        catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public Exceptional<T> proxyInstance() {
        return Exceptional.of(this.proxyInstance);
    }

    public void proxyInstance(final T proxyInstance) {
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
