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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.NamedContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import lombok.Getter;

public class JavaInterfaceProxyHandler<T> implements InvocationHandler, ProxyHandler<T> {

    @Getter private final JavassistProxyHandler<T> handler;

    public JavaInterfaceProxyHandler(final JavassistProxyHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        return this.handler().invoke(null, method, null, args);
    }

    public T proxy() {
        T proxy = (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{
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
    public <C extends Context> void add(final C context) {
        this.handler().add(context);
    }

    @Override
    public <N extends NamedContext> void add(final N context) {
        this.handler().add(context);
    }

    @Override
    public <C extends Context> void add(final String name, final C context) {
        this.handler().add(name, context);
    }

    @Override
    public <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Class<C> context) {
        return this.handler().first(applicationContext, context);
    }

    @Override
    public Exceptional<Context> first(final String name) {
        return this.handler().first(name);
    }

    @Override
    public <N extends Context> Exceptional<N> first(final String name, final Class<N> context) {
        return this.handler().first(name, context);
    }

    @Override
    public <C extends Context> List<C> all(final Class<C> context) {
        return this.handler().all(context);
    }

    @Override
    public List<Context> all(final String name) {
        return this.handler().all(name);
    }

    @Override
    public <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.handler().all(name, context);
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

    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        return this.handler().invoke(self, thisMethod, proceed, args);
    }
}
