/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component.processing.proxy;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.FunctionalComponentPostProcessor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallback;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;

public abstract class PhasedProxyCallbackPostProcessor extends FunctionalComponentPostProcessor {

    @Override
    public final <T> T process(final ApplicationContext context, @Nullable T instance, final ComponentContainer container, final ComponentProcessingContext<T> processingContext) {
        final ComponentKey<T> key = processingContext.key();
        final Collection<MethodView<T, ?>> methods = this.modifiableMethods(context, key, instance);

        final ProxyFactory<T> factory = processingContext.get(ComponentKey.of(ProxyFactory.class));
        if (factory == null) return instance;

        instance = this.processProxy(context, instance, processingContext, factory);

        for (final MethodView<T, ?> method : methods) {
            final ProxyCallback<T> before = this.doBefore(context, method, key, instance);
            final ProxyCallback<T> after = this.doAfter(context, method, key, instance);
            final ProxyCallback<T> afterThrowing = this.doAfterThrowing(context, method, key, instance);
            final MethodWrapper<T> wrapper = MethodWrapper.of(before, after, afterThrowing);

            if (before != null || after != null || afterThrowing != null) {
                factory.advisors().method(method).wrapAround(wrapper);
            }
        }

        return instance;
    }

    protected <T> T processProxy(final ApplicationContext context, @Nullable final T instance, final ComponentProcessingContext<T> processingContext, final ProxyFactory<T> proxyFactory) {
        // Left for subclasses to override if necessary
        return instance;
    }

    protected <T> Collection<MethodView<T, ?>> modifiableMethods(final ApplicationContext context, final ComponentKey<T> key, @Nullable final T instance) {
        final TypeView<T> typeView = instance == null
                ? context.environment().introspect(key.type())
                : context.environment().introspect(instance);

        return typeView.methods().all()
                .stream().filter(method -> this.wraps(context, method, key, instance))
                .toList();
    }

    public abstract <T> boolean wraps(final ApplicationContext context, final MethodView<T, ?> method, final ComponentKey<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doBefore(final ApplicationContext context, final MethodView<T, ?> method, final ComponentKey<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doAfter(final ApplicationContext context, final MethodView<T, ?> method, final ComponentKey<T> key, @Nullable final T instance);

    public abstract <T> ProxyCallback<T> doAfterThrowing(final ApplicationContext context, final MethodView<T, ?> method, final ComponentKey<T> key, @Nullable final T instance);
}
