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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.proxy.ModifiableProxyManager;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ObtainableView;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import jakarta.inject.Singleton;

public class BindingProcessor {

    private final Set<LateSingletonContext<?>> proxiesToInitialize = ConcurrentHashMap.newKeySet();

    public void process(final ProviderContextList context, final ApplicationContext applicationContext) throws ApplicationException {
        final MultiMap<Integer, ProviderContext> elements = context.elements();

        for (final Integer phase : elements.keySet()) {
            for (final ProviderContext provider : elements.get(phase)) {

                final Key<?> key = provider.key();
                final AnnotatedElementView element = provider.element();

                applicationContext.log().debug("Processing provider context of " + element.qualifiedName() + " for " + key + " in phase " + phase);

                if (element instanceof MethodView<?,?> methodView) {
                    this.process(key, methodView, applicationContext);
                }
                else if (element instanceof FieldView<?,?> fieldView) {
                    this.process(key, fieldView, applicationContext);
                }
            }
        }
    }

    private <R, E extends AnnotatedElementView & ObtainableView<?> & GenericTypeView<?>> void process(final Key<R> key, final E element,
                                                                                                      final ApplicationContext applicationContext) throws ApplicationException {
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        final Provider annotation = element.annotations().get(Provider.class).get();

        final boolean singleton = applicationContext.environment().singleton(key.type()) || element.annotations().has(Singleton.class);

        if (conditionMatcher.match(element)) {
            if (element.type().is(Class.class)) {
                applicationContext.log().debug("Processing class binding for " + element.type().name());
                this.processClassBinding(applicationContext, TypeUtils.adjustWildcards(element, ObtainableView.class), key, singleton, annotation);
            }
            else {
                applicationContext.log().debug("Processing instance binding for " + element.type().name());
                this.processInstanceBinding(applicationContext, TypeUtils.adjustWildcards(element, ObtainableView.class), key, singleton, annotation);
            }
        }
    }

    private <R> void processInstanceBinding(
            final ApplicationContext context, final ObtainableView<R> element, final Key<R> key, final boolean singleton, final Provider annotation
    ) {
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());
        final Supplier<R> supplier = () -> element.getWithContext().rethrowUnchecked().orNull();

        if (singleton) {
            if (annotation.lazy()) function.lazySingleton(supplier);
            else function.singleton(supplier.get());
        }
        else function.to(supplier);
    }

    private <R, C extends Class<R>> void processClassBinding(final ApplicationContext context, final ObtainableView<C> element,
                                                             final Key<R> key, boolean singleton, final Provider annotation) throws ApplicationException {
        final C targetType = element.getWithContext().rethrowUnchecked()
                .orThrow(() -> new ComponentInitializationException("Failed to obtain class type for " + element.qualifiedName()));

        singleton = singleton || context.environment().singleton(targetType);
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());

        if (singleton) {
            final boolean lazy = annotation.lazy() || Boolean.TRUE.equals(context.get(ComponentLocator.class).container(targetType).map(ComponentContainer::lazy).or(false));
            if (lazy) function.lazySingleton(() -> context.get(targetType));
            else {
                final Proxy<R> proxy = TypeUtils.adjustWildcards(context.environment().factory(targetType)
                        .proxy()
                        .rethrowUnchecked()
                        .orThrow(() -> new ComponentInitializationException("Could create temporary empty proxy for " + targetType.getSimpleName() + ", any errors may be displayed above.")), Proxy.class);
                this.proxiesToInitialize.add(new LateSingletonContext<>(targetType, proxy));
            }
        }
        else function.to(targetType);
    }

    public void finalizeProxies(final ApplicationContext applicationContext) throws ApplicationException {
        if (this.proxiesToInitialize.isEmpty()) return;

        for (final LateSingletonContext<?> proxyContext : new ArrayList<>(this.proxiesToInitialize)) {
            this.proxiesToInitialize.remove(proxyContext);

            final Object instance = applicationContext.get(proxyContext.targetType);
            if (proxyContext.proxy.manager() instanceof ModifiableProxyManager) {
                final ModifiableProxyManager<Object, ?> proxyManager = TypeUtils.adjustWildcards(proxyContext.proxy.manager(), ModifiableProxyManager.class);
                proxyManager.delegate(instance);
            }
            else {
                throw new ComponentInitializationException("Cannot lazily initialize singletons for non-modifiable proxy " + proxyContext.proxy);
            }
        }
    }

    private record LateSingletonContext<T>(Class<T> targetType, Proxy<T> proxy) {
    }
}
