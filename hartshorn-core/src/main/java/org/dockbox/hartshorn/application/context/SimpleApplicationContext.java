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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.PostProcessingComponentProvider;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.inject.ComponentContainerDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.DependencyResolutionException;
import org.dockbox.hartshorn.inject.PostProcessorDependencyDeclarationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.collections.ConcurrentSetTreeMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SimpleApplicationContext extends DelegatingApplicationContext implements ProcessableApplicationContext {

    protected transient MultiMap<Integer, ComponentPreProcessor> preProcessors;
    private final DependencyGraphInitializer dependencyGraphInitializer;

    public SimpleApplicationContext(SingleElementContext<? extends ApplicationEnvironment> initializerContext, Configurer configurer) {
        super(initializerContext, configurer);
        this.dependencyGraphInitializer = configurer.dependencyGraphInitializer.initialize(initializerContext.transform(this));
    }

    @Override
    protected void prepareInitialization() {
        this.preProcessors = new ConcurrentSetTreeMultiMap<>();
    }

    @Override
    public void add(ComponentProcessor processor) {
        this.checkRunning();

        Integer order = processor.priority();
        String name = processor.getClass().getSimpleName();

        if (processor instanceof ComponentPostProcessor postProcessor && this.componentProvider() instanceof PostProcessingComponentProvider provider) {
            // Singleton binding is decided by the component provider, to allow for further optimization
            provider.postProcessor(postProcessor);
            this.log().debug("Added %s for component post-processing with order %d".formatted(name, order));
        }
        else if (processor instanceof ComponentPreProcessor preProcessor) {
            this.preProcessors.put(preProcessor.priority(), preProcessor);
            this.bind((Class<ComponentPreProcessor>) preProcessor.getClass()).singleton(preProcessor);
            this.log().debug("Added %s for component pre-processing with order %d".formatted(name, order));
        }
        else {
            this.log().warn("Unsupported component processor type [" + name + "]");
        }
    }

    @Override
    public void add(Class<? extends ComponentPostProcessor> processor) {
        this.checkRunning();

        if (this.componentProvider() instanceof PostProcessingComponentProvider provider) {
            provider.postProcessor(processor);
        }
        else {
            this.log().warn("Lazy initialization of component processors is not supported by this component provider [for " + processor.getSimpleName() + "]");
        }
    }

    @Override
    public void loadContext() {
        this.checkRunning();

        Collection<ComponentContainer<?>> containers = this.locator().containers();
        this.log().debug("Located %d components".formatted(containers.size()));

        try {
            Collection<DependencyDeclarationContext<?>> declarationContexts = new ArrayList<>();

            if (this.componentProvider() instanceof PostProcessingComponentProvider postProcessingComponentProvider) {
                Set<? extends DependencyDeclarationContext<?>> uninitializedPostProcessorContexts = postProcessingComponentProvider.uninitializedPostProcessors().stream()
                        .map(this.environment().introspector()::introspect)
                        .map(PostProcessorDependencyDeclarationContext::new)
                        .collect(Collectors.toSet());
                declarationContexts.addAll(uninitializedPostProcessorContexts);
            }

            List<? extends DependencyDeclarationContext<?>> componentContexts = containers.stream()
                    .map(ComponentContainerDependencyDeclarationContext::new)
                    .toList();
            declarationContexts.addAll(componentContexts);

            this.dependencyGraphInitializer.initializeDependencyGraph(declarationContexts);
        }
        catch (DependencyResolutionException e) {
            throw new ComponentInitializationException("Failed to resolve dependencies", e);
        }
        catch (GraphException e) {
            throw new ComponentInitializationException("Failed to iterate dependency graph", e);
        }
        catch (ApplicationException e) {
            throw new ComponentInitializationException("Failed to initialize components", e);
        }
        this.initializePostProcessors();
        this.processComponents(containers);

        this.isRunning = true;
    }

    private void initializePostProcessors() {
        if (this.componentProvider() instanceof PostProcessingComponentProvider provider) {
            for (Class<? extends ComponentPostProcessor> uninitializedPostProcessor : provider.uninitializedPostProcessors()) {
                ComponentPostProcessor processor = this.componentProvider().get(uninitializedPostProcessor);
                provider.postProcessor(processor);
            }
        }
    }

    @Override
    public MultiMap<Integer, ComponentPreProcessor> processors() {
        return this.preProcessors;
    }

    protected void processComponents(Collection<ComponentContainer<?>> containers) {
        this.checkRunning();
        for (ComponentPreProcessor serviceProcessor : this.preProcessors.allValues()) {
            this.log().debug("Processing %s components with registered processor %s".formatted(containers.size(), serviceProcessor.getClass().getSimpleName()));
            for (ComponentContainer<?> container : containers) {
                this.processStandaloneComponent(container, serviceProcessor);
            }
            if (serviceProcessor instanceof ExitingComponentProcessor exiting) {
                exiting.exit(this);
            }
        }
    }

    private void processStandaloneComponent(ComponentContainer<?> container, ComponentPreProcessor serviceProcessor) {
        TypeView<?> service = container.type();
        ComponentKey<?> key = ComponentKey.of(service.type());
        ComponentProcessingContext<?> context = new ComponentProcessingContext<>(this, key, null);
        this.log().debug("Processing component %s with registered processor %s".formatted(container.id(), serviceProcessor.getClass().getSimpleName()));
        serviceProcessor.process(context);
    }

    public static ContextualInitializer<ApplicationEnvironment, ApplicationContext> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new SimpleApplicationContext(context, configurer);
        };
    }

    public static class Configurer extends DelegatingApplicationContext.Configurer {

        private ContextualInitializer<ApplicationContext, ? extends DependencyGraphInitializer> dependencyGraphInitializer = DependencyGraphInitializer.create(Customizer.useDefaults());

        public Configurer dependencyGraphInitializer(DependencyGraphInitializer dependencyGraphInitializer) {
            return this.dependencyGraphInitializer(ContextualInitializer.of(dependencyGraphInitializer));
        }

        public Configurer dependencyGraphInitializer(ContextualInitializer<ApplicationContext, DependencyGraphInitializer> dependencyGraphInitializer) {
            this.dependencyGraphInitializer = dependencyGraphInitializer;
            return this;
        }

    }
}
