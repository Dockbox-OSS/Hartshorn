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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.DelegatingContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.Getter;

public class JavassistInterfaceHandler<T> implements InvocationHandler, ProxyHandler<T>, DelegatingContext {

    @Getter private final JavassistProxyHandler<T> handler;

    public JavassistInterfaceHandler(final JavassistProxyHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.handler().invoke(proxy, method, null, args);
    }

    public T proxy() {
        final T proxy = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
                this.handler().type().type()
        }, this);
        this.handler().proxyInstance(proxy);
        return proxy;
    }

    @Override
    public T proxy(final T existing) throws ApplicationException {
        return this.handler().proxy(existing);
    }

    @Override
    public Context get() {
        return this.handler();
    }

    @Override
    public TypeContext<T> type() {
        return this.handler().type();
    }

    @Override
    public void delegate(final MethodProxyContext<T, ?> property) {
        this.handler().delegate(property);
    }

    @Override
    public Exceptional<T> proxyInstance() {
        return this.handler().proxyInstance();
    }

    @Override
    public Exceptional<T> instance() {
        return this.handler().instance();
    }

    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        return this.handler().invoke(self, thisMethod, proceed, args);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.handler().applicationContext();
    }
}
