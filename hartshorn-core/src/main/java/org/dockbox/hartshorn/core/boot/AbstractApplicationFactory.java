/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.Modifiers;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.PrefixContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PROTECTED)
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
    protected Function<ApplicationManager, PrefixContext> prefixContext;

    protected TypeContext<?> activator;

    protected final Set<InjectConfiguration> injectConfigurations = HartshornUtils.emptyConcurrentSet();
    protected final Set<Annotation> serviceActivators = HartshornUtils.emptyConcurrentSet();
    protected final Set<Modifiers> modifiers = HartshornUtils.emptyConcurrentSet();
    protected final Set<String> arguments = HartshornUtils.emptyConcurrentSet();
    protected final Set<String> prefixes = HartshornUtils.emptyConcurrentSet();
    protected final Set<ComponentPostProcessor<?>> componentPostProcessors = HartshornUtils.emptyConcurrentSet();
    protected final Set<ComponentPreProcessor<?>> componentPreProcessors = HartshornUtils.emptyConcurrentSet();

    @Override
    public Self modifiers(final Modifiers... modifiers) {
        this.modifiers.addAll(Set.of(modifiers));
        return this.self();
    }

    @Override
    public Self modifier(final Modifiers modifier) {
        this.modifiers.add(modifier);
        return this.self();
    }

    @Override
    public Self activator(final TypeContext<?> activator) {
        final Exceptional<Activator> annotation = activator.annotation(Activator.class);
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
}
