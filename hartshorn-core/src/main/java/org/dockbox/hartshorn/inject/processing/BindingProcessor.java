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
import org.dockbox.hartshorn.inject.CyclingConstructorAnalyzer;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.proxy.ModifiableProxyManager;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementContext;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.ObtainableElement;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.reflect.TypedElementContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

public class BindingProcessor {

    private final Set<LateSingletonContext> proxiesToInitialize = ConcurrentHashMap.newKeySet();

    public void process(final ProviderListContext context, final ApplicationContext applicationContext) throws ApplicationException {
        final List<Entry<Key<?>, Collection<AnnotatedElementContext<?>>>> entries = context.entries().stream()
                .sorted(Comparator.comparingInt(entry -> {
                    return entry.getValue().stream().mapToInt(element -> {
                        if (element instanceof MethodContext<?, ?> method) {
                            return ((MethodContext<?, ?>) element).parameterCount();
                        }
                        else if (element instanceof FieldContext<?>) {
                            return 1;
                        }
                        else return Integer.MAX_VALUE;
                    }).min().orElse(-1);
                })).toList();

        for (final Entry<Key<?>, Collection<AnnotatedElementContext<?>>> entry : entries) {
            final Key<?> key = entry.getKey();

            for (final AnnotatedElementContext<?> element : entry.getValue()) {
                if (element instanceof MethodContext) {
                    this.process(key, (MethodContext<?, ?>) element, applicationContext, context);
                }
                else if (element instanceof FieldContext) {
                    this.process(key, (FieldContext<?>) element, applicationContext, context);
                }
            }
        }
    }

    private <R, E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> void process(final Key<R> key, final E element, final ApplicationContext applicationContext) throws ApplicationException {
        final ProviderListContext providerContext = applicationContext.first(ProviderListContext.class).orNull();
        this.process(key, element, applicationContext, providerContext);
    }

    private <R, E extends AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>> void process(final Key<R> key, final E element, final ApplicationContext applicationContext, final ProviderListContext context) throws ApplicationException {
        final MetaProvider metaProvider = applicationContext.get(MetaProvider.class);
        final ConditionMatcher conditionMatcher = applicationContext.get(ConditionMatcher.class);
        final Provider annotation = element.annotation(Provider.class).get();

        final boolean singleton = metaProvider.singleton(key.type()) || element.annotation(Singleton.class).present();

        if (conditionMatcher.match(element)) {
            if (element.type().is(Class.class))
                this.processClassBinding(applicationContext, (ObtainableElement<Class<R>>) element, key, singleton, annotation, context);
            else if ((element.type().is(TypeContext.class)))
                this.processTypeBinding(applicationContext, (ObtainableElement<TypeContext<R>>) element, key, singleton, annotation, context);
            else
                this.processInstanceBinding(applicationContext, (ObtainableElement<R>) element, key, singleton, annotation);
        }
    }

    private void checkRequiredTypeBindings(final ApplicationContext applicationContext, final ObtainableElement<?> element, final ProviderListContext providerContext, final TypeContext<?> target) throws ApplicationException {
        final Result<? extends ConstructorContext<?>> optimalConstructor = CyclingConstructorAnalyzer.findConstructor(target).rethrowUnchecked();
        if (optimalConstructor.present()) {
            final ConstructorContext<?> constructor = optimalConstructor.get();
            for (final TypeContext<?> parameterType : constructor.parameterTypes()) {
                final Key<?> key = Key.of(parameterType);
                if (providerContext.containsKey(key)) {
                    for (final AnnotatedElementContext<?> context : providerContext.get(key)) {
                        this.process(key, (AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>) context, applicationContext);
                    }
                }
            }
        }
    }

    private void checkRequiredInjectionPoints(final ApplicationContext applicationContext, final TypeContext<?> target, final ObtainableElement<?> element, final ProviderListContext providerContext) throws ApplicationException {
        for (final FieldContext<?> field : target.fields(Inject.class)) {
            final TypeContext<?> fieldType = field.type();
            final Key<?> key = Key.of(fieldType);
            this.processIfExists(key, providerContext, applicationContext);
        }

        for (final MethodContext<?, ?> method : target.methods(Inject.class)) {
            for (final ParameterContext<?> parameter : method.parameters()) {
                final Key<?> key = Key.of(parameter.type()).name(parameter.annotation(Named.class).orNull());
                this.processIfExists(key, providerContext, applicationContext);
            }
        }
    }

    private void processIfExists(final Key<?> key, final ProviderListContext context, final ApplicationContext applicationContext) throws ApplicationException {
        if (context.containsKey(key)) {
            for (final AnnotatedElementContext<?> element : context.get(key)) {
                this.process(key, (AnnotatedElementContext<?> & ObtainableElement<?> & TypedElementContext<?>) element, applicationContext);
            }
        }
    }

    private <R> void processInstanceBinding(
            final ApplicationContext context, final ObtainableElement<R> element, final Key<R> key, final boolean singleton, final Provider annotation
    ) {
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());
        final Supplier<R> supplier = () -> element.obtain(context).rethrowUnchecked().orNull();

        if (singleton) {
            if (annotation.lazy()) function.lazySingleton(supplier);
            else function.singleton(supplier.get());
        }
        else function.to(supplier);
    }

    private <R, C extends Class<R>> void processClassBinding(
            final ApplicationContext context, final ObtainableElement<C> element, final Key<R> key, boolean singleton, final Provider annotation,
            final ProviderListContext providerListContext) throws ApplicationException {

        final C targetType = element.obtain(context).rethrowUnchecked().orNull();
        final MetaProvider metaProvider = context.get(MetaProvider.class);
        final TypeContext<R> typeContext = TypeContext.of(targetType);

        this.checkRequiredTypeBindings(context, element, providerListContext, typeContext);

        singleton = singleton || typeContext.annotation(Singleton.class).present() || metaProvider.singleton(typeContext);
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());

        if (singleton) {
            final boolean lazy = annotation.lazy() || context.get(ComponentLocator.class).container(typeContext).map(ComponentContainer::lazy).or(false);
            if (lazy) function.lazySingleton(() -> context.get(targetType));
            else {
                final Proxy<R> proxy = (Proxy<R>) context.environment().manager().factory(targetType)
                        .proxy()
                        .rethrowUnchecked()
                        .orThrow(() -> new IllegalStateException("Could create temporary empty proxy for " + targetType.getSimpleName() + ", any errors may be displayed above."));
                this.proxiesToInitialize.add(new LateSingletonContext<>(targetType, element, proxy));
            }
        }
        else function.to(targetType);
    }

    private <R, C extends TypeContext<R>> void processTypeBinding(
            final ApplicationContext context, final ObtainableElement<C> element, final Key<R> key, boolean singleton, final Provider annotation,
            final ProviderListContext providerListContext) throws ApplicationException {

        final C targetType = element.obtain(context).rethrowUnchecked().orNull();
        final MetaProvider metaProvider = context.get(MetaProvider.class);

        this.checkRequiredTypeBindings(context, element, providerListContext, targetType);

        singleton = singleton || targetType.annotation(Singleton.class).present() || metaProvider.singleton(targetType);
        final BindingFunction<R> function = context.bind(key).priority(annotation.priority());

        if (singleton) {
            final boolean lazy = annotation.lazy() || context.get(ComponentLocator.class).container(targetType).map(ComponentContainer::lazy).or(false);
            if (lazy) function.lazySingleton(() -> context.get(targetType));
            else {
                final Proxy<R> proxy = (Proxy<R>) context.environment().manager().factory(targetType)
                        .proxy()
                        .rethrowUnchecked()
                        .orThrow(() -> new IllegalStateException("Could create temporary empty proxy for " + targetType.name() + ", any errors may be displayed above."));
                this.proxiesToInitialize.add(new LateSingletonContext<>(targetType.type(), element, proxy));
            }
        }
        else function.to(targetType);
    }

    public void finalizeProxies(final ApplicationContext applicationContext) throws ApplicationException {
        if (this.proxiesToInitialize.isEmpty()) return;

        final ProviderListContext context = applicationContext.first(ProviderListContext.class).orNull();

        for (final LateSingletonContext proxyContext : new ArrayList<>(this.proxiesToInitialize)) {
            this.proxiesToInitialize.remove(proxyContext);
            this.checkRequiredInjectionPoints(applicationContext, TypeContext.of(proxyContext.targetType), proxyContext.element, context);

            final Object instance = applicationContext.get(proxyContext.targetType);
            if (proxyContext.proxy.manager() instanceof ModifiableProxyManager modifiableProxyManager) {
                modifiableProxyManager.delegate(instance);
            }
            else {
                throw new IllegalStateException("Cannot lazily initialize singletons for non-modifiable proxy " + proxyContext.proxy);
            }
        }
    }

    private static class LateSingletonContext<T> {
        private final Class<T> targetType;
        private final ObtainableElement<?> element;
        private final Proxy<T> proxy;

        public LateSingletonContext(final Class<T> targetType, final ObtainableElement<?> element, final Proxy<T> proxy) {
            this.targetType = targetType;
            this.element = element;
            this.proxy = proxy;
        }
    }
}
