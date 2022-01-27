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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.Setter;

public final class MethodProxyContext<T, R> {

    @Getter private final Class<T> value;
    @Getter private final Method target;
    private final ProxyFunction<T, R> delegate;

    @Setter private boolean overwriteResult = true;
    @Getter @Setter private int priority = 10;

    private MethodProxyContext(final Class<T> type, final Method target, final ProxyFunction<T, R> delegate) {
        this.value = type;
        this.target = target;
        this.delegate = delegate;
    }

    public static <T, R> MethodProxyContext<T, R> of(final TypeContext<T> type, final MethodContext<?, T> target, final ProxyFunction<T, R> proxyFunction) {
        return of(type.type(), target.method(), proxyFunction);
    }

    public static <T, R> MethodProxyContext<T, R> of(final Class<T> type, final Method target, final ProxyFunction<T, R> proxyFunction) {
        return new MethodProxyContext<>(type, target, proxyFunction);
    }

    public Class<?> targetClass() {
        return this.target().getDeclaringClass();
    }

    public R delegate(final T instance, final ProxyHandler<T> handler, final MethodContext<?, ?> proceed, final Object self, final Object... args) throws Throwable {
        return this.delegate.delegate(instance, args, new ProxyContextImpl(handler, proceed, self));
    }

    public boolean overwriteResult() {
        return this.overwriteResult && !this.isVoid();
    }

    public boolean isVoid() {
        return Void.TYPE.equals(this.target().getReturnType());
    }
}
