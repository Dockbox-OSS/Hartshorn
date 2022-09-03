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

package org.dockbox.hartshorn.component;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ProcessingPhase;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.ComponentInstanceFactory;
import org.dockbox.hartshorn.inject.binding.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchyBindingFunction;
import org.dockbox.hartshorn.inject.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

public class HierarchicalApplicationComponentProvider extends DefaultContext implements StandardComponentProvider, ContextCarrier {

    private final transient ApplicationContext applicationContext;
    private final transient ComponentLocator locator;
    private final transient MetaProvider metaProvider;

    private final transient SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final transient Map<Key<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    private final transient MultiMap<Integer, ComponentPostProcessor> postProcessors = new ConcurrentSetTreeMultiMap<>();
    private final transient ComponentInstanceFactory factory;
    private final transient ComponentPostConstructor postConstructor;

    public HierarchicalApplicationComponentProvider(InitializingContext context) {
        this.applicationContext = context.applicationContext();
        this.locator = context.componentLocator();
        this.metaProvider = context.metaProvider();
        this.postConstructor = context.componentPostConstructor();
        this.factory = this::raw;
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.get(key, true);
    }

    @Override
    public <T> T get(final Key<T> key, final boolean enable) {
        if (this.singletonCache.contains(key)) {
            return this.singletonCache.get(key);
        }

        this.locator.validate(key);

        final ObjectContainer<T> objectContainer = this.create(key)
                .rethrowUnchecked()
                .orElse(() -> new ObjectContainer<>(null, false))
                .get();

        T instance = objectContainer.instance();

        // If the object is already processed at this point, it means that the object container was
        // reused, so we don't need to process it again. Note that this is not the same as the object
        // being a singleton, which is handled by the singleton cache. It is however possible that the
        // reuse of the object container is due to it being a singleton, but we do not know that for
        // sure.
        if (objectContainer.processed()) {
            return instance;
        }

        TypeContext<? extends T> type = key.type();
        if (instance != null) {
            type = TypeContext.of(instance);
        }

        final Result<ComponentContainer> container = this.locator.container(type);
        if (container.present()) {
            // Will only mark the object container as processed if the component container permits
            // processing.
            instance = this.process(key, objectContainer, container.get());
        }
        else {
            this.verify(instance);
            objectContainer.processed(true);
        }

        this.storeSingletons(key, instance);

        // Inject properties if applicable
        if (enable) {
            try {
                instance = postConstructor.doPostConstruct(instance);
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }

        return instance;
    }

    @Nullable
    private <T> Result<ObjectContainer<T>> create(final Key<T> key) {
        return this.provide(key)
                .rethrowUnchecked()
                .orFlat(() -> this.raw(key));
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C> BindingFunction<C> bind(final Key<C> key) {
        final BindingHierarchy<C> hierarchy = this.hierarchy(key);
        return new HierarchyBindingFunction<>(hierarchy, this, this.singletonCache, this.factory);
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        final BindingHierarchy<T> hierarchy = (BindingHierarchy<T>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this.applicationContext()));
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (hierarchy instanceof ContextWrappedHierarchy) return hierarchy;
        else return new ContextWrappedHierarchy<>(hierarchy, this.applicationContext(), updated -> this.hierarchies.put(key, updated));
    }

    public <T> Result<ObjectContainer<T>> provide(final Key<T> key) {
        return Result.of(key)
                .map(this::hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Result<ObjectContainer<T>> provided = provider.provide(this.applicationContext()).rethrowUnchecked();
                        if (provided.present()) return provided;
                    }
                    return Result.empty();
                });
    }

    protected <T> T process(final Key<T> key, final ObjectContainer<T> objectContainer, final ComponentContainer container) {
        final boolean doProcess = container.permitsProcessing();

        T instance = objectContainer.instance();
        final ComponentProcessingContext processingContext = this.prepareProcessingContext(key, instance, container);

        if (doProcess) {
            objectContainer.processed(true);

            // Modify the instance during phase 1. This allows discarding the existing instance and replacing it with a new instance.
            // See ServiceOrder#PHASE_1
            instance = this.process(key, instance, ProcessingOrder.INITIALIZING, processingContext);

            // Modify the instance during phase 2. This does not allow discarding the existing instance.
            // See ServiceOrder#PHASE_2
            instance = this.process(key, instance, ProcessingOrder.MODIFYING, processingContext);
        }
        return instance;
    }

    protected <T> ComponentProcessingContext prepareProcessingContext(final Key<T> key, final T instance, final ComponentContainer container) {
        final ComponentProcessingContext processingContext = new ComponentProcessingContext(this.applicationContext(), key);
        processingContext.put(Key.of(ComponentContainer.class), container);

        if (container.permitsProxying()) {
            final StateAwareProxyFactory<T, ?> factory = this.applicationContext().environment().manager().factory(key.type());

            if (instance != null) {
                factory.trackState(false);
                factory.delegateAbstract(instance);
                factory.trackState(true);
            }
            processingContext.put(Key.of(ProxyFactory.class), factory);
        }
        return processingContext;
    }

    protected <T> T process(final Key<T> key, final T instance, final ProcessingPhase phase, final ComponentProcessingContext processingContext) {
        T result = instance;
        processingContext.phase(phase);

        for (final Integer priority : this.postProcessors.keySet()) {
            if (phase.test(priority)) {
                for (final ComponentPostProcessor postProcessor : this.postProcessors.get(priority)) {

                    if (postProcessor.preconditions(this.applicationContext(), key, result, processingContext)) {
                        final T modified = postProcessor.process(this.applicationContext(), key, result, processingContext);

                        if (processingContext.phase() != phase) {
                            throw new IllegalPhaseModificationException(postProcessor, phase, processingContext.phase());
                        }

                        checkForIllegalModification:
                        if (!phase.modifiable() && instance != modified) {
                            if (modified instanceof Proxy) {
                                final Proxy<T> proxy = (Proxy<T>) modified;
                                final boolean delegateMatches = proxy.manager().delegate().orNull() == instance;

                                if (delegateMatches)
                                    break checkForIllegalModification;
                            }
                            throw new IllegalComponentModificationException(key.type().name(), priority, postProcessor);
                        }

                        result = modified;
                    }
                }
            }
        }
        this.storeSingletons(key, result);
        return result;
    }

    protected <T> void storeSingletons(final Key<T> key, final T instance) {
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (this.metaProvider.singleton(key.type()) || this.metaProvider.singleton(TypeContext.unproxy(this.applicationContext(), instance)))) {
            this.singletonCache.put(key, instance);
        }
    }

    public void postProcessor(final ComponentPostProcessor postProcessor) {
        this.postProcessors.put(postProcessor.order(), postProcessor);
        this.singletonCache.put(Key.of((Class<ComponentPostProcessor>) postProcessor.getClass()), postProcessor);
    }

    public <T> Result<ObjectContainer<T>> raw(final Key<T> key) {
        return new ContextDrivenProvider<>(key.type()).provide(this.applicationContext()).rethrowUnchecked();
    }

    private void verify(final Object instance) {
        if (instance != null) {
            final TypeContext<Object> type = TypeContext.unproxy(this.applicationContext(), instance);
            for (final FieldContext<?> field : type.fields(Inject.class)) {
                this.applicationContext().log().warn("Field {} of {} is not injected, because {} is not a managed component.", field.name(), type.name(), type.name());
            }
        }
    }
}
