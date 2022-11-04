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
import org.dockbox.hartshorn.component.processing.ModifiableComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.component.processing.ProcessingPhase;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.Key;
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
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

public class HierarchicalApplicationComponentProvider extends DefaultContext implements StandardComponentProvider, ContextCarrier {

    private final transient ApplicationContext applicationContext;
    private final transient ComponentLocator locator;

    private final transient SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final transient Map<Key<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    private final transient MultiMap<Integer, ComponentPostProcessor> postProcessors = new ConcurrentSetTreeMultiMap<>();
    private final transient ComponentInstanceFactory factory;
    private final transient ComponentPostConstructor postConstructor;

    public HierarchicalApplicationComponentProvider(final InitializingContext context) {
        this.applicationContext = context.applicationContext();
        this.locator = context.componentLocator();
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

        Class<? extends T> type = key.type();
        if (instance != null) {
            type = (Class<T>) instance.getClass();
        }

        final Result<ComponentContainer> container = this.locator.container(type);
        if (container.present()) {
            // Will only mark the object container as processed if the component container permits
            // processing.
            instance = this.process(key, objectContainer, container.get());
        }
        else {
            final TypeView<? extends T> typeView = this.applicationContext().environment().introspect(type);
            if (typeView.annotations().has(Component.class)) {
                throw new ApplicationRuntimeException("Component " + typeView.name() + " is not registered");
            }

            if (instance != null) {
                final TypeView<Object> instanceType = this.applicationContext().environment().introspect(instance);
                for (final FieldView<?, ?> field : instanceType.fields().annotatedWith(Inject.class)) {
                    this.applicationContext().log().warn("Field {} of {} is not injected, because {} is not a managed component.", field.name(), instanceType.name(), instanceType.name());
                }
            }

            objectContainer.processed(true);
        }

        this.storeSingletons(key, instance);

        // Inject properties if applicable
        if (enable) {
            try {
                instance = this.postConstructor.doPostConstruct(instance);
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
        if (!container.permitsProcessing()) return objectContainer.instance();

        ModifiableComponentProcessingContext<T> processingContext = this.prepareProcessingContext(key, objectContainer.instance(), container);
        objectContainer.processed(true);

        // Modify the instance during phase 1. This allows discarding the existing instance and replacing it with a new instance.
        // See ServiceOrder#PHASE_1
        processingContext = this.process(ProcessingOrder.INITIALIZING, processingContext);

        // Modify the instance during phase 2. This does not allow discarding the existing instance.
        // See ServiceOrder#PHASE_2
        processingContext = this.process(ProcessingOrder.MODIFYING, processingContext);

        return processingContext.instance();
    }

    protected <T> ModifiableComponentProcessingContext<T> prepareProcessingContext(final Key<T> key, final T instance, final ComponentContainer container) {
        final ModifiableComponentProcessingContext<T> processingContext = new ModifiableComponentProcessingContext<>(this.applicationContext(), key, instance);
        processingContext.put(Key.of(ComponentContainer.class), container);

        if (container.permitsProxying()) {
            final StateAwareProxyFactory<T, ?> factory = this.applicationContext().environment().factory(key.type());

            if (instance != null) {
                factory.trackState(false);
                factory.delegateAbstract(instance);
                factory.trackState(true);
            }
            processingContext.put(Key.of(ProxyFactory.class), factory);
        }
        return processingContext;
    }

    protected <T> ModifiableComponentProcessingContext<T> process(final ProcessingPhase phase, final ModifiableComponentProcessingContext<T> processingContext) {
        final Key<T> key = processingContext.key();
        final T instance = processingContext.instance();

        processingContext.phase(phase);

        for (final Integer priority : this.postProcessors.keySet()) {
            if (phase.test(priority)) {
                for (final ComponentPostProcessor postProcessor : this.postProcessors.get(priority)) {
                    final T modified = postProcessor.process(processingContext);

                    if (processingContext.phase() != phase) {
                        throw new IllegalPhaseModificationException(postProcessor, phase, processingContext.phase());
                    }

                    if (instance != modified) {

                        checkForIllegalModification:
                        if (!phase.modifiable()) {
                            if (modified instanceof Proxy) {
                                final Proxy<T> proxy = (Proxy<T>) modified;
                                final boolean delegateMatches = proxy.manager().delegate().orNull() == instance;

                                if (delegateMatches)
                                    break checkForIllegalModification;
                            }
                            throw new IllegalComponentModificationException(key.type().getSimpleName(), priority, postProcessor);
                        }

                        processingContext.instance(modified);
                    }
                }
            }
        }
        this.storeSingletons(key, processingContext.instance());
        return processingContext;
    }

    protected <T> void storeSingletons(final Key<T> key, final T instance) {
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null) {
            final TypeView<T> keyType = this.applicationContext().environment().introspect(key.type());
            boolean isSingleton = this.applicationContext().environment().singleton(keyType);

            // Same effect as ||, but this is more readable. It's important to only check the instance type if the key doesn't already match,
            // as introspecting and unproxying the instance can be expensive when it's not necessary.
            if (!isSingleton) {
                final TypeView<T> instanceType = this.applicationContext().environment().introspect(instance);
                isSingleton = this.applicationContext().environment().singleton(instanceType);
            }

            if (isSingleton) {
                this.singletonCache.put(key, instance);
            }
        }
    }

    public void postProcessor(final ComponentPostProcessor postProcessor) {
        this.postProcessors.put(postProcessor.order(), postProcessor);
        this.singletonCache.put(Key.of((Class<ComponentPostProcessor>) postProcessor.getClass()), postProcessor);
    }

    public <T> Result<ObjectContainer<T>> raw(final Key<T> key) {
        return new ContextDrivenProvider<>(key.type()).provide(this.applicationContext()).rethrowUnchecked();
    }
}
