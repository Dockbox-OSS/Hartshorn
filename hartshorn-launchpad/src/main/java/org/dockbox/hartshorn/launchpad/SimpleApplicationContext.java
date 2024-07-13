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

package org.dockbox.hartshorn.launchpad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.graph.SkipConfigurationDependencyVisitor;
import org.dockbox.hartshorn.inject.graph.resolve.ApplicationDependencyResolver;
import org.dockbox.hartshorn.inject.provider.PostProcessingComponentProvider;
import org.dockbox.hartshorn.inject.graph.ComponentContainerDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.launchpad.graph.DelegatingConfigurationDependencyVisitor;
import org.dockbox.hartshorn.launchpad.graph.PostProcessorDependencyDeclarationContext;
import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.inject.graph.DependencyGraphInitializer;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionException;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyDeclarationContext;
import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.inject.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.inject.processing.ExitingComponentProcessor;
import org.dockbox.hartshorn.inject.processing.ComponentProcessorRegistry;
import org.dockbox.hartshorn.inject.provider.ComponentObjectContainer;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of the {@link ApplicationContext} interface. This implementation primarily delegates to individual
 * components, such as the {@link DependencyGraphInitializer} and {@link PostProcessingComponentProvider}. It also supports
 * pre-processing of components, which is performed immediately when {@link #loadContext() the context is loaded}.
 *
 * <p>This context is limited to only being initialized once, and is not refreshable (unless its individual components support
 * this).
 *
 * @see DependencyGraphInitializer
 * @see PostProcessingComponentProvider
 * @see ComponentPreProcessor
 * @see DelegatingApplicationContext
 * @see ProcessableApplicationContext
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public class SimpleApplicationContext extends DelegatingApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleApplicationContext.class);
    private final DependencyGraphInitializer dependencyGraphInitializer;

    public SimpleApplicationContext(SingleElementContext<? extends ApplicationEnvironment> initializerContext, Configurer configurer) {
        super(initializerContext, configurer);
        this.dependencyGraphInitializer = configurer.dependencyGraphInitializer.initialize(initializerContext.transform(this));
    }

    @Override
    protected void prepareInitialization() {
        // Nothing to do, override if needed
    }

    @Override
    public synchronized void loadContext() {
        this.checkRunning();

        Collection<ComponentContainer<?>> containers = this.componentRegistry().containers();
        LOG.debug("Located %d components".formatted(containers.size()));

        try {
            Collection<DependencyDeclarationContext<?>> declarationContexts = new ArrayList<>();

            if (this.componentProvider() instanceof PostProcessingComponentProvider postProcessingComponentProvider) {
                Set<? extends DependencyDeclarationContext<?>> uninitializedPostProcessorContexts = postProcessingComponentProvider
                        .processorRegistry()
                        .uninitializedPostProcessors().stream()
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
            ComponentProcessorRegistry registry = provider.processorRegistry();
            for (Class<? extends ComponentPostProcessor> uninitializedPostProcessor : registry.uninitializedPostProcessors()) {
                ComponentPostProcessor processor = this.componentProvider().get(uninitializedPostProcessor);
                registry.register(processor);
            }
        }
    }

    /**
     * Pre-processes all components with the registered {@link ComponentPreProcessor component pre-processors}. This method
     * is called immediately after the dependency graph has been initialized.
     *
     * @param containers the components to process
     */
    protected void processComponents(Collection<ComponentContainer<?>> containers) {
        this.checkRunning();
        MultiMap<Integer, ComponentPreProcessor> processors = this.componentProvider().processorRegistry().preProcessors();
        for(int priority : processors.keySet()) {
            for(ComponentPreProcessor processor : processors.get(priority)) {
                LOG.debug("Processing %s components with registered processor %s".formatted(containers.size(), processor.getClass().getSimpleName()));
                for (ComponentContainer<?> container : containers) {
                    this.processStandaloneComponent(container, processor);
                }
                if (processor instanceof ExitingComponentProcessor exiting) {
                    exiting.exit(this);
                }
            }
        }
    }

    private void processStandaloneComponent(ComponentContainer<?> container, ComponentPreProcessor serviceProcessor) {
        TypeView<?> service = container.type();
        ComponentKey<?> key = ComponentKey.of(service.type());
        LOG.debug("Processing component %s with registered processor %s".formatted(container.id(), serviceProcessor.getClass().getSimpleName()));
        ComponentProcessingContext<?> context = new ComponentProcessingContext<>(
                this, ComponentRequestContext.createForComponent(),
                key, ComponentObjectContainer.empty(), container.permitsProxying()
        );
        serviceProcessor.process(context);
    }

    /**
     * Creates a new {@link SimpleApplicationContext} which may be customized using the given {@link Customizer}.
     *
     * @param customizer the customizer
     * @return the new {@link SimpleApplicationContext}
     */
    public static ContextualInitializer<ApplicationEnvironment, ApplicationContext> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new SimpleApplicationContext(context, configurer);
        };
    }

    /**
     * Configuration class for the {@link SimpleApplicationContext}. This class is used to configure the {@link SimpleApplicationContext}
     * before it is created.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer extends DelegatingApplicationContext.Configurer {

        private ContextualInitializer<ApplicationContext, ? extends DependencyGraphInitializer> dependencyGraphInitializer = ContextualInitializer.defer(() -> {
            return DependencyGraphInitializer.create(graph -> {
                // Support @Binds methods
                graph.dependencyResolver(ApplicationDependencyResolver.create(Customizer.useDefaults()));

                // If context is processable, ensure processors are registered as components
                graph.dependencyVisitor(ContextualInitializer.of(context -> {
                    if (context instanceof ProcessableApplicationContext processableApplicationContext) {
                        return new DelegatingConfigurationDependencyVisitor(
                                context.defaultBinder(),
                                context.defaultProvider(),
                                processableApplicationContext.defaultProvider().processorRegistry()
                        );
                    }
                    return new SkipConfigurationDependencyVisitor(context.defaultBinder(), context.defaultProvider());
                }));
            });
        });

        /**
         * Configures the dependency graph initializer to use the given {@link DependencyGraphInitializer}.
         *
         * @param dependencyGraphInitializer the dependency graph initializer
         * @return the current instance
         */
        public Configurer dependencyGraphInitializer(DependencyGraphInitializer dependencyGraphInitializer) {
            return this.dependencyGraphInitializer(ContextualInitializer.of(dependencyGraphInitializer));
        }

        /**
         * Configures the dependency graph initializer to use the given {@link ContextualInitializer} to create a {@link DependencyGraphInitializer}.
         *
         * @param dependencyGraphInitializer the initializer of the dependency graph initializer
         * @return the current instance
         */
        public Configurer dependencyGraphInitializer(ContextualInitializer<ApplicationContext, DependencyGraphInitializer> dependencyGraphInitializer) {
            this.dependencyGraphInitializer = dependencyGraphInitializer;
            return this;
        }
    }
}
