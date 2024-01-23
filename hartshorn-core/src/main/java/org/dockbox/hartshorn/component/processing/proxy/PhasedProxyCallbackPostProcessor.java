/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallback;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public abstract class PhasedProxyCallbackPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying();
    }

    @Override
    public <T> void preConfigureComponent(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        ComponentKey<T> key = processingContext.key();
        Collection<MethodView<T, ?>> methods = this.modifiableMethods(context, key, instance);

        ProxyFactory<T> factory = processingContext.get(ProxyFactory.class);
        if (factory == null) {
            return;
        }

        instance = this.processProxy(context, instance, processingContext, factory);

        for (MethodView<T, ?> method : methods) {
            ProxyCallback<T> before = this.doBefore(context, method, key, instance);
            ProxyCallback<T> after = this.doAfter(context, method, key, instance);
            ProxyCallback<T> afterThrowing = this.doAfterThrowing(context, method, key, instance);
            MethodWrapper<T> wrapper = MethodWrapper.of(before, after, afterThrowing);

            if (before != null || after != null || afterThrowing != null) {
                factory.advisors().method(method).wrapAround(wrapper);
            }
        }
    }

    protected <T> T processProxy(ApplicationContext context, @Nullable T instance, ComponentProcessingContext<T> processingContext, ProxyFactory<T> proxyFactory) {
        // Left for subclasses to override if necessary
        return instance;
    }

    protected <T> Collection<MethodView<T, ?>> modifiableMethods(ApplicationContext context, ComponentKey<T> key, @Nullable T instance) {
        TypeView<T> typeView = instance == null
                ? context.environment().introspector().introspect(key.type())
                : context.environment().introspector().introspect(instance);

        return typeView.methods().all()
                .stream().filter(method -> this.wraps(context, method, key, instance))
                .toList();
    }

    public abstract <T> boolean wraps(ApplicationContext context, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    public abstract <T> ProxyCallback<T> doBefore(ApplicationContext context, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    public abstract <T> ProxyCallback<T> doAfter(ApplicationContext context, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    public abstract <T> ProxyCallback<T> doAfterThrowing(ApplicationContext context, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);
}
