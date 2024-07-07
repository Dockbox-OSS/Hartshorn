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

package org.dockbox.hartshorn.application;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.ProcessableApplicationContext;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessor;
import org.dockbox.hartshorn.inject.activation.ServiceActivator;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.util.introspect.Introspector;

/**
 * Registers component processors to the application context. This class should be used to prepare the application
 * context before {@link ProcessableApplicationContext#loadContext()} is called, to ensure that all components are
 * processed correctly.
 *
 * @see ComponentProcessor
 * @see ServiceActivator
 * @see ApplicationContext
 * @see ProcessableApplicationContext
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class ComponentProcessorRegistrar {

    private final Set<ComponentProcessor> additionalProcessors = new HashSet<>();

    private final ServiceActivatorCollector activatorCollector;
    private final ApplicationBuildContext buildContext;

    public ComponentProcessorRegistrar(ServiceActivatorCollector activatorCollector, ApplicationBuildContext buildContext) {
        this.activatorCollector = activatorCollector;
        this.buildContext = buildContext;
    }

    /**
     * Adds additional component processors to the registrar. These processors will be registered to the application
     * context when {@link #registerComponentProcessors(ApplicationContext, Set)} is called.
     *
     * @param processors the additional processors to add
     */
    public void withAdditionalProcessors(Collection<? extends ComponentProcessor> processors) {
        this.additionalProcessors.addAll(processors);
    }

    /**
     * Registers component processors to the application context. This method will collect all activators from the given
     * set of annotations, and then collect all processors from the activators. The processors will then be registered
     * to the application context.
     *
     * @param applicationContext the application context
     * @param activators the set of annotations to collect activators from
     */
    public void registerComponentProcessors(ApplicationContext applicationContext, Set<Annotation> activators) {
        Set<ServiceActivator> serviceActivatorAnnotations = activators.stream()
            .flatMap(activator -> this.activatorCollector.collectDeclarationsOnActivator(activator).stream())
            .collect(Collectors.toSet());

        Set<Class<? extends ComponentProcessor>> processorTypes = serviceActivatorAnnotations.stream()
            .flatMap(serviceActivator -> Arrays.stream(serviceActivator.processors()))
            .collect(Collectors.toSet());

        this.buildContext.logger().debug("Registering {} component processors to application context", processorTypes.size());

        this.registerPostProcessors(applicationContext, processorTypes);
        this.registerPreProcessors(applicationContext, processorTypes);
    }

    /**
     * Registers pre-processors to the application context. Pre-processors are instantiated before they are used, as they
     * are required to process components before they are registered.
     *
     * @param applicationContext the application context
     * @param processorTypes the types of pre-processors to register
     *
     * @see ApplicationContext#add(ComponentProcessor)
     */
    protected void registerPreProcessors(ApplicationContext applicationContext, Set<Class<? extends ComponentProcessor>> processorTypes) {
        Set<Class<? extends ComponentPreProcessor>> preProcessorTypes = this.extractProcessors(processorTypes, ComponentPreProcessor.class);
        Set<ComponentPreProcessor> componentProcessors = this.createPreProcessors(applicationContext, preProcessorTypes);
        for (ComponentProcessor componentProcessor : componentProcessors) {
            applicationContext.add(componentProcessor);
        }
    }

    /**
     * Registers post-processors to the application context. Post-processors are registered as-is, as they are not
     * required to be instantiated before they are used. If additional processors are registered, they will be added to
     * the set of post-processors.
     *
     * @param applicationContext the application context
     * @param processorTypes the types of post-processors to register
     *
     * @see ApplicationContext#add(Class)
     * @see ApplicationContext#add(ComponentProcessor)
     */
    protected void registerPostProcessors(ApplicationContext applicationContext, Set<Class<? extends ComponentProcessor>> processorTypes) {
        Set<Class<? extends ComponentPostProcessor>> postProcessorTypes = this.extractProcessors(processorTypes, ComponentPostProcessor.class);
        for (Class<? extends ComponentPostProcessor> postProcessorType : postProcessorTypes) {
            applicationContext.add(postProcessorType);
        }

        this.additionalProcessors.stream()
            .filter(ComponentPostProcessor.class::isInstance)
            .map(ComponentPostProcessor.class::cast)
            .forEach(applicationContext::add);
    }

    protected <T extends ComponentProcessor> Set<Class<? extends T>> extractProcessors(Set<Class<? extends ComponentProcessor>> processorTypes, Class<T> processorClass) {
        //noinspection unchecked
        return processorTypes.stream()
            .filter(processorClass::isAssignableFrom)
            .map(type -> (Class<? extends T>) type)
            .collect(Collectors.toSet());
    }

    /**
     * Instantiates required pre-processors so they can be registered for immediate use. If additional processors are
     * registered, they will be added to the set of pre-processors.
     *
     * @param applicationContext the application context
     * @param processorTypes the types of pre-processors to instantiate
     * @return a set of pre-processors
     */
    @NonNull
    protected Set<ComponentPreProcessor> createPreProcessors(ApplicationContext applicationContext, Set<Class<? extends ComponentPreProcessor>> processorTypes) {
        Set<ComponentPreProcessor> componentProcessors = this.additionalProcessors
            .stream()
            .filter(ComponentPreProcessor.class::isInstance)
            .map(ComponentPreProcessor.class::cast)
            .collect(Collectors.toSet());

        Introspector introspector = applicationContext.environment().introspector();
        // Note: pre-processors should never have dependencies, as they are used to process components before they are registered
        // and therefore cannot rely on other components being available
        for (Class<? extends ComponentPreProcessor> processorType : processorTypes) {
            var constructor = introspector.introspect(processorType).constructors().defaultConstructor();
            if (constructor.absent()) {
                this.buildContext.logger().error("Component pre-processor {} does not have a default constructor, skipping", processorType);
                continue;
            }
            try {
                componentProcessors.add(constructor.get().create());
            }
            catch(Throwable e) {
                throw new ComponentInitializationException("Failed to create component pre-processor", e);
            }
        }
        return componentProcessors;
    }
}
