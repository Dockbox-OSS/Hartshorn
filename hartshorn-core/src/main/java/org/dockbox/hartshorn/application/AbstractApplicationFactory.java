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
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Set;

public abstract class AbstractApplicationFactory<Self extends ApplicationFactory<Self, C>, C extends ApplicationContext> implements ApplicationFactory<Self, C> {

    private final ApplicationContextConfiguration configuration = new ApplicationContextConfiguration();

    public ApplicationContextConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public Self activator(final TypeContext<?> activator) {
        final Result<Activator> annotation = activator.annotation(Activator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Application type should be decorated with @Activator");

        if (activator.isAbstract())
            throw new IllegalArgumentException("Bootstrap type cannot be abstract, got " + activator.name());

        this.configuration.activator = activator;
        return this.self();
    }

    @Override
    public Self argument(final String argument) {
        this.configuration.arguments.add(argument);
        return this.self();
    }

    @Override
    public Self arguments(final String... args) {
        this.configuration.arguments.addAll(Set.of(args));
        return this.self();
    }

    @Override
    public Self serviceActivators(final Set<Annotation> annotations) {
        this.configuration.serviceActivators.addAll(annotations);
        return this.self();
    }

    @Override
    public Self postProcessor(final ComponentPostProcessor modifier) {
        this.configuration.componentPostProcessors.add(modifier);
        return this.self();
    }

    @Override
    public Self preProcessor(final ComponentPreProcessor processor) {
        this.configuration.componentPreProcessors.add(processor);
        return this.self();
    }

    @Override
    public Self serviceActivator(final Annotation annotation) {
        this.configuration.serviceActivators.add(annotation);
        return this.self();
    }

    @Override
    public Self prefix(final String prefix) {
        this.configuration.prefixes.add(prefix);
        return this.self();
    }

    @Override
    public Self prefixes(final Set<String> prefixes) {
        this.configuration.prefixes.addAll(prefixes);
        return this.self();
    }

    @Override
    public Self prefixes(final String... prefixes) {
        this.configuration.prefixes.addAll(Set.of(prefixes));
        return this.self();
    }

    @Override
    public Self activatorHolder(final Initializer<ActivatorHolder> activatorHolder) {
        this.configuration.activatorHolder = activatorHolder.cached();
        return this.self();
    }

    @Override
    public Self applicationConfigurator(final Initializer<ApplicationConfigurator> applicationConfigurator) {
        this.configuration.applicationConfigurator = applicationConfigurator.cached();
        return this.self();
    }

    @Override
    public Self applicationProxier(final Initializer<ApplicationProxier> applicationProxier) {
        this.configuration.applicationProxier = applicationProxier.cached();
        return this.self();
    }

    @Override
    public Self applicationLogger(final Initializer<ApplicationLogger> applicationLogger) {
        this.configuration.applicationLogger = applicationLogger.cached();
        return this.self();
    }

    @Override
    public Self applicationFSProvider(final Initializer<ApplicationFSProvider> applicationFSProvider) {
        this.configuration.applicationFSProvider = applicationFSProvider.cached();
        return this.self();
    }

    @Override
    public Self applicationEnvironment(final Initializer<ApplicationEnvironment> applicationEnvironment) {
        this.configuration.applicationEnvironment = applicationEnvironment.cached();
        return this.self();
    }

    @Override
    public Self componentLocator(final Initializer<ComponentLocator> componentLocator) {
        this.configuration.componentLocator = componentLocator.cached();
        return this.self();
    }

    @Override
    public Self metaProvider(final Initializer<MetaProvider> metaProvider) {
        this.configuration.metaProvider = metaProvider.cached();
        return this.self();
    }

    @Override
    public Self resourceLocator(final Initializer<ClasspathResourceLocator> resourceLocator) {
        this.configuration.resourceLocator = resourceLocator.cached();
        return this.self();
    }

    @Override
    public Self exceptionHandler(final Initializer<ExceptionHandler> exceptionHandler) {
        this.configuration.exceptionHandler = exceptionHandler.cached();
        return this.self();
    }

    @Override
    public Self argumentParser(final Initializer<ApplicationArgumentParser> argumentParser) {
        this.configuration.argumentParser = argumentParser.cached();
        return this.self();
    }

    @Override
    public Self prefixContext(final Initializer<PrefixContext> prefixContext) {
        this.configuration.prefixContext = prefixContext.cached();
        return this.self();
    }

    @Override
    public Self componentProvider(final Initializer<ComponentProvider> componentProvider) {
        this.configuration.componentProvider = componentProvider.cached();
        return this.self();
    }

    @Override
    public Self componentPopulator(final Initializer<ComponentPopulator> componentPopulator) {
        this.configuration.componentPopulator = componentPopulator.cached();
        return this.self();
    }

    @Override
    public Self conditionMatcher(final Initializer<ConditionMatcher> conditionMatcher) {
        this.configuration.conditionMatcher = conditionMatcher.cached();
        return this.self();
    }

    @Override
    public Self manager(final Initializer<ApplicationManager> manager) {
        this.configuration.manager = manager.cached();
        return this.self();
    }
}
