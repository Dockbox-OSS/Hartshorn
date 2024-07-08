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

package org.dockbox.hartshorn.inject.processors.proxy;

import java.util.Collection;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.advice.registry.AdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallback;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ComponentPostProcessor} that allows for the wrapping of methods in a proxy. This is useful for
 * implementing cross-cutting concerns such as logging, security, and performance monitoring. Method wrappers
 * are generated based on the created {@link ProxyCallback}s, and are then applied to the {@link AdvisorRegistry}
 * of the proxy factory.
 *
 * @see ProxyCallback
 * @see ProxyFactory
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public abstract class PhasedProxyCallbackPostProcessor extends ComponentPostProcessor {

    @Override
    public <T> boolean isCompatible(ComponentProcessingContext<T> processingContext) {
        return processingContext.permitsProxying();
    }

    @Override
    public <T> void preConfigureComponent(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext) {
        ComponentKey<T> key = processingContext.key();
        Collection<MethodView<T, ?>> methods = this.modifiableMethods(application, key, instance);

        ProxyFactory<T> factory = processingContext.get(ProxyFactory.class);
        if (factory == null) {
            return;
        }

        instance = this.processProxy(application, instance, processingContext, factory);

        for (MethodView<T, ?> method : methods) {
            ProxyCallback<T> before = this.doBefore(application, method, key, instance);
            ProxyCallback<T> after = this.doAfter(application, method, key, instance);
            ProxyCallback<T> afterThrowing = this.doAfterThrowing(application, method, key, instance);
            MethodWrapper<T> wrapper = MethodWrapper.of(before, after, afterThrowing);

            if (before != null || after != null || afterThrowing != null) {
                factory.advisors().method(method).wrapAround(wrapper);
            }
        }
    }

    protected <T> T processProxy(InjectionCapableApplication application, @Nullable T instance, ComponentProcessingContext<T> processingContext, ProxyFactory<T> proxyFactory) {
        // Left for subclasses to override if necessary
        return instance;
    }

    protected <T> Collection<MethodView<T, ?>> modifiableMethods(InjectionCapableApplication application, ComponentKey<T> key, @Nullable T instance) {
        TypeView<T> typeView = instance == null
                ? application.environment().introspector().introspect(key.type())
                : application.environment().introspector().introspect(instance);

        return typeView.methods().all()
                .stream().filter(method -> this.preconditions(application, method, key, instance))
                .toList();
    }

    /**
     * Returns whether the method should be wrapped. This method is called for each method of the component that is
     * being processed. If this method returns {@code false}, the method will not be wrapped in a proxy, and the proxy
     * callback methods will never be called.
     *
     * @param application the application in which the component is being processed
     * @param method the method that is being processed
     * @param key the component key of the component that is being processed
     * @param instance the instance of the component that is being processed, or {@code null} if the component is not yet instantiated
     * @param <T> the type of the component that is being processed
     *
     * @return {@code true} if the method should be wrapped, {@code false} otherwise
     */
    public abstract <T> boolean preconditions(InjectionCapableApplication application, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    /**
     * Returns the proxy callback that should be called before the method is invoked. This method is called for each
     * compatible method of the component that is being processed. If this method returns {@code null}, no proxy callback
     * will be called before the method is invoked.
     *
     * @param application the application in which the component is being processed
     * @param method the method that is being processed
     * @param key the component key of the component that is being processed
     * @param instance the instance of the component that is being processed, or {@code null} if the component is not yet instantiated
     * @param <T> the type of the component that is being processed
     *
     * @return the proxy callback that should be called before the method is invoked, or {@code null} if no proxy callback should be called
     */
    @Nullable
    public abstract <T> ProxyCallback<T> doBefore(InjectionCapableApplication application, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    /**
     * Returns the proxy callback that should be called after the method is invoked. This method is called for each
     * compatible method of the component that is being processed. If this method returns {@code null}, no proxy callback
     * will be called after the method is invoked.
     *
     * @param application the application in which the component is being processed
     * @param method the method that is being processed
     * @param key the component key of the component that is being processed
     * @param instance the instance of the component that is being processed, or {@code null} if the component is not yet instantiated
     * @param <T> the type of the component that is being processed
     *
     * @return the proxy callback that should be called after the method is invoked, or {@code null} if no proxy callback should be called
     */
    @Nullable
    public abstract <T> ProxyCallback<T> doAfter(InjectionCapableApplication application, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);

    /**
     * Returns the proxy callback that should be called after the method has thrown an exception. This method is called for each
     * compatible method of the component that is being processed. If this method returns {@code null}, no proxy callback
     * will be called after the method has thrown an exception.
     *
     * @param application the application in which the component is being processed
     * @param method the method that is being processed
     * @param key the component key of the component that is being processed
     * @param instance the instance of the component that is being processed, or {@code null} if the component is not yet instantiated
     * @param <T> the type of the component that is being processed
     *
     * @return the proxy callback that should be called after the method has thrown an exception, or {@code null} if no proxy callback should be called
     */
    @Nullable
    public abstract <T> ProxyCallback<T> doAfterThrowing(InjectionCapableApplication application, MethodView<T, ?> method, ComponentKey<T> key, @Nullable T instance);
}
