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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;

/**
 * The {@link ApplicationBuilder} is responsible for creating the {@link ApplicationContext} and constructing the
 * required components. By default, all components are required, and will be validated before a {@link ApplicationContext}
 * is created.
 *
 * @param <Self> The type of the {@link ApplicationBuilder}, used to implement the fluent API.
 * @param <C> The type of the {@link ApplicationContext} that is created.
 * @author Guus Lieben
 * @since 0.4.8
 */
public interface ApplicationBuilder<Self extends ApplicationBuilder<Self, C>, C extends ApplicationContext> extends Reportable {

    /**
     * Sets the main class to use. Depending on the type of application environment, this may require the
     * class to have relevant {@link ServiceActivator service activators}. Alternative metadata sources may be
     * used, depending on the application environment.
     *
     * @param mainClass The mainClass to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self mainClass(Class<?> mainClass);

    Class<?> mainClass();

    /**
     * Adds a commandline argument to the application. These are later parsed by the active {@link ApplicationContext}.
     *
     * @param argument The argument to add.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self argument(String argument);

    /**
     * Adds commandline arguments to the application. These are later parsed by the active {@link ApplicationContext}.
     *
     * @param args The arguments to add.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self arguments(String... args);

    Set<String> arguments();

    /**
     * Whether to include the base package of the main class explicitly, or to only use the prefixes provided to
     * {@link #scanPackages(String...)} (and overloaded methods).
     *
     * @param include Whether to include the base package of the main class explicitly.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self includeBasePackages(boolean include);

    boolean includeBasePackages();

    /**
     * Whether to print the application banner to the console on startup.
     *
     * @param enable Whether to print the application banner to the console on startup.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self enableBanner(boolean enable);

    boolean enableBanner();

    Self enableBatchMode(boolean enable);

    boolean enableBatchMode();

    /**
     * Adds a service activator to the application. Service activators should always be valid
     * {@link ServiceActivator}s.
     *
     * @param annotation The service activator to add.
     * @return The {@link ApplicationBuilder} instance.
     * @see ActivatorHolder
     */
    Self serviceActivator(Annotation annotation);

    /**
     * Adds service activators to the application. Service activators should always be valid
     * {@link ServiceActivator}s.
     *
     * @param annotations The service activators to add.
     * @return The {@link ApplicationBuilder} instance.
     * @see ActivatorHolder
     */
    Self serviceActivators(Set<Annotation> annotations);

    Set<Annotation> serviceActivators();

    /**
     * Sets the {@link ApplicationConfigurator} to use. This is used to configure the application environment after
     * the {@link ApplicationContext} is created.
     *
     * @param applicationConfigurator The application configurator to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ApplicationConfigurator
     */
    Self applicationConfigurator(Initializer<ApplicationConfigurator> applicationConfigurator);

    ApplicationConfigurator applicationConfigurator(final InitializingContext context);

    /**
     * Sets the {@link ApplicationProxier} to use. This is responsible for component and service proxying during the entire
     * application lifetime.
     *
     * @param applicationProxier The application proxier to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ApplicationProxier
     */
    Self applicationProxier(Initializer<ApplicationProxier> applicationProxier);

    ApplicationProxier applicationProxier(final InitializingContext context);

    /**
     * Sets the {@link ApplicationLogger} to use. This is responsible for all logging purposes and should expose a valid
     * {@link org.slf4j.Logger} instance.
     *
     * @param applicationLogger The application logger to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self applicationLogger(Initializer<ApplicationLogger> applicationLogger);

    ApplicationLogger applicationLogger(final InitializingContext context);

    /**
     * Sets the {@link ApplicationFSProvider} to use. This is responsible for all filesystem operations and should expose
     * a valid {@link Path} representing the application base path or working directory.
     *
     * @param applicationFSProvider The application filesystem provider to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self applicationFSProvider(Initializer<ApplicationFSProvider> applicationFSProvider);

    ApplicationFSProvider applicationFSProvider(final InitializingContext context);

    /**
     * Sets the {@link ApplicationEnvironment} to use. The environment is responsible for keeping track of known prefixes
     * and type hierarchies. The provider used a {@link Function} as the environment should typically be bound to a given
     * {@link ApplicationContext}. The {@link ApplicationEnvironment} is created by the {@link ApplicationContext}.
     *
     * @param applicationEnvironment The application environment to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ApplicationEnvironment
     */
    Self applicationEnvironment(Initializer<ApplicationEnvironment> applicationEnvironment);

    ApplicationEnvironment applicationEnvironment(final InitializingContext context);

    /**
     * Sets the {@link ComponentLocator} to use. The locator is responsible for locating components and services, as well
     * as keeping track of registered components and services. The provider used a {@link Function} as the locator should
     * typically be bound to a given {@link ApplicationContext}. The {@link ComponentLocator} is created by the
     * {@link ApplicationContext}.
     *
     * @param componentLocator The component locator to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ComponentLocator
     */
    Self componentLocator(Initializer<ComponentLocator> componentLocator);

    ComponentLocator componentLocator(final InitializingContext context);

    /**
     * Sets the {@link ComponentPostProcessor} to use. The post constructor is responsible for invoking actions on a
     * component after it has been constructed. The provider used a {@link Function} as the post constructor should
     * typically be bound to a given {@link ApplicationContext}. The {@link ComponentPostProcessor} is created by the
     * {@link ComponentProvider}.
     *
     * @param componentPostConstructor The component post constructor to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self componentPostConstructor(Initializer<ComponentPostConstructor> componentPostConstructor);

    ComponentPostConstructor componentPostConstructor(final InitializingContext context);

    /**
     * Sets the {@link AnnotationLookup} to use. The annotation lookup is responsible for finding annotations on a given
     * class, and tracking virtual hierarchies of annotations.
     *
     * @param annotationLookup The annotation lookup to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see AnnotationLookup
     */
    Self annotationLookup(Initializer<AnnotationLookup> annotationLookup);

    AnnotationLookup annotationLookup(final InitializingContext context);

    /**
     * Sets the {@link ClasspathResourceLocator} to use. The classpath resource locator is responsible for locating resources on
     * the classpath, and make them available to the application.
     *
     * @param resourceLocator The classpath resource locator to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ClasspathResourceLocator
     */
    Self resourceLocator(Initializer<ClasspathResourceLocator> resourceLocator);

    ClasspathResourceLocator resourceLocator(final InitializingContext context);

    /**
     * Sets the {@link ExceptionHandler} to use. The exception handler is responsible for handling exceptions during the entire
     * application lifecycle.
     *
     * @param exceptionHandler The exception handler to use.
     * @return The {@link ApplicationBuilder} instance.
     * @see ExceptionHandler
     */
    Self exceptionHandler(Initializer<ExceptionHandler> exceptionHandler);

    ExceptionHandler exceptionHandler(final InitializingContext context);

    /**
     * Sets the {@link ApplicationArgumentParser} to use. The argument parser is responsible for parsing command line
     * arguments and making them available to the application.
     *
     * @param argumentParser The argument parser to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self argumentParser(Initializer<ApplicationArgumentParser> argumentParser);

    ApplicationArgumentParser argumentParser(final InitializingContext context);

    /**
     * Sets the {@link ComponentProvider} to use. The component provider is responsible for providing components and services
     * to the application. This acts as the primary component provider.
     *
     * @param componentProvider The component provider to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self componentProvider(Initializer<ComponentProvider> componentProvider);

    ComponentProvider componentProvider(final InitializingContext context);

    /**
     * Sets the {@link ComponentPopulator} to use. The component populator is responsible for populating components and services
     * created by the component provider.
     *
     * @param componentPopulator The component populator to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self componentPopulator(Initializer<ComponentPopulator> componentPopulator);

    ComponentPopulator componentPopulator(final InitializingContext context);

    /**
     * Sets the {@link ViewContextAdapter} to use. The view context adapter is responsible for adapting various
     * {@link org.dockbox.hartshorn.util.introspect.view.View} implementations to the application context.
     *
     * @param viewContextAdapter The view context adapter to use.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self viewContextAdapter(Initializer<ViewContextAdapter> viewContextAdapter);

    ViewContextAdapter viewContextAdapter(final InitializingContext context);

    /**
     * Registers a custom {@link ComponentPostProcessor}. Unlike automatically activated {@link ComponentPostProcessor}s,
     * custom {@link ComponentPostProcessor}s are always added even if their activator is absent.
     *
     * @param postProcessor The post processor to register
     * @return The {@link ApplicationBuilder} instance.
     * @see ComponentPostProcessor
     */
    Self postProcessor(ComponentPostProcessor postProcessor);

    Set<ComponentPostProcessor> componentPostProcessors();

    /**
     * Registers a custom {@link ComponentPreProcessor}. Unlike automatically activated {@link ComponentPreProcessor}s,
     * custom {@link ComponentPreProcessor}s are always added even if their activator is absent.
     *
     * @param preProcessor The pre processor to register
     * @return The {@link ApplicationBuilder} instance.
     * @see ComponentPreProcessor
     */
    Self preProcessor(ComponentPreProcessor preProcessor);

    Set<ComponentPreProcessor> componentPreProcessors();

    Self standaloneComponent(Class<?> component);

    Set<Class<?>> standaloneComponents();

    /**
     * Registers a custom package prefix which should be known to the application. These prefixes are bound using the
     * configured {@link ApplicationConfigurator} during application creation.
     *
     * @param packageName The prefix to register.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self scanPackage(String packageName);

    /**
     * Registers custom prefixes which should be known to the application. These prefixes are bound using the configured
     * {@link ApplicationConfigurator} during application creation.
     *
     * @param packages The prefixes to register.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self scanPackages(String... packages);

    /**
     * Registers custom prefixes which should be known to the application. These prefixes are bound using the configured
     * {@link ApplicationConfigurator} during application creation.
     *
     * @param packages The prefixes to register.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self scanPackages(Set<String> packages);

    Set<String> scanPackages();

    /**
     * Registers a custom {@link ConditionMatcher} which should be known to the application.
     * The condition matcher is used to determine if a component should be activated or not,
     * but can also be applied to any other annotated element.
     *
     * @param conditionMatcher The condition matcher to register.
     * @return The {@link ApplicationBuilder} instance.
     */
    Self conditionMatcher(Initializer<ConditionMatcher> conditionMatcher);

    ConditionMatcher conditionMatcher(final InitializingContext context);

    /**
     * Returns itself, for chaining without losing the fluent API.
     *
     * @return The {@link ApplicationBuilder} instance.
     */
    Self self();

    /**
     * Creates a new {@link ApplicationContext} instance with the components configured in this {@link ApplicationBuilder}.
     *
     * @return The created {@link ApplicationContext}.
     */
    C create();
}
