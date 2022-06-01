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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.component.processing.ActivatorFiltered;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.binding.InjectConfiguration;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;

/**
 * The {@link ApplicationFactory} is responsible for creating the {@link ApplicationContext} and constructing the
 * required components. By default, all components are required, and will be validated before a {@link ApplicationContext}
 * is created.
 *
 * @param <Self> The type of the {@link ApplicationFactory}, used to implement the fluent API.
 * @param <C> The type of the {@link ApplicationContext} that is created.
 * @author Guus Lieben
 * @since 21.9
 */
public interface ApplicationFactory<Self extends ApplicationFactory<Self, C>, C extends ApplicationContext> {

    /**
     * Sets the application activator to use. Depending on the type of application environment, this may require the
     * activator to have relevant {@link ServiceActivator service activators}, and a valid {@link Activator} annotation.
     * Alternative metadata sources may be used, depending on the application environment.
     *
     * @param activator The activator to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self activator(TypeContext<?> activator);

    /**
     * Adds a commandline argument to the application. These are later parsed by the active {@link ApplicationContext}.
     *
     * @param argument The argument to add.
     * @return The {@link ApplicationFactory} instance.
     */
    Self argument(String argument);

    /**
     * Adds commandline arguments to the application. These are later parsed by the active {@link ApplicationContext}.
     *
     * @param args The arguments to add.
     * @return The {@link ApplicationFactory} instance.
     */
    Self arguments(String... args);

    /**
     * Adds a service activator to the application. These are later used by the active {@link ApplicationContext} to
     * indicate which {@link ActivatorFiltered} implementations may be used. Service
     * activators should always be valid {@link ServiceActivator}s.
     *
     * @param annotation The service activator to add.
     * @return The {@link ApplicationFactory} instance.
     * @see ActivatorHolder
     */
    Self serviceActivator(Annotation annotation);

    /**
     * Adds service activators to the application. These are later used by the active {@link ApplicationContext} to
     * indicate which {@link ActivatorFiltered} implementations may be used. Service
     * activators should always be valid {@link ServiceActivator}s.
     *
     * @param annotations The service activators to add.
     * @return The {@link ApplicationFactory} instance.
     * @see ActivatorHolder
     */
    Self serviceActivators(Set<Annotation> annotations);

    /**
     * Sets the {@link ApplicationConfigurator} to use. This is used to configure the application environment after
     * the {@link ApplicationContext} is created.
     *
     * @param applicationConfigurator The application configurator to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationConfigurator
     */
    Self applicationConfigurator(Initializer<ApplicationConfigurator> applicationConfigurator);

    /**
     * Sets the {@link ApplicationProxier} to use. This is responsible for component and service proxying during the entire
     * application lifetime.
     *
     * @param applicationProxier The application proxier to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationProxier
     */
    Self applicationProxier(Initializer<ApplicationProxier> applicationProxier);

    /**
     * Sets the {@link ApplicationLogger} to use. This is responsible for all logging purposes and should expose a valid
     * {@link org.slf4j.Logger} instance.
     *
     * @param applicationLogger The application logger to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self applicationLogger(Initializer<ApplicationLogger> applicationLogger);

    /**
     * Sets the {@link ApplicationFSProvider} to use. This is responsible for all filesystem operations and should expose
     * a valid {@link Path} representing the application base path or working directory.
     *
     * @param applicationFSProvider The application filesystem provider to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self applicationFSProvider(Initializer<ApplicationFSProvider> applicationFSProvider);

    /**
     * Sets the {@link ApplicationEnvironment} to use. The environment is responsible for keeping track of known prefixes
     * and type hierarchies. The provider used a {@link Function} as the environment should typically be bound to a given
     * {@link ApplicationContext}. The {@link ApplicationEnvironment} is created by the {@link ApplicationContext}.
     *
     * @param applicationEnvironment The application environment to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationEnvironment
     */
    Self applicationEnvironment(Initializer<ApplicationEnvironment> applicationEnvironment);

    /**
     * Sets the {@link ComponentLocator} to use. The locator is responsible for locating components and services, as well
     * as keeping track of registered components and services. The provider used a {@link Function} as the locator should
     * typically be bound to a given {@link ApplicationContext}. The {@link ComponentLocator} is created by the
     * {@link ApplicationContext}.
     *
     * @param componentLocator The component locator to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ComponentLocator
     */
    Self componentLocator(Initializer<ComponentLocator> componentLocator);

    /**
     * Sets the {@link MetaProvider} to use. The meta provider is responsible for providing meta information about types.
     *
     * @param metaProvider The meta provider to use.
     * @return The {@link ApplicationFactory} instance.
     * @see MetaProvider
     */
    Self metaProvider(Initializer<MetaProvider> metaProvider);

    /**
     * Sets the {@link ClasspathResourceLocator} to use. The classpath resource locator is responsible for locating resources on
     * the classpath, and make them available to the application.
     *
     * @param resourceLocator The classpath resource locator to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ClasspathResourceLocator
     */
    Self resourceLocator(Initializer<ClasspathResourceLocator> resourceLocator);

    /**
     * Sets the {@link ExceptionHandler} to use. The exception handler is responsible for handling exceptions during the entire
     * application lifecycle.
     *
     * @param exceptionHandler The exception handler to use.
     * @return The {@link ApplicationFactory} instance.
     * @see ExceptionHandler
     */
    Self exceptionHandler(Initializer<ExceptionHandler> exceptionHandler);

    /**
     * Sets the {@link ApplicationArgumentParser} to use. The argument parser is responsible for parsing command line
     * arguments and making them available to the application.
     *
     * @param argumentParser The argument parser to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self argumentParser(Initializer<ApplicationArgumentParser> argumentParser);

    /**
     * Sets the {@link PrefixContext} to use. The prefix context is responsible for keeping track of known prefixes and provide
     * access to annotated- and subtypes in the application.
     *
     * @param prefixContext The prefix context to use.
     * @return The {@link ApplicationFactory} instance.
     * @see PrefixContext
     */
    Self prefixContext(Initializer<PrefixContext> prefixContext);

    /**
     * Sets the {@link ComponentProvider} to use. The component provider is responsible for providing components and services
     * to the application. This acts as the primary component provider.
     *
     * @param componentProvider The component provider to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self componentProvider(Initializer<ComponentProvider> componentProvider);

    /**
     * Sets the {@link ComponentPopulator} to use. The component populator is responsible for populating components and services
     * created by the component provider.
     *
     * @param componentPopulator The component populator to use.
     * @return The {@link ApplicationFactory} instance.
     */
    Self componentPopulator(Initializer<ComponentPopulator> componentPopulator);

    /**
     * Registers a custom {@link ComponentPostProcessor}. Unlike automatically activated {@link ComponentPostProcessor}s,
     * custom {@link ComponentPostProcessor}s are always added even if their activator is absent.
     *
     * @param postProcessor The post processor to register
     * @return The {@link ApplicationFactory} instance.
     * @see ComponentPostProcessor
     */
    Self postProcessor(ComponentPostProcessor postProcessor);

    /**
     * Registers a custom {@link ComponentPreProcessor}. Unlike automatically activated {@link ComponentPreProcessor}s,
     * custom {@link ComponentPreProcessor}s are always added even if their activator is absent.
     *
     * @param preProcessor The pre processor to register
     * @return The {@link ApplicationFactory} instance.
     * @see ComponentPreProcessor
     */
    Self preProcessor(ComponentPreProcessor preProcessor);

    /**
     * Registers a custom package prefix which should be known to the application. These prefixes are bound using the
     * configured {@link ApplicationConfigurator} during application creation.
     *
     * @param prefix The prefix to register.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationConfigurator#bind(ApplicationManager, String)
     * @see ApplicationEnvironment#prefix(String)
     */
    Self prefix(String prefix);

    /**
     * Registers custom prefixes which should be known to the application. These prefixes are bound using the configured
     * {@link ApplicationConfigurator} during application creation.
     *
     * @param prefixes The prefixes to register.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationConfigurator#bind(ApplicationManager, String)
     * @see ApplicationEnvironment#prefix(String)
     */
    Self prefixes(String... prefixes);

    /**
     * Registers custom prefixes which should be known to the application. These prefixes are bound using the configured
     * {@link ApplicationConfigurator} during application creation.
     *
     * @param prefixes The prefixes to register.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationConfigurator#bind(ApplicationManager, String)
     * @see ApplicationEnvironment#prefix(String)
     */
    Self prefixes(Set<String> prefixes);

    /**
     * Registers a custom {@link InjectConfiguration} which should be known to the application. The configuration is bound
     * using the configured {@link ApplicationConfigurator} during application creation.
     *
     * @param injectConfiguration The configuration to register.
     * @return The {@link ApplicationFactory} instance.
     * @see ApplicationConfigurator#apply(ApplicationManager, Set)
     */
    @Deprecated(since = "22.3", forRemoval = true)
    Self configuration(InjectConfiguration injectConfiguration);

    /**
     * Registers a custom {@link ActivatorHolder} which should be known to the application.
     *
     * @param activatorHolder The activator holder to register.
     * @return The {@link ApplicationFactory} instance.
     */
    Self activatorHolder(Initializer<ActivatorHolder> activatorHolder);

    /**
     * Registers a custom {@link ConditionMatcher} which should be known to the application.
     * The condition matcher is used to determine if a component should be activated or not,
     * but can also be applied to any other annotated element.
     *
     * @param conditionMatcher The condition matcher to register.
     * @return The {@link ApplicationFactory} instance.
     */
    Self conditionMatcher(Initializer<ConditionMatcher> conditionMatcher);

    /**
     * Returns itself, for chaining without losing the fluent API.
     *
     * @return The {@link ApplicationFactory} instance.
     */
    Self self();

    /**
     * Creates a new {@link ApplicationContext} instance with the components configured in this {@link ApplicationFactory}.
     *
     * @return The created {@link ApplicationContext}.
     */
    C create();
}
