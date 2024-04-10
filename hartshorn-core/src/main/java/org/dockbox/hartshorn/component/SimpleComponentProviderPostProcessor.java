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

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ModifiableComponentProcessingContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.ContainerAwareComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.SimpleComponentCollection;
import org.dockbox.hartshorn.proxy.ProxyFactory;
import org.dockbox.hartshorn.proxy.lookup.StateAwareProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class SimpleComponentProviderPostProcessor implements ComponentProviderPostProcessor {

    private final ScopedProviderOwner owner;
    private final ComponentPostProcessor processor;
    private final ApplicationContext applicationContext;
    private final ComponentStoreCallback storeSingleton;

    public SimpleComponentProviderPostProcessor(
            ScopedProviderOwner owner,
            ComponentPostProcessor processor,
            ApplicationContext applicationContext,
            ComponentStoreCallback storeSingleton
    ) {
        this.owner = owner;
        this.processor = processor;
        this.applicationContext = applicationContext;
        this.storeSingleton = storeSingleton;
    }

    @Override
    public <T> T processInstance(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, T instance,
            ComponentRequestContext requestContext) throws ApplicationException {
        Class<? extends T> type = componentKey.type();
        if (instance != null) {
            type = TypeUtils.adjustWildcards(instance.getClass(), Class.class);
        }

        Option<ComponentContainer<?>> container = this.owner.componentRegistry().container(type);
        instance = container.present()
                ? this.processManagedComponent(componentKey, objectContainer, container, requestContext)
                : this.processUnmanagedComponent(componentKey, objectContainer, type,requestContext);

        if (instance == null) {
            throw new ComponentResolutionException("No component found for key " + componentKey);
        }

        return this.finishComponentRequest(componentKey, instance);
    }


    private <T> T finishComponentRequest(ComponentKey<T> componentKey, T instance) {
        this.storeSingleton.store(componentKey, instance);

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

    private <T> T processManagedComponent(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer,
            Option<ComponentContainer<?>> container, ComponentRequestContext requestContext) throws ApplicationException {
        // Will only mark the object container as processed if the component container permits
        // processing.
        return this.process(componentKey, objectContainer, container.get(), requestContext);
    }

    private <T> T processUnmanagedComponent(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, Class<? extends T> type, ComponentRequestContext requestContext)
            throws ApplicationException {
        TypeView<? extends T> typeView = this.applicationContext.environment().introspector().introspect(type);
        if (typeView.annotations().has(Component.class)) {
            throw new ApplicationRuntimeException("Component " + typeView.name() + " is not registered");
        }
        if (ComponentCollection.class.isAssignableFrom(componentKey.type())) {
            if (ComponentCollection.class != componentKey.type()) {
                throw new IllegalArgumentException("Component collection key must be of type ComponentCollection, specific implementations are not supported");
            }
            ComponentCollection<Object> collection = this.processComponentCollection(
                    TypeUtils.adjustWildcards(componentKey, ComponentKey.class),
                    TypeUtils.adjustWildcards(objectContainer, ObjectContainer.class),
                    requestContext
            );
            return componentKey.type().cast(collection);
        }
        return this.process(componentKey, objectContainer, null, requestContext);
    }

    private <E, T extends ComponentCollection<E>> ComponentCollection<E> processComponentCollection(ComponentKey<T> componentKey, ObjectContainer<T> objectContainer, ComponentRequestContext requestContext)
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

            ContainerAwareComponentCollection<E> processed = this.processCollection(key, collection, requestContext);
            return new SimpleComponentCollection<>(Set.copyOf(processed));
        }
        else {
            throw new IllegalArgumentException("Component collection from provider must be of type ContainerAwareComponentCollection");
        }
    }

    protected <T> ModifiableComponentProcessingContext<T> process(ModifiableComponentProcessingContext<T> processingContext) throws ApplicationException {
        this.processor.process(processingContext);
        this.storeSingleton.store(processingContext.key(), processingContext.instance());
        return processingContext;
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

    protected <E, T extends ContainerAwareComponentCollection<E>> T processCollection(ComponentKey<T> key, T collection, ComponentRequestContext requestContext) throws ApplicationException {
        ComponentKey<E> build = TypeUtils.adjustWildcards(key.mutable()
                .type(key.parameterizedType().parameters().getFirst())
                .build(), ComponentKey.class);

        for(ObjectContainer<E> container : collection.containers()) {
            this.process(build, container, null, requestContext);
        }
        return collection;
    }

    protected <T> ModifiableComponentProcessingContext<T> prepareProcessingContext(ComponentKey<T> key, T instance, @Nullable ComponentContainer<?> container, ComponentRequestContext requestContext) {
        ModifiableComponentProcessingContext<T> processingContext = new ModifiableComponentProcessingContext<>(
                this.applicationContext, key, requestContext, instance,
                container == null || container.permitsProxying(),
                latest -> this.storeSingleton.store(key, latest));

        if (container != null) {
            processingContext.put(ComponentKey.of(ComponentContainer.class), container);
            if (container.permitsProxying()) {
                StateAwareProxyFactory<T> factory = this.applicationContext.environment().proxyOrchestrator().factory(key.type());

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
}
