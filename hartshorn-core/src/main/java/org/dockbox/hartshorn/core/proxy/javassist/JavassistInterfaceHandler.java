/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.core.proxy.javassist;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.DelegatingContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.MethodProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyMethodCallback;
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
    public void delegate(@NonNull final MethodProxyContext<T, ?> property) {
        this.handler().delegate(property);
    }

    @Override
    public void callback(@NonNull final MethodContext<?, T> method, @NonNull final ProxyMethodCallback<T> callback) {
        this.handler().callback(method, callback);
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
