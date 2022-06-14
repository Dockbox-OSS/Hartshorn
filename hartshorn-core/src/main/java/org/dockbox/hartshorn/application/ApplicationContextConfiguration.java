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

import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
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
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContextConfiguration {

    protected Initializer<ApplicationConfigurator> applicationConfigurator;
    protected Initializer<ApplicationProxier> applicationProxier;
    protected Initializer<ApplicationFSProvider> applicationFSProvider;
    protected Initializer<ExceptionHandler> exceptionHandler;
    protected Initializer<ApplicationArgumentParser> argumentParser;
    protected Initializer<ApplicationLogger> applicationLogger;
    protected Initializer<ApplicationEnvironment> applicationEnvironment;
    protected Initializer<ComponentLocator> componentLocator;
    protected Initializer<ClasspathResourceLocator> resourceLocator;
    protected Initializer<MetaProvider> metaProvider;
    protected Initializer<ComponentProvider> componentProvider;
    protected Initializer<ComponentPopulator> componentPopulator;
    protected Initializer<PrefixContext> prefixContext;
    protected Initializer<ActivatorHolder> activatorHolder;
    protected Initializer<ConditionMatcher> conditionMatcher;

    protected TypeContext<?> activator;

    protected final Set<Annotation> serviceActivators = ConcurrentHashMap.newKeySet();
    protected final Set<String> arguments = ConcurrentHashMap.newKeySet();
    protected final Set<String> prefixes = ConcurrentHashMap.newKeySet();
    protected final Set<ComponentPostProcessor> componentPostProcessors = ConcurrentHashMap.newKeySet();
    protected final Set<ComponentPreProcessor> componentPreProcessors = ConcurrentHashMap.newKeySet();

    public ApplicationConfigurator applicationConfigurator(final InitializingContext context) {
        return this.applicationConfigurator.initialize(context);
    }

    public ApplicationProxier applicationProxier(final InitializingContext context) {
        return this.applicationProxier.initialize(context);
    }

    public ConditionMatcher conditionMatcher(final InitializingContext context) {
        return this.conditionMatcher.initialize(context);
    }

    public ApplicationFSProvider applicationFSProvider(final InitializingContext context) {
        return this.applicationFSProvider.initialize(context);
    }

    public ApplicationLogger applicationLogger(final InitializingContext context) {
        return this.applicationLogger.initialize(context);
    }

    public ExceptionHandler exceptionHandler(final InitializingContext context) {
        return this.exceptionHandler.initialize(context);
    }

    public ApplicationArgumentParser argumentParser(final InitializingContext context) {
        return this.argumentParser.initialize(context);
    }

    public ApplicationEnvironment applicationEnvironment(final InitializingContext context) {
        return this.applicationEnvironment.initialize(context);
    }

    public ComponentLocator componentLocator(final InitializingContext context) {
        return this.componentLocator.initialize(context);
    }

    public ClasspathResourceLocator resourceLocator(final InitializingContext context) {
        return this.resourceLocator.initialize(context);
    }

    public MetaProvider metaProvider(final InitializingContext context) {
        return this.metaProvider.initialize(context);
    }

    public ComponentProvider componentProvider(final InitializingContext context) {
        return this.componentProvider.initialize(context);
    }

    public ComponentPopulator componentPopulator(final InitializingContext context) {
        return this.componentPopulator.initialize(context);
    }

    public PrefixContext prefixContext(final InitializingContext context) {
        return this.prefixContext.initialize(context);
    }

    public ActivatorHolder activatorHolder(final InitializingContext context) {
        return this.activatorHolder.initialize(context);
    }

    public TypeContext<?> activator() {
        return this.activator;
    }

    public Set<Annotation> serviceActivators() {
        return this.serviceActivators;
    }

    public Set<String> arguments() {
        return this.arguments;
    }

    public Set<String> prefixes() {
        return this.prefixes;
    }

    public Set<ComponentPostProcessor> componentPostProcessors() {
        return this.componentPostProcessors;
    }

    public Set<ComponentPreProcessor> componentPreProcessors() {
        return this.componentPreProcessors;
    }
}
