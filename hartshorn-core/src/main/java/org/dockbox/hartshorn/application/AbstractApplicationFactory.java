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
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.inject.binding.InjectConfiguration;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.logging.ApplicationLogger;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractApplicationFactory<Self extends ApplicationFactory<Self, C>, C extends ApplicationContext> implements ApplicationFactory<Self, C> {

    protected ApplicationConfigurator applicationConfigurator;
    protected ApplicationProxier applicationProxier;
    protected ApplicationFSProvider applicationFSProvider;
    protected ApplicationLogger applicationLogger;
    protected ExceptionHandler exceptionHandler;
    protected BiFunction<PrefixContext, ApplicationManager, ApplicationEnvironment> applicationEnvironment;
    protected Function<ApplicationContext, ComponentLocator> componentLocator;
    protected Function<ApplicationContext, ClasspathResourceLocator> resourceLocator;
    protected Function<ApplicationContext, MetaProvider> metaProvider;
    protected Function<ApplicationContext, ComponentProvider> componentProvider;
    protected Function<ApplicationContext, ComponentPopulator> componentPopulator;
    protected Function<ApplicationManager, PrefixContext> prefixContext;

    protected TypeContext<?> activator;

    protected final Set<InjectConfiguration> injectConfigurations = ConcurrentHashMap.newKeySet();
    protected final Set<Annotation> serviceActivators = ConcurrentHashMap.newKeySet();
    protected final Set<StartupModifiers> modifiers = ConcurrentHashMap.newKeySet();
    protected final Set<String> arguments = ConcurrentHashMap.newKeySet();
    protected final Set<String> prefixes = ConcurrentHashMap.newKeySet();
    protected final Set<ComponentPostProcessor<?>> componentPostProcessors = ConcurrentHashMap.newKeySet();
    protected final Set<ComponentPreProcessor<?>> componentPreProcessors = ConcurrentHashMap.newKeySet();

    protected ApplicationConfigurator applicationConfigurator() {
        return this.applicationConfigurator;
    }

    protected ApplicationProxier applicationProxier() {
        return this.applicationProxier;
    }

    protected ApplicationFSProvider applicationFSProvider() {
        return this.applicationFSProvider;
    }

    protected ApplicationLogger applicationLogger() {
        return this.applicationLogger;
    }

    protected ExceptionHandler exceptionHandler() {
        return this.exceptionHandler;
    }

    protected BiFunction<PrefixContext, ApplicationManager, ApplicationEnvironment> applicationEnvironment() {
        return this.applicationEnvironment;
    }

    protected Function<ApplicationContext, ComponentLocator> componentLocator() {
        return this.componentLocator;
    }

    protected Function<ApplicationContext, ClasspathResourceLocator> resourceLocator() {
        return this.resourceLocator;
    }

    protected Function<ApplicationContext, MetaProvider> metaProvider() {
        return this.metaProvider;
    }

    protected Function<ApplicationContext, ComponentProvider> componentProvider() {
        return this.componentProvider;
    }

    protected Function<ApplicationContext, ComponentPopulator> componentPopulator() {
        return this.componentPopulator;
    }

    protected Function<ApplicationManager, PrefixContext> prefixContext() {
        return this.prefixContext;
    }

    protected TypeContext<?> activator() {
        return this.activator;
    }

    protected Set<InjectConfiguration> injectConfigurations() {
        return this.injectConfigurations;
    }

    protected Set<Annotation> serviceActivators() {
        return this.serviceActivators;
    }

    protected Set<StartupModifiers> modifiers() {
        return this.modifiers;
    }

    protected Set<String> arguments() {
        return this.arguments;
    }

    protected Set<String> prefixes() {
        return this.prefixes;
    }

    protected Set<ComponentPostProcessor<?>> componentPostProcessors() {
        return this.componentPostProcessors;
    }

    protected Set<ComponentPreProcessor<?>> componentPreProcessors() {
        return this.componentPreProcessors;
    }

    @Override
    public Self modifiers(final StartupModifiers... modifiers) {
        this.modifiers.addAll(Set.of(modifiers));
        return this.self();
    }

    @Override
    public Self modifier(final StartupModifiers modifier) {
        this.modifiers.add(modifier);
        return this.self();
    }

    @Override
    public Self activator(final TypeContext<?> activator) {
        final Result<Activator> annotation = activator.annotation(Activator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Application type should be decorated with @Activator");

        if (activator.isAbstract())
            throw new IllegalArgumentException("Bootstrap type cannot be abstract, got " + activator.name());

        this.activator = activator;
        return this.self();
    }

    @Override
    public Self argument(final String argument) {
        this.arguments.add(argument);
        return this.self();
    }

    @Override
    public Self arguments(final String... args) {
        this.arguments.addAll(Set.of(args));
        return this.self();
    }

    @Override
    public Self serviceActivators(final Set<Annotation> annotations) {
        this.serviceActivators.addAll(annotations);
        return this.self();
    }

    @Override
    public Self postProcessor(final ComponentPostProcessor<?> modifier) {
        this.componentPostProcessors.add(modifier);
        return this.self();
    }

    @Override
    public Self preProcessor(final ComponentPreProcessor<?> processor) {
        this.componentPreProcessors.add(processor);
        return this.self();
    }

    @Override
    public Self serviceActivator(final Annotation annotation) {
        this.serviceActivators.add(annotation);
        return this.self();
    }

    @Override
    public Self prefix(final String prefix) {
        this.prefixes.add(prefix);
        return this.self();
    }

    @Override
    public Self prefixes(final Set<String> prefixes) {
        this.prefixes.addAll(prefixes);
        return this.self();
    }

    @Override
    public Self prefixes(final String... prefixes) {
        this.prefixes.addAll(Set.of(prefixes));
        return this.self();
    }

    @Override
    public Self configuration(final InjectConfiguration injectConfiguration) {
        this.injectConfigurations.add(injectConfiguration);
        return this.self();
    }

    @Override
    public Self applicationConfigurator(final ApplicationConfigurator applicationConfigurator) {
        this.applicationConfigurator = applicationConfigurator;
        return this.self();
    }

    @Override
    public Self applicationProxier(final ApplicationProxier applicationProxier) {
        this.applicationProxier = applicationProxier;
        return this.self();
    }

    @Override
    public Self applicationLogger(final ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
        return this.self();
    }

    @Override
    public Self applicationFSProvider(final ApplicationFSProvider applicationFSProvider) {
        this.applicationFSProvider = applicationFSProvider;
        return this.self();
    }

    @Override
    public Self applicationEnvironment(final BiFunction<PrefixContext, ApplicationManager, ApplicationEnvironment> applicationEnvironment) {
        this.applicationEnvironment = applicationEnvironment;
        return this.self();
    }

    @Override
    public Self componentLocator(final Function<ApplicationContext, ComponentLocator> componentLocator) {
        this.componentLocator = componentLocator;
        return this.self();
    }

    @Override
    public Self metaProvider(final Function<ApplicationContext, MetaProvider> metaProvider) {
        this.metaProvider = metaProvider;
        return this.self();
    }

    @Override
    public Self resourceLocator(final Function<ApplicationContext, ClasspathResourceLocator> resourceLocator) {
        this.resourceLocator = resourceLocator;
        return this.self();
    }

    @Override
    public Self exceptionHandler(final ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this.self();
    }

    @Override
    public Self prefixContext(final Function<ApplicationManager, PrefixContext> prefixContext) {
        this.prefixContext = prefixContext;
        return this.self();
    }

    @Override
    public Self componentProvider(final Function<ApplicationContext, ComponentProvider> componentProvider) {
        this.componentProvider = componentProvider;
        return this.self();
    }

    @Override
    public Self componentPopulator(final Function<ApplicationContext, ComponentPopulator> componentPopulator) {
        this.componentPopulator = componentPopulator;
        return this.self();
    }
}
