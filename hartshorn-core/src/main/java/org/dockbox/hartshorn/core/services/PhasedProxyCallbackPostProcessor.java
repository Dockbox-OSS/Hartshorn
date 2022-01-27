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

package org.dockbox.hartshorn.core.services;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.proxy.ProxyMethodCallback;
import org.dockbox.hartshorn.core.proxy.ProxyCallback;
import org.dockbox.hartshorn.core.proxy.ProxyMethodCallbackImpl;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.CallbackPhase;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.stream.Collectors;

public abstract class PhasedProxyCallbackPostProcessor<A extends Annotation> extends FunctionalComponentPostProcessor<A> {

    @Override
    public <T> T process(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        final Collection<MethodContext<?, T>> methods = this.modifiableMethods(context, key, instance);

        // Will reuse existing handler of proxy
        final ProxyHandler<T> handler = context.environment().manager().handler(key.type(), instance);

        for (final MethodContext<?, T> method : methods) {
            final ProxyCallback<T> before = this.doBefore(context, method, key, instance);
            final ProxyCallback<T> after = this.doAfter(context, method, key, instance);
            final ProxyCallback<T> afterThrowing = this.doAfterThrowing(context, method, key, instance);

            if (before != null) handler.callback(method, this.callback(before, CallbackPhase.BEFORE, method));
            if (after != null) handler.callback(method, this.callback(after, CallbackPhase.AFTER, method));
            if (afterThrowing != null) handler.callback(method, this.callback(afterThrowing, CallbackPhase.THROWING, method));
        }

        return instance;
    }

    protected <T> ProxyMethodCallback<T> callback(final ProxyCallback<T> function, final CallbackPhase phase, final MethodContext<?, T> methodContext) {
        return new ProxyMethodCallbackImpl<>(phase, function);
    }

    protected <T> Collection<MethodContext<?, T>> modifiableMethods(final ApplicationContext context, final Key<T> key, @Nullable final T instance) {
        return key.type().methods().stream()
                .filter(method -> this.wraps(context, method, key, instance))
                .collect(Collectors.toList());
    }

    public abstract <T> boolean wraps(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doBefore(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doAfter(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doAfterThrowing(final ApplicationContext context, final MethodContext<?, T> method, final Key<T> key, @Nullable final T instance);
}
