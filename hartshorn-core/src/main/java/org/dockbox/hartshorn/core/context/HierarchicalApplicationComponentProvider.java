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

package org.dockbox.hartshorn.core.context;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.core.CustomMultiTreeMap;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.MultiMap;
import org.dockbox.hartshorn.core.binding.BindingHierarchy;
import org.dockbox.hartshorn.core.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.core.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.core.binding.Provider;
import org.dockbox.hartshorn.core.binding.Providers;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.ProxyFactory;
import org.dockbox.hartshorn.core.proxy.StateAwareProxyFactory;
import org.dockbox.hartshorn.core.services.ComponentContainer;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;
import org.dockbox.hartshorn.core.services.ComponentProcessingContext;
import org.dockbox.hartshorn.core.services.ProcessingOrder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HierarchicalApplicationComponentProvider extends DefaultContext implements StandardComponentProvider, ContextCarrier {

    private final ApplicationContext applicationContext;

    private final transient Map<Key<?>, Object> singletons = new ConcurrentHashMap<>();
    private final transient Map<Key<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    protected final transient MultiMap<Integer, ComponentPostProcessor<?>> postProcessors = new CustomMultiTreeMap<>(ConcurrentHashMap::newKeySet);

    public HierarchicalApplicationComponentProvider(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.get(key, true);
    }

    @Override
    public <T> T get(final Key<T> key, final boolean enable) {
        if (this.singletons.containsKey(key)) return (T) this.singletons.get(key);
        this.applicationContext().locator().validate(key);

        T instance = this.create(key);

        final Exceptional<ComponentContainer> container = this.applicationContext().locator().container(key.type());
        if (container.present()) {
            instance = this.process(key, instance, container.get());
        }
        else {
            this.populateAndStore(key, instance);
        }

        // Inject properties if applicable
        if (enable) {
            try {
                this.applicationContext().enable(instance);
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }

        // May be null, but we have used all possible injectors, it's up to the developer now
        return instance;
    }

    @Nullable
    public <T> T create(final Key<T> key) {
        final Exceptional<T> provision = this.provide(key).rethrowUnchecked();
        if (provision.present())
            return provision.get();

        final TypeContext<T> type = key.type();

        final Exceptional<T> raw = Exceptional.of(() -> this.raw(type)).rethrowUnchecked();
        if (raw.present())
            return raw.get();

        // If the component is functional and permits proxying, a post processor will be able to proxy the instance
        return null;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public <C> void bind(final Key<C> contract, final Supplier<C> supplier) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(supplier)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final Class<? extends T> implementation) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(implementation)));
    }

    @Override
    public <C, T extends C> void bind(final Key<C> contract, final T instance) {
        this.inHierarchy(contract, hierarchy -> hierarchy.add(Providers.of(instance)));
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        final BindingHierarchy<T> hierarchy = (BindingHierarchy<T>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this.applicationContext()));
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (hierarchy instanceof ContextWrappedHierarchy) return hierarchy;
        else return new ContextWrappedHierarchy<>(hierarchy, this.applicationContext(), updated -> this.hierarchies.put(key, updated));
    }

    @Override
    public <T, C extends T> void singleton(final Key<T> key, final C instance) {
        if (instance == null) {
            throw new IllegalStateException("Singleton %s was not created".formatted(key.type().name()));
        }
        this.singletons.put(key, instance);
    }

    @Override
    public <C> void inHierarchy(final Key<C> key, final Consumer<BindingHierarchy<C>> consumer) {
        final BindingHierarchy<C> hierarchy = (BindingHierarchy<C>) this.hierarchies.getOrDefault(key, new NativeBindingHierarchy<>(key, this.applicationContext()));
        consumer.accept(hierarchy);
        this.hierarchies.put(key, hierarchy);
    }

    public <T> Exceptional<T> provide(final Key<T> key) {
        return Exceptional.of(key)
                .map(this::hierarchy)
                .flatMap(hierarchy -> {
                    // Will continue going through each provider until a provider was successful or no other providers remain
                    for (final Provider<T> provider : hierarchy.providers()) {
                        final Exceptional<T> provided = provider.provide(this.applicationContext()).rethrowUnchecked();
                        if (provided.present()) return provided;
                    }
                    return Exceptional.empty();
                });
    }

    protected <T> T process(final Key<T> key, T instance, final ComponentContainer container) {
        final boolean doProcess = container.permitsProcessing();

        final ComponentProcessingContext processingContext = this.prepareProcessingContext(key, instance, container);

        // Modify the instance during phase 1. This allows discarding the existing instance and replacing it with a new instance.
        // See ServiceOrder#PHASE_1
        if (doProcess) instance = this.process(key, instance, ProcessingOrder.PHASE_1, true, processingContext);

        instance = this.populateAndStore(key, instance);

        // Modify the instance during phase 2. This does not allow discarding the existing instance.
        // See ServiceOrder#PHASE_2
        if (doProcess) this.process(key, instance, ProcessingOrder.PHASE_2, false, processingContext);

        if (container.permitsProxying()) {
            instance = this.finalize(key, instance, processingContext);
        }

        return instance;
    }

    protected <T> ComponentProcessingContext prepareProcessingContext(final Key<T> key, final T instance, final ComponentContainer container) {
        final ComponentProcessingContext processingContext = new ComponentProcessingContext(this.applicationContext());
        if (container.permitsProxying()) {
            final StateAwareProxyFactory<T, ?> factory = this.applicationContext().environment().manager().factory(key.type());

            if (instance != null) {
                factory.trackState(false);
                factory.delegate(instance);
                factory.trackState(true);
            }
            processingContext.put(Key.of(ProxyFactory.class), factory);
        }
        return processingContext;
    }

    protected <T> T finalize(final Key<T> key, final T instance, final ComponentProcessingContext context) {
        if (context.containsKey(Key.of(ProxyFactory.class))) {
            final ProxyFactory<T, ?> factory = context.get(Key.of(ProxyFactory.class));
            try {
                if (((StateAwareProxyFactory) factory).modified() || (instance == null && key.type().isAbstract())) {
                    return factory.proxy().or(instance);
                } else {
                    return instance;
                }
            }
            catch (final ApplicationException e) {
                ExceptionHandler.unchecked(e);
            }
        }
        return instance;
    }

    protected <T> T process(final Key<T> key, final T instance, final Predicate<Integer> orders, final boolean modifiable, final ComponentProcessingContext processingContext) {
        T result = instance;
        for (final Integer priority : this.postProcessors.keySet()) {
            if (orders.test(priority)) {
                for (final ComponentPostProcessor<?> postProcessor : this.postProcessors.get(priority)) {
                    if (postProcessor.preconditions(this.applicationContext(), key, result)) {
                        final T modified = postProcessor.process(this.applicationContext(), key, result, processingContext);
                        if (modifiable) result = modified;
                        else if (!modifiable && modified != instance) {
                            throw new IllegalStateException(("Component %s was modified during phase with priority %s by %s. " +
                                    "Component processors are only able to discard existing instances in phases with priority < 0").formatted(key.type().name(), priority, TypeContext.of(postProcessor).name()));
                        }
                    }
                }
            }
        }
        return result;
    }

    protected <T> T populateAndStore(final Key<T> key, final T instance) {
        final MetaProvider meta = this.applicationContext().meta();
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null && (meta.singleton(key.type()) || meta.singleton(TypeContext.unproxy(this.applicationContext(), instance))))
            this.singletons.put(key, instance);

        // Recreating field instances ensures all fields are created through bootstrapping, allowing injection
        // points to apply correctly
        return this.applicationContext().populate(instance);
    }

    public void postProcessor(final ComponentPostProcessor<?> postProcessor) {
        this.postProcessors.put(postProcessor.order(), postProcessor);
    }

    public <T> T raw(final TypeContext<T> type) {
        return Providers.of(type).provide(this.applicationContext()).rethrowUnchecked().orNull();
    }
}
