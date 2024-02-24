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

package org.dockbox.hartshorn.component;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.CompositeComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ModifiableComponentProcessingContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.context.DefaultProvisionContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.binding.AbstractBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.binding.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.binding.ContextWrappedHierarchy;
import org.dockbox.hartshorn.inject.binding.HierarchyBindingFunction;
import org.dockbox.hartshorn.inject.binding.NativePrunableBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.inject.binding.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.ContainerAwareComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.ImmutableCompositeBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.SimpleComponentCollection;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.NavigableMultiMap;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ComponentProvider} which is aware of the {@link Scope} in which it is installed, and tracks bindings
 * based on available {@link BindingHierarchy binding hierarchies}. This allows for the creation of a hierarchy of
 * bindings, which can be used to resolve components at specific priorities.
 *
 * <p>As this provider is aware of the {@link Scope} in which it is installed, it is constrained to be part of a
 * {@link ScopedProviderOwner}. This owner is responsible for providing the {@link Scope} in which this provider is
 * installed.
 *
 * @see ScopedProviderOwner
 * @see HierarchicalComponentProvider
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HierarchyAwareComponentProvider extends DefaultProvisionContext implements HierarchicalComponentProvider, ContextCarrier {

    private final transient ScopedProviderOwner owner;
    private final transient Scope scope;

    private final transient SingletonCache singletonCache = new ConcurrentHashSingletonCache();
    private final transient Map<ComponentKeyView<?>, BindingHierarchy<?>> hierarchies = new ConcurrentHashMap<>();
    private final transient ComponentPostProcessor processor;

    public HierarchyAwareComponentProvider(ScopedProviderOwner owner, Scope scope) {
        this.owner = owner;
        this.scope = scope;
        this.processor = new CompositeComponentPostProcessor(owner::postProcessors);
    }

    private <T> Option<ObjectContainer<T>> create(ComponentKey<T> key, ComponentRequestContext requestContext) {
        try {
            Option<ObjectContainer<T>> objectContainer = this.provide(key, requestContext);
            if (objectContainer.present()) {
                return objectContainer;
            }

            return this.createContextualInstanceContainer(key, requestContext);
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

    public <T> Option<ObjectContainer<T>> provide(ComponentKey<T> key, ComponentRequestContext requestContext) throws ApplicationException {
        Option<BindingHierarchy<T>> hierarchy = Option.of(this.hierarchy(key, true));
        if (hierarchy.present()) {
            Provider<T> provider = key.strategy().selectProvider(hierarchy.get());
            if (provider != null) {
                return provider.provide(this.applicationContext(), requestContext);
            }
        }
        return Option.empty();
    }

    protected <T> T process(ComponentKey<T> key, ObjectContainer<T> objectContainer, @Nullable ComponentContainer<?> container) throws ApplicationException {

    }
    protected <T> T process(ComponentKey<T> key, ObjectContainer<T> objectContainer, @Nullable ComponentContainer<?> container, ComponentRequestContext requestContext) throws ApplicationException {
        if (container != null && !container.permitsProcessing()) {
            return objectContainer.instance();
        }

        ModifiableComponentProcessingContext<T> processingContext = this.prepareProcessingContext(key, objectContainer.instance(), container, requestContext);
        objectContainer.processed(true);

        processingContext = this.process(processingContext);
        return processingContext.instance();
    }

    protected <E, T extends ContainerAwareComponentCollection<E>> T processCollection(ComponentKey<T> key, T collection) throws ApplicationException {
        ComponentKey<E> build = TypeUtils.adjustWildcards(key.mutable()
                .type(key.parameterizedType().parameters().get(0))
                .build(), ComponentKey.class);

        for(ObjectContainer<E> container : collection.containers()) {
            this.process(build, container, null);
        }
        return collection;
    }

    protected <T> ModifiableComponentProcessingContext<T> prepareProcessingContext(ComponentKey<T> key, T instance, @Nullable ComponentContainer<?> container, ComponentRequestContext requestContext) {
        ModifiableComponentProcessingContext<T> processingContext = new ModifiableComponentProcessingContext<>(
                this.applicationContext(), key, requestContext, instance,
                container == null || container.permitsProxying(),
                latest -> this.storeSingletons(key, latest));

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

    protected <T> ModifiableComponentProcessingContext<T> process(ModifiableComponentProcessingContext<T> processingContext) throws ApplicationException {
        this.processor.process(processingContext);
        this.storeSingletons(processingContext.key(), processingContext.instance());
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

    public <T> Option<ObjectContainer<T>> createContextualInstanceContainer(ComponentKey<T> key, ComponentRequestContext requestContext) throws ApplicationException {
        return new ContextDrivenProvider<>(key).provide(this.applicationContext(), requestContext);
    }

    @Override
    public <T> T get(ComponentKey<T> componentKey, ComponentRequestContext requestContext) {
        if (componentKey.type() == ApplicationContext.class && componentKey.qualifier().isEmpty()) {
            return TypeUtils.adjustWildcards(this.applicationContext(), Object.class);
        }

        if (this.singletonCache.contains(componentKey)) {
            return this.singletonCache.get(componentKey)
                .orElseThrow(() -> new ComponentResolutionException("No instance found for key " + componentKey + ", but the key was present in the singleton cache"));
        }

        this.owner.componentLocator().validate(componentKey);

        ObjectContainer<T> objectContainer = this.create(componentKey, requestContext)
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

        try {
            return this.processInstance(componentKey, objectContainer, instance);
        }
        catch(ApplicationException e) {
            throw new ComponentResolutionException("Failed to process component with key " + componentKey, e);
        }
    }

    private <T> T processInstance(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, T instance)
            throws ApplicationException {
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
                                      Option<ComponentContainer<?>> container) throws ApplicationException {
        // Will only mark the object container as processed if the component container permits
        // processing.
        return this.process(componentKey, objectContainer, container.get());
    }

    private <T> T getUnmanagedComponent(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, Class<? extends T> type)
            throws ApplicationException {
        TypeView<? extends T> typeView = this.applicationContext().environment().introspector().introspect(type);
        if (typeView.annotations().has(Component.class)) {
            throw new ApplicationRuntimeException("Component " + typeView.name() + " is not registered");
        }
        if (ComponentCollection.class.isAssignableFrom(componentKey.type())) {
            if (ComponentCollection.class != componentKey.type()) {
                throw new IllegalArgumentException("Component collection key must be of type ComponentCollection, specific implementations are not supported");
            }
            ComponentCollection<Object> collection = this.processComponentCollection(
                    TypeUtils.adjustWildcards(componentKey, ComponentKey.class),
                    TypeUtils.adjustWildcards(objectContainer, ObjectContainer.class)
            );
            return componentKey.type().cast(collection);
        }
        return this.process(componentKey, objectContainer, null);
    }

    private <E, T extends ComponentCollection<E>> ComponentCollection<E> processComponentCollection(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer)
            throws ApplicationException {
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

    private <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        // If the scope is default, it means that the binding is not explicitly scoped, so it can be
        // installed in any scope. If our active scope is the active application context, it means
        // the requested scope is not installed, so we can fall back to the application scope.
        if (key.scope() != this.scope && key.scope() != Scope.DEFAULT_SCOPE && this.scope != this.applicationContext()) {
            throw new IllegalArgumentException("Cannot create a binding hierarchy for a component key with a different scope");
        }

        BindingHierarchy<?> hierarchy = this.getOrComputeHierarchy(key, permitFallbackResolution);
        BindingHierarchy<T> adjustedHierarchy = TypeUtils.adjustWildcards(hierarchy, BindingHierarchy.class);
        // onUpdate callback is purely so updates will still be saved even if the reference is lost
        if (adjustedHierarchy instanceof ContextWrappedHierarchy || adjustedHierarchy instanceof CollectionBindingHierarchy<?>) {
            return adjustedHierarchy;
        }
        else {
            return new ContextWrappedHierarchy<>(adjustedHierarchy, this.applicationContext(), updated -> this.hierarchies.put(key.view(), updated));
        }
    }

    private <T> BindingHierarchy<?> getOrComputeHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        ComponentKeyView<T> view = key.view();
        if (this.hierarchies.containsKey(view)) {
            return this.hierarchies.get(view);
        }
        else {
            return this.computeHierarchy(key, permitFallbackResolution);
        }
    }

    @NonNull
    private <T> BindingHierarchy<?> computeHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        BindingHierarchy<?> hierarchy = this.tryCreateHierarchy(key, permitFallbackResolution);

        return Objects.requireNonNullElseGet(hierarchy, () -> {
            // If we don't have an explicit hierarchy on the key, we can try to use the hierarchy of
            // the application context. This is useful for components that are not explicitly scoped,
            // but are still accessed through a scope.
            if(permitFallbackResolution && this.owner.applicationProvider() != this) {
                return this.owner.applicationProvider().hierarchy(key);
            }
            return new NativePrunableBindingHierarchy<>(key, this.applicationContext());
        });
    }

    @Nullable
    private <T> BindingHierarchy<?> tryCreateHierarchy(ComponentKey<T> key, boolean permitFallbackResolution) {
        BindingHierarchy<?> hierarchy;
        if(this.isStrict(key)) {
            hierarchy = this.createHierarchy(key);
            if (hierarchy != null) {
                this.hierarchies.put(key.view(), hierarchy);
            }
        }
        else if (permitFallbackResolution) {
            // Don't bind this hierarchy, as it's a loose match. If the configuration changes, the loose
            // match may not be valid anymore, so we don't want to cache it.
            hierarchy = this.looseLookupHierarchy(key);
        }
        else if (this.isCollectionComponentKey(key)) {
            hierarchy = new CollectionBindingHierarchy<>(TypeUtils.adjustWildcards(key, ComponentKey.class), this.applicationContext());
        }
        else {
            hierarchy = null;
        }
        return hierarchy;
    }

    protected boolean isStrict(ComponentKey<?> key) {
        if (key.strict() == Tristate.UNDEFINED) {
            return this.applicationContext().environment().isStrictMode();
        }
        else {
            return key.strict().booleanValue();
        }
    }

    @Nullable
    private <T> AbstractBindingHierarchy<?> createHierarchy(ComponentKey<T> key) {
        if (this.isCollectionComponentKey(key)) {
            return new CollectionBindingHierarchy<>(
                TypeUtils.adjustWildcards(key, ComponentKey.class),
                this.applicationContext()
            );
        }
        else {
            return null;
        }
    }

    @Nullable
    private <T> BindingHierarchy<?> looseLookupHierarchy(ComponentKey<T> key) {
        Set<ComponentKeyView<?>> hierarchyKeys = this.hierarchies.keySet();
        Set<ComponentKeyView<?>> compatibleKeys = hierarchyKeys.stream()
            .filter(hierarchyKey -> this.isCompatible(key, hierarchyKey))
            .collect(Collectors.toSet());

        if (this.isCollectionComponentKey(key)) {
            return this.composeCollectionHierarchy(TypeUtils.adjustWildcards(key, ComponentKey.class), compatibleKeys);
        }
        else {
            if (compatibleKeys.size() == 1) {
                ComponentKeyView<?> compatibleKey = CollectionUtilities.first(compatibleKeys);
                return this.hierarchies.get(compatibleKey);
            }
            else {
                // Acceptable, as long as there is a single highest priority binding. If multiple match, it's an error.
                return this.lookupHighestPriorityHierarchy(key, compatibleKeys);
            }
        }
    }

    private boolean isCollectionComponentKey(ComponentKey<?> key) {
        return ComponentCollection.class.isAssignableFrom(key.type());
    }

    @Nullable
    private BindingHierarchy<?> lookupHighestPriorityHierarchy(ComponentKey<?> key, Set<ComponentKeyView<?>> compatibleKeys) {
        Set<BindingHierarchy<?>> compatibleHierarchies = compatibleKeys.stream()
            .map(this.hierarchies::get)
            .collect(Collectors.toSet());

        // Track entire hierarchy, so potential duplicate top-priority hierarchies can be reported
        NavigableMultiMap<Integer, BindingHierarchy<?>> providers = new ConcurrentSetTreeMultiMap<>();
        for (BindingHierarchy<?> compatibleHierarchy : compatibleHierarchies) {
            int highestPriority = compatibleHierarchy.highestPriority();
            compatibleHierarchy.get(highestPriority).peek(provider -> {
                providers.put(highestPriority, compatibleHierarchy);
            });
        }
        Collection<BindingHierarchy<?>> highestPriority = providers.lastEntry();
        if (highestPriority.size() > 1) {
            Set<ComponentKey<?>> foundKeys = highestPriority.stream()
                .map(BindingHierarchy::key)
                .collect(Collectors.toSet());
            throw new AmbiguousComponentException(key, foundKeys);
        }
        return CollectionUtilities.first(highestPriority);
    }

    private boolean isCompatible(ComponentKey<?> key, ComponentKeyView<?> other) {
        ParameterizableType originType = key.parameterizedType();
        ParameterizableType targetType = other.type();
        return this.isCompatible(originType, targetType);
    }

    private boolean isCompatible(ParameterizableType originType, ParameterizableType targetType) {
        if (!originType.type().isAssignableFrom(targetType.type())) {
            return false;
        }
        List<ParameterizableType> originalParameters = originType.parameters();
        List<ParameterizableType> targetParameters = targetType.parameters();
        if (originalParameters.size() != targetParameters.size()) {
            return false;
        }
        for (int i = 0; i < originalParameters.size(); i++) {
            ParameterizableType originalParameter = originalParameters.get(i);
            ParameterizableType targetParameter = targetParameters.get(i);
            if (!this.isCompatible(originalParameter, targetParameter)) {
                return false;
            }
        }
        return true;
    }

    private <T> BindingHierarchy<?> composeCollectionHierarchy(ComponentKey<ComponentCollection<T>> key, Set<ComponentKeyView<?>> compatibleKeys) {
        Set<CollectionBindingHierarchy<?>> hierarchies = new HashSet<>();
        for (ComponentKeyView<?> compatibleKey : compatibleKeys) {
            BindingHierarchy<?> hierarchy = this.hierarchies.get(compatibleKey);
            if (hierarchy instanceof CollectionBindingHierarchy<?> collectionBindingHierarchy) {
                hierarchies.add(collectionBindingHierarchy);
            }
            else {
                throw new IllegalStateException("Found incompatible hierarchy for key " + compatibleKey +". Expected CollectionBindingHierarchy, but found " + hierarchy.getClass().getSimpleName());
            }
        }
        return new ImmutableCompositeBindingHierarchy<>(
            key, this.applicationContext(),
            TypeUtils.adjustWildcards(hierarchies, Collection.class)
        );
    }
}
