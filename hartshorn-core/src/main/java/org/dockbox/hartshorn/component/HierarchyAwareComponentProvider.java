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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey.ComponentKeyView;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ModifiableComponentProcessingContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchyBindingFunction;
import org.dockbox.hartshorn.inject.binding.NativeBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.HashSetMultiMap;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

public class HierarchyAwareComponentProvider extends DefaultProvisionContext implements HierarchicalComponentProvider, ContextCarrier {

    private final transient ScopedProviderOwner owner;
    private final transient Scope scope;

    private final transient SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final transient Map<ComponentKeyView<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    public HierarchyAwareComponentProvider(final ScopedProviderOwner owner, final Scope scope) {
        this.owner = owner;
        this.scope = scope;
    }

    private <T> Option<ObjectContainer<T>> create(final ComponentKey<T> key) {
        try {
            final Option<ObjectContainer<T>> objectContainer = this.provide(key);
            if (objectContainer.present()) return objectContainer;

            return this.createContextualInstanceContainer(key);
        } catch (final ApplicationException e) {
            throw new ComponentInitializationException("Failed to create component for key " + key, e);
        }
    }

    @Override
    public <C> BindingFunction<C> bind(final ComponentKey<C> key) {
        if (key.scope() != this.scope && key.scope() != Scope.DEFAULT_SCOPE) {
            throw new IllegalArgumentException("Cannot bind to a different scope");
        }
        final BindingHierarchy<C> hierarchy = this.hierarchy(key);

        final ContextKey<ScopeModuleContext> scopeModuleContextKey = ContextKey.builder(ScopeModuleContext.class)
                .fallback(ScopeModuleContext::new)
                .build();
        final Option<ScopeModuleContext> scopeModuleContext = this.applicationContext().first(scopeModuleContextKey);

        if (scopeModuleContext.absent() && !(this.scope instanceof ApplicationContext)) {
            throw new IllegalModificationException("Cannot add binding to non-application hierarchy without a module context");
        }

        return new HierarchyBindingFunction<>(hierarchy, this, this.singletonCache, this.owner.instanceFactory(), this.scope, scopeModuleContext.orNull());
    }

    @Override
    public <C> Binder bind(final BindingHierarchy<C> hierarchy) {
        this.hierarchies.put(hierarchy.key().view(), hierarchy);
        return this;
    }

    public <T> Option<ObjectContainer<T>> provide(final ComponentKey<T> key) throws ApplicationException {
        final Option<BindingHierarchy<T>> hierarchy = Option.of(this.hierarchy(key, true));
        if (hierarchy.present()) {
            // Will continue going through each provider until a provider was successful or no other providers remain
            for (final Provider<T> provider : hierarchy.get().providers()) {
                final Option<ObjectContainer<T>> provided = provider.provide(this.applicationContext());
                if (provided.present()) return provided;
            }
        }
        return Option.empty();
    }

    protected <T> T process(final ComponentKey<T> key, final ObjectContainer<T> objectContainer, final ComponentContainer<?> container) {
        if (container != null && !container.permitsProcessing()) {
            return objectContainer.instance();
        }

        ModifiableComponentProcessingContext<T> processingContext = this.prepareProcessingContext(key, objectContainer.instance(), container);
        objectContainer.processed(true);

        processingContext = this.process(processingContext);
        return processingContext.instance();
    }

    protected <T> ModifiableComponentProcessingContext<T> prepareProcessingContext(final ComponentKey<T> key, final T instance, final ComponentContainer<?> container) {
        final ModifiableComponentProcessingContext<T> processingContext = new ModifiableComponentProcessingContext<>(this.applicationContext(), key, instance);

        if (container != null) {
            processingContext.put(ComponentKey.of(ComponentContainer.class), container);
            if (container.permitsProxying()) {
                final StateAwareProxyFactory<T> factory = this.applicationContext().environment().factory(key.type());

                if (instance != null) {
                    factory.trackState(false);
                    factory.advisors().type().delegateAbstractOnly(instance);
                    factory.trackState(true);
                }
                processingContext.put(ComponentKey.of(ProxyFactory.class), factory);
            }
        }
        return processingContext;
    }

    protected <T> ModifiableComponentProcessingContext<T> process(final ModifiableComponentProcessingContext<T> processingContext) {
        final ComponentKey<T> key = processingContext.key();

        for (final Integer priority : this.owner.postProcessors().keySet()) {
            for (final ComponentPostProcessor postProcessor : this.owner.postProcessors().get(priority)) {
                final T modified = postProcessor.process(processingContext);
                if (modified != null) {
                    processingContext.instance(modified);
                }
            }
        }
        this.storeSingletons(key, processingContext.instance());
        return processingContext;
    }

    protected <T> void storeSingletons(final ComponentKey<T> key, final T instance) {
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

    public <T> Option<ObjectContainer<T>> createContextualInstanceContainer(final ComponentKey<T> key) throws ApplicationException {
        return new ContextDrivenProvider<>(key).provide(this.applicationContext());
    }

    @Override
    public <T> T get(final ComponentKey<T> componentKey) {
        if (componentKey.type() == ApplicationContext.class && StringUtilities.empty(componentKey.name())) {
            return TypeUtils.adjustWildcards(this.applicationContext(), Object.class);
        }

        if (this.singletonCache.contains(componentKey)) {
            return this.singletonCache.get(componentKey);
        }

        this.owner.componentLocator().validate(componentKey);

        final ObjectContainer<T> objectContainer = this.create(componentKey)
                .orElseGet(() -> new ObjectContainer<>(null, false));

        final T instance = objectContainer.instance();

        // If the object is already processed at this point, it means that the object container was
        // reused, so we don't need to process it again. Note that this is not the same as the object
        // being a singleton, which is handled by the singleton cache. It is however possible that the
        // reuse of the object container is due to it being a singleton, but we do not know that for
        // sure.
        if (objectContainer.processed()) {
            return instance;
        }

        return this.processInstance(componentKey, objectContainer, instance);
    }

    private <T> T processInstance(final ComponentKey<T> componentKey, final ObjectContainer<T> objectContainer, T instance) {
        Class<? extends T> type = componentKey.type();
        if (instance != null) {
            type = TypeUtils.adjustWildcards(instance.getClass(), Class.class);
        }

        final Option<ComponentContainer<?>> container = this.owner.componentLocator().container(type);
        instance = container.present()
                ? this.getManagedComponent(componentKey, objectContainer, container)
                : this.getUnmanagedComponent(componentKey, objectContainer, type);

        if (instance == null) {
            throw new ComponentResolutionException("No component found for key " + componentKey);
        }

        return this.finishComponentRequest(componentKey, instance);
    }

    private <T> T finishComponentRequest(final ComponentKey<T> componentKey, T instance) {
        this.storeSingletons(componentKey, instance);

        // Inject properties if applicable
        if (componentKey.enable()) {
            try {
                instance = this.owner.postConstructor().doPostConstruct(instance);
            } catch (final ApplicationException e) {
                throw new ComponentInitializationException("Failed to perform post-construction on component with key " + componentKey, e);
            }
        }

        return instance;
    }

    private <T> T getManagedComponent(final ComponentKey<T> componentKey, final ObjectContainer<T> objectContainer,
                                      final Option<ComponentContainer<?>> container) {
        // Will only mark the object container as processed if the component container permits
        // processing.
        return this.process(componentKey, objectContainer, container.get());
    }

    private <T> T getUnmanagedComponent(final ComponentKey<T> componentKey, final ObjectContainer<T> objectContainer, final Class<? extends T> type) {
        final TypeView<? extends T> typeView = this.applicationContext().environment().introspect(type);
        if (typeView.annotations().has(Component.class)) {
            throw new ApplicationRuntimeException("Component " + typeView.name() + " is not registered");
        }

        final T instance = objectContainer.instance();
        if (instance != null) {
            final TypeView<Object> instanceType = this.applicationContext().environment().introspect(instance);
            for (final FieldView<?, ?> field : instanceType.fields().annotatedWith(Inject.class)) {
                this.applicationContext().log().warn("Field {} of {} is not injected, because {} is not a managed component.", field.name(), instanceType.name(), instanceType.name());
            }
        }

        this.process(componentKey, objectContainer, null);
        return instance;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.owner.applicationContext();
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final ComponentKey<T> key) {
        return this.hierarchy(key, false);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        final MultiMap<Scope, BindingHierarchy<?>> map = new HashSetMultiMap<>();
        map.putAll(this.scope, this.hierarchies.values());
        return map;
    }

    private <T> BindingHierarchy<T> hierarchy(final ComponentKey<T> key, final boolean useParentIfAbsent) {
        // If the scope is default, it means that the binding is not explicitly scoped, so it can be
        // installed in any scope. If our active scope is the active application context, it means
        // the requested scope is not installed, so we can fall back to the application scope.
        if (key.scope() != this.scope && key.scope() != Scope.DEFAULT_SCOPE && this.scope != this.applicationContext()) {
            throw new IllegalArgumentException("Cannot create a binding hierarchy for a component key with a different scope");
        }

        final BindingHierarchy<?> hierarchy = this.hierarchies.computeIfAbsent(key.view(), componentKey -> {
            // If we don't have an explicit hierarchy on the key, we can try to use the hierarchy of
            // the application context. This is useful for components that are not explicitly scoped,
            // but are still accessed through a scope.
            if (useParentIfAbsent && this.owner.applicationProvider() != this) {
                return this.owner.applicationProvider().hierarchy(key);
            }
            return new NativeBindingHierarchy<>(key, this.applicationContext());
        });
        final BindingHierarchy<T> adjustedHierarchy = TypeUtils.adjustWildcards(hierarchy, BindingHierarchy.class);
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (adjustedHierarchy instanceof ContextWrappedHierarchy) return adjustedHierarchy;
        else
            return new ContextWrappedHierarchy<>(adjustedHierarchy, this.applicationContext(), updated -> this.hierarchies.put(key.view(), updated));
    }
}
