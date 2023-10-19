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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey.ComponentKeyView;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ModifiableComponentProcessingContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentObjectContainer;
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
import org.dockbox.hartshorn.inject.binding.collection.SimpleComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.ContainerAwareComponentCollection;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class HierarchyAwareComponentProvider extends DefaultProvisionContext implements HierarchicalComponentProvider, ContextCarrier {

    private final transient ScopedProviderOwner owner;
    private final transient Scope scope;

    private final transient SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final transient Map<ComponentKeyView<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();

    public HierarchyAwareComponentProvider(ScopedProviderOwner owner, Scope scope) {
        this.owner = owner;
        this.scope = scope;
    }

    private <T> Option<ObjectContainer<T>> create(ComponentKey<T> key) {
        try {
            Option<ObjectContainer<T>> objectContainer = this.provide(key);
            if (objectContainer.present()) {
                return objectContainer;
            }

            return this.createContextualInstanceContainer(key);
        } catch (ApplicationException e) {
            throw new ComponentInitializationException("Failed to create component for key " + key, e);
        }
    }

    @Override
    public <C> BindingFunction<C> bind(ComponentKey<C> key) {
        if (key.scope() != this.scope && key.scope() != Scope.DEFAULT_SCOPE) {
            throw new IllegalArgumentException("Cannot bind to a different scope");
        }
        BindingHierarchy<C> hierarchy = this.hierarchy(key);

        ContextKey<ScopeModuleContext> scopeModuleContextKey = ContextKey.builder(ScopeModuleContext.class)
                .fallback(ScopeModuleContext::new)
                .build();
        Option<ScopeModuleContext> scopeModuleContext = this.applicationContext().first(scopeModuleContextKey);

        if (scopeModuleContext.absent() && !(this.scope instanceof ApplicationContext)) {
            throw new IllegalModificationException("Cannot add binding to non-application hierarchy without a module context");
        }

        return new HierarchyBindingFunction<>(hierarchy, this, this.singletonCache, this.owner.instanceFactory(), this.scope, scopeModuleContext.orNull());
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        this.hierarchies.put(hierarchy.key().view(), hierarchy);
        return this;
    }

    public <T> Option<ObjectContainer<T>> provide(ComponentKey<T> key) throws ApplicationException {
        Option<BindingHierarchy<T>> hierarchy = Option.of(this.hierarchy(key, true));
        if (hierarchy.present()) {
            // Will continue going through each provider until a provider was successful or no other providers remain
            for (Provider<T> provider : hierarchy.get().providers()) {
                Option<ObjectContainer<T>> provided = provider.provide(this.applicationContext());
                if (provided.present()) {
                    return provided;
                }
            }
        }
        return Option.empty();
    }

    protected <T> T process(ComponentKey<T> key, ObjectContainer<T> objectContainer, ComponentContainer<?> container) {
        if (container != null && !container.permitsProcessing()) {
            return objectContainer.instance();
        }

        ModifiableComponentProcessingContext<T> processingContext = this.prepareProcessingContext(key, objectContainer.instance(), container);
        objectContainer.processed(true);

        processingContext = this.process(processingContext);
        return processingContext.instance();
    }

    protected <E, T extends ContainerAwareComponentCollection<E>> T processCollection(ComponentKey<T> key, T collection) {
        ComponentKey<E> build = TypeUtils.adjustWildcards(key.mutable()
                .type(key.parameterizedType().parameters().get(0))
                .build(), ComponentKey.class);

        for(ObjectContainer<E> container : collection.containers()) {
            E processed = process(build, container, null);
        }
        return collection;
    }

    protected <T> ModifiableComponentProcessingContext<T> prepareProcessingContext(ComponentKey<T> key, T instance, ComponentContainer<?> container) {
        ModifiableComponentProcessingContext<T> processingContext = new ModifiableComponentProcessingContext<>(
                this.applicationContext(), key, instance, latest -> this.storeSingletons(key, latest));

        if (container != null) {
            processingContext.put(ComponentKey.of(ComponentContainer.class), container);
            if (container.permitsProxying()) {
                StateAwareProxyFactory<T> factory = this.applicationContext().environment().proxyOrchestrator().factory(key.type());

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

    protected <T> ModifiableComponentProcessingContext<T> process(ModifiableComponentProcessingContext<T> processingContext) {
        ComponentKey<T> key = processingContext.key();

        for (Integer priority : this.owner.postProcessors().keySet()) {
            for (ComponentPostProcessor postProcessor : this.owner.postProcessors().get(priority)) {
                postProcessor.process(processingContext);
            }
        }
        this.storeSingletons(key, processingContext.instance());
        return processingContext;
    }

    protected <T> void storeSingletons(ComponentKey<T> key, T instance) {
        // Ensure the order of resolution is to first resolve the instance singleton state, and only after check the type state.
        // Typically, the implementation decided whether it should be a singleton, so this cuts time complexity in half.
        if (instance != null) {
            TypeView<T> keyType = this.applicationContext().environment().introspector().introspect(key.type());
            boolean isSingleton = this.applicationContext().environment().singleton(keyType);

            // Same effect as ||, but this is more readable. It's important to only check the instance type if the key doesn't already match,
            // as introspecting and unproxying the instance can be expensive when it's not necessary.
            if (!isSingleton) {
                TypeView<T> instanceType = this.applicationContext().environment().introspector().introspect(instance);
                isSingleton = this.applicationContext().environment().singleton(instanceType);
            }

            if (isSingleton) {
                this.singletonCache.put(key, instance);
            }
        }
    }

    public <T> Option<ObjectContainer<T>> createContextualInstanceContainer(ComponentKey<T> key) throws ApplicationException {
        return new ContextDrivenProvider<>(key).provide(this.applicationContext());
    }

    @Override
    public <T> T get(ComponentKey<T> componentKey) {
        if (componentKey.type() == ApplicationContext.class && StringUtilities.empty(componentKey.name())) {
            return TypeUtils.adjustWildcards(this.applicationContext(), Object.class);
        }

        if (this.singletonCache.contains(componentKey)) {
            return this.singletonCache.get(componentKey).orNull();
        }

        this.owner.componentLocator().validate(componentKey);

        ObjectContainer<T> objectContainer = this.create(componentKey)
                .orElseGet(() -> new ComponentObjectContainer<>(null));

        T instance = objectContainer.instance();

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

    private <T> T processInstance(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, T instance) {
        Class<? extends T> type = componentKey.type();
        if (instance != null) {
            type = TypeUtils.adjustWildcards(instance.getClass(), Class.class);
        }

        Option<ComponentContainer<?>> container = this.owner.componentLocator().container(type);
        instance = container.present()
                ? this.getManagedComponent(componentKey, objectContainer, container)
                : this.getUnmanagedComponent(componentKey, objectContainer, type);

        if (instance == null) {
            throw new ComponentResolutionException("No component found for key " + componentKey);
        }

        return this.finishComponentRequest(componentKey, instance);
    }

    private <T> T finishComponentRequest(ComponentKey<T> componentKey, T instance) {
        this.storeSingletons(componentKey, instance);

        // Inject properties if applicable
        if (componentKey.enable()) {
            try {
                instance = this.owner.postConstructor().doPostConstruct(instance);
            } catch (ApplicationException e) {
                throw new ComponentInitializationException("Failed to perform post-construction on component with key " + componentKey, e);
            }
        }

        return instance;
    }

    private <T> T getManagedComponent(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer,
                                      Option<ComponentContainer<?>> container) {
        // Will only mark the object container as processed if the component container permits
        // processing.
        return this.process(componentKey, objectContainer, container.get());
    }

    private <T> T getUnmanagedComponent(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, Class<? extends T> type) {
        TypeView<? extends T> typeView = this.applicationContext().environment().introspector().introspect(type);
        if (typeView.annotations().has(Component.class)) {
            throw new ApplicationRuntimeException("Component " + typeView.name() + " is not registered");
        }
        if (ComponentCollection.class.isAssignableFrom(componentKey.type())) {
            if (ComponentCollection.class != componentKey.type()) {
                throw new IllegalArgumentException("Component collection key must be of type ComponentCollection, specific implementations are not supported");
            }
            ComponentCollection<Object> collection = processComponentCollection(
                    TypeUtils.adjustWildcards(componentKey, ComponentKey.class),
                    TypeUtils.adjustWildcards(objectContainer, ObjectContainer.class)
            );
            return TypeUtils.adjustWildcards(collection, componentKey.type());
        }
        return this.process(componentKey, objectContainer, null);
    }

    private <E, T extends ComponentCollection<E>> ComponentCollection<E> processComponentCollection(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer) {
        if (objectContainer.instance() == null) {
            return new SimpleComponentCollection<>(Set.of());
        }
        else if (objectContainer.instance() instanceof ContainerAwareComponentCollection<?> containerAwareComponentCollection) {

            ContainerAwareComponentCollection<E> collection = TypeUtils.adjustWildcards(
                    containerAwareComponentCollection,
                    ContainerAwareComponentCollection.class);

            ComponentKey<ContainerAwareComponentCollection<E>> key = TypeUtils.adjustWildcards(
                    componentKey,
                    ComponentKey.class);

            ContainerAwareComponentCollection<E> processed = this.processCollection(key, collection);
            return new SimpleComponentCollection<>(Set.copyOf(processed));
        }
        else {
            throw new IllegalArgumentException("Component collection from provider must be of type ContainerAwareComponentCollection");
        }
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.owner.applicationContext();
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        return this.hierarchy(key, false);
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        MultiMap<Scope, BindingHierarchy<?>> map = new HashSetMultiMap<>();
        map.putAll(this.scope, this.hierarchies.values());
        return map;
    }

    private <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean useParentIfAbsent) {
        // If the scope is default, it means that the binding is not explicitly scoped, so it can be
        // installed in any scope. If our active scope is the active application context, it means
        // the requested scope is not installed, so we can fall back to the application scope.
        if (key.scope() != this.scope && key.scope() != Scope.DEFAULT_SCOPE && this.scope != this.applicationContext()) {
            throw new IllegalArgumentException("Cannot create a binding hierarchy for a component key with a different scope");
        }

        BindingHierarchy<?> hierarchy = this.hierarchies.computeIfAbsent(key.view(), componentKey -> {
            // If we don't have an explicit hierarchy on the key, we can try to use the hierarchy of
            // the application context. This is useful for components that are not explicitly scoped,
            // but are still accessed through a scope.
            if (useParentIfAbsent && this.owner.applicationProvider() != this) {
                return this.owner.applicationProvider().hierarchy(key);
            }
            if (ComponentCollection.class.isAssignableFrom(key.type())) {
                return new CollectionBindingHierarchy<>(
                        TypeUtils.adjustWildcards(key, ComponentKey.class),
                        this.applicationContext()
                );
            }
            else {
                return new NativeBindingHierarchy<>(key, this.applicationContext());
            }
        });
        BindingHierarchy<T> adjustedHierarchy = TypeUtils.adjustWildcards(hierarchy, BindingHierarchy.class);
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (adjustedHierarchy instanceof ContextWrappedHierarchy || adjustedHierarchy instanceof CollectionBindingHierarchy<?>) {
            return adjustedHierarchy;
        }
        else {
            return new ContextWrappedHierarchy<>(adjustedHierarchy, this.applicationContext(), updated -> this.hierarchies.put(key.view(), updated));
        }
    }
}
