/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.collection.ContainerAwareComponentCollection;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.provider.ComponentRegistryAwareComponentProvider;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.targets.AnnotatedInjectionPointRequireRule;
import org.dockbox.hartshorn.inject.targets.RequireInjectionPointRule;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * An adapter for {@link ComponentProviderPostProcessor} that allows for the processing of
 * components provided by a {@link ComponentRegistryAwareComponentProvider}. This offers full
 * support for {@link ComponentCollection component collections}, and allows for the processing of
 * both managed and unmanaged components.
 *
 * <p>The actual processing of components is delegated to a {@link ComponentPostProcessor}. This
 * adapter ensures
 * the instance is sufficiently prepared for processing, and that the component store is updated
 * accordingly.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ComponentProviderPostProcessorAdapter implements ComponentProviderPostProcessor {

    private final RequireInjectionPointRule requireRule;
    private final ComponentRegistryAwareComponentProvider owner;
    private final ComponentPostProcessor processor;
    private final InjectionCapableApplication application;
    private final ComponentStoreCallback componentStoreCallback;

    public ComponentProviderPostProcessorAdapter(
        ComponentRegistryAwareComponentProvider owner,
        ComponentPostProcessor processor,
        InjectionCapableApplication application,
        ComponentStoreCallback componentStoreCallback
    ) {
        this.owner = owner;
        this.processor = processor;
        this.application = application;
        this.componentStoreCallback = componentStoreCallback;
        this.requireRule = new AnnotatedInjectionPointRequireRule(
                application.environment().configuration()
        );
    }

    @Override
    public <T> T processInstance(
        ComponentKey<T> componentKey,
        ObjectContainer<T> objectContainer,
        ComponentRequestContext requestContext
    ) throws ApplicationException {
        Class<? extends T> type = componentKey.type();
        T instance = objectContainer.instance();
        if (instance != null) {
            type = TypeUtils.unchecked(instance.getClass(), Class.class);
        }

        Option<ComponentContainer<?>> container = this.owner.componentRegistry().container(type);
        instance = container.present()
            ? this.processManagedComponent(componentKey, objectContainer, container, requestContext)
            : this.processUnmanagedComponent(componentKey, objectContainer, requestContext);

        if (instance == null && (!requestContext.isForInjectionPoint()
            || this.requireRule.isRequired(requestContext.injectionPoint()))) {
            componentKey.failureStrategy().onResolutionFailure(componentKey, requestContext);
        }

        return instance;
    }

    private <T> T processManagedComponent(
        ComponentKey<T> componentKey, ObjectContainer<T> objectContainer,
        Option<ComponentContainer<?>> container, ComponentRequestContext requestContext
    ) throws ApplicationException {
        // Will only mark the object container as processed if the component container permits
        // processing.
        return this.process(componentKey, objectContainer, container.get(), requestContext);
    }

    private <T> T processUnmanagedComponent(
        ComponentKey<T> componentKey,
        ObjectContainer<T> objectContainer,
        ComponentRequestContext requestContext
    )
        throws ApplicationException {
        if (ComponentCollection.class.isAssignableFrom(componentKey.type())) {
            if (ComponentCollection.class != componentKey.type()) {
                throw new IllegalArgumentException(
                    "Component collection key must be of type ComponentCollection, "
                        + "specific implementations are not supported"
                );
            }
            ComponentCollection<Object> collection = this.processComponentCollection(
                TypeUtils.unchecked(componentKey, ComponentKey.class),
                TypeUtils.unchecked(objectContainer, ObjectContainer.class),
                requestContext
            );
            return componentKey.type().cast(collection);
        }
        return this.process(componentKey, objectContainer, null, requestContext);
    }

    private <E, T extends ComponentCollection<E>> ComponentCollection<E> processComponentCollection(
        ComponentKey<T> componentKey,
        ObjectContainer<T> objectContainer,
        ComponentRequestContext requestContext
    )
        throws ApplicationException {
        if (objectContainer.instance() == null) {
            return new ContainerAwareComponentCollection<>(Set.of());
        }
        else if (objectContainer.instance() instanceof ContainerAwareComponentCollection<?>
            containerAwareComponentCollection) {

            ContainerAwareComponentCollection<E> collection = TypeUtils.unchecked(
                containerAwareComponentCollection,
                ContainerAwareComponentCollection.class);

            ComponentKey<ContainerAwareComponentCollection<E>> key = TypeUtils.unchecked(
                componentKey,
                ComponentKey.class);

            return this.processCollection(key, collection, requestContext);
        }
        else {
            throw new IllegalArgumentException(
                "Component collection from provider must be of type"
                    + " ContainerAwareComponentCollection"
            );
        }
    }

    /**
     * Processes the given {@link LockableComponentProcessingContext} using the configured
     * {@link ComponentPostProcessor}.
     *
     * @param processingContext the processing context to process
     * @param <T> the type of the component being processed
     *
     * @return the processed context
     *
     * @throws ApplicationException if an error occurs during processing
     */
    protected <T> LockableComponentProcessingContext<T> process(
        LockableComponentProcessingContext<T> processingContext
    ) throws ApplicationException {
        // Store early, so cyclic dependencies may be resolved
        this.componentStoreCallback.store(processingContext.key(), processingContext.container());
        this.processor.process(processingContext);
        return processingContext;
    }

    /**
     * Processes a component instance within the given {@link ObjectContainer}. If a
     * {@link ComponentContainer} is provided, it is used to determine whether processing is
     * permitted. If processing is not permitted, the instance is returned as-is.
     *
     * @param key the component key of the component being processed
     * @param objectContainer the object container holding the instance to be processed
     * @param container the component container describing the component, if any
     * @param requestContext the request context for the processing operation
     * @param <T> the type of the component being processed
     *
     * @return the processed instance
     *
     * @throws ApplicationException if an error occurs during processing
     */
    protected <T> T process(
        ComponentKey<T> key,
        ObjectContainer<T> objectContainer,
        @Nullable ComponentContainer<?> container,
        ComponentRequestContext requestContext
    ) throws ApplicationException {
        if (container != null && !container.permitsProcessing()) {
            return objectContainer.instance();
        }

        LockableComponentProcessingContext<T> processingContext =
            this.prepareProcessingContext(key, objectContainer, container, requestContext);
        objectContainer.processed(true);

        processingContext = this.process(processingContext);
        return processingContext.instance();
    }

    /**
     * Processes a {@link ContainerAwareComponentCollection} by processing each individual component
     * within the collection.
     *
     * @param key the component key of the collection
     * @param collection the collection to process
     * @param requestContext the request context for the processing operation
     * @param <E> the element type of the collection
     * @param <T> the type of the collection
     *
     * @return the processed collection
     *
     * @throws ApplicationException if an error occurs during processing
     */
    protected <E, T extends ContainerAwareComponentCollection<E>> T processCollection(
        ComponentKey<T> key,
        T collection,
        ComponentRequestContext requestContext
    ) throws ApplicationException {
        ComponentKey<E> build = TypeUtils.unchecked(key.mutable()
            .type(key.parameterizedType().parameters().getFirst())
            .build(), ComponentKey.class);

        for (ObjectContainer<E> container : collection.containers()) {
            this.process(build, container, null, requestContext);
        }
        return collection;
    }

    /**
     * Prepares a {@link LockableComponentProcessingContext} for processing. This includes setting
     * up tooling which may be required during processing, such as a {@link ProxyFactory} if
     * proxying is permitted.
     *
     * @param key the component key of the component being processed
     * @param objectContainer the object container holding the instance to be processed
     * @param componentContainer the component container describing the component, if any
     * @param requestContext the request context for the processing operation
     * @param <T> the type of the component being processed
     *
     * @return the prepared processing context
     */
    protected <T> LockableComponentProcessingContext<T> prepareProcessingContext(
        ComponentKey<T> key,
        ObjectContainer<T> objectContainer,
        @Nullable ComponentContainer<?> componentContainer,
        ComponentRequestContext requestContext
    ) {
        LockableComponentProcessingContext<T> processingContext =
            new LockableComponentProcessingContext<>(
                this.application, key, requestContext, objectContainer,
                componentContainer == null || componentContainer.permitsProxying(),
                this.componentStoreCallback);

        if (componentContainer != null) {
            processingContext.put(ComponentContainer.class, componentContainer);
            if (componentContainer.permitsProxying()) {
                boolean hasObjectInstance = objectContainer.instance() != null;
                // Always attempt to use the most detailed proxy factory available, as this allows
                // for more advanced proxying capabilities.
                Class<?> proxyBaseType = hasObjectInstance
                    ? objectContainer.instance().getClass()
                    : key.type();
                StateAwareProxyFactory<?> factory =
                    this.application.environment().proxyOrchestrator().factory(proxyBaseType);

                if (objectContainer.instance() != null) {
                    factory.trackState(false);
                    factory.advisors()
                        .type()
                        .delegateAbstractOnly(TypeUtils.unchecked(objectContainer.instance(),
                            Object.class));
                    factory.trackState(true);
                }
                processingContext.put(ProxyFactory.class, factory);
            }
        }
        return processingContext;
    }
}
