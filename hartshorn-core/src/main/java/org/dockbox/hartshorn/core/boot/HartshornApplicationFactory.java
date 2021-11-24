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
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.annotations.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.HartshornApplicationContext;
import org.dockbox.hartshorn.core.context.HartshornApplicationEnvironment;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.inject.InjectionModifier;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentLocatorImpl;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

public class HartshornApplicationFactory implements ApplicationFactory<HartshornApplicationFactory, HartshornApplicationContext> {

    @Getter
    private final HartshornApplicationFactory self = this;

    @Setter
    private ApplicationConfigurator applicationConfigurator;
    @Setter
    private ApplicationProxier applicationProxier;
    @Setter
    private ApplicationFSProvider applicationFSProvider;
    @Setter
    private ApplicationLogger applicationLogger;
    @Setter
    private Function<ApplicationManager, ApplicationEnvironment> applicationEnvironment;
    @Setter
    private Function<ApplicationContext, ComponentLocator> componentLocator;

    private TypeContext<?> activator;

    private final Set<InjectConfiguration> injectConfigurations = HartshornUtils.emptyConcurrentSet();
    private final Set<Annotation> serviceActivators = HartshornUtils.emptyConcurrentSet();
    private final Set<Modifier> modifiers = HartshornUtils.emptyConcurrentSet();
    private final Set<String> arguments = HartshornUtils.emptyConcurrentSet();
    private final Set<String> prefixes = HartshornUtils.emptyConcurrentSet();
    private final Set<InjectionModifier<?>> injectionModifiers = HartshornUtils.emptyConcurrentSet();
    private final Set<ComponentProcessor<?>> componentProcessors = HartshornUtils.emptyConcurrentSet();

    @Override
    public HartshornApplicationFactory modifiers(final Modifier... modifiers) {
        this.modifiers.addAll(HartshornUtils.asSet(modifiers));
        return this.self();
    }

    @Override
    public HartshornApplicationFactory modifier(final Modifier modifier) {
        this.modifiers.add(modifier);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory activator(final TypeContext<?> activator) {
        final Exceptional<Activator> annotation = activator.annotation(Activator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Application type should be decorated with @Activator");

        if (activator.isAbstract())
            throw new IllegalArgumentException("Bootstrap type cannot be abstract, got " + activator.name());

        this.activator = activator;
        return this.self();
    }

    @Override
    public HartshornApplicationFactory argument(final String argument) {
        this.arguments.add(argument);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory arguments(final String... args) {
        this.arguments.addAll(HartshornUtils.asSet(args));
        return this.self();
    }

    @Override
    public HartshornApplicationFactory serviceActivators(final Set<Annotation> annotations) {
        this.serviceActivators.addAll(annotations);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory injectionModifier(final InjectionModifier<?> modifier) {
        this.injectionModifiers.add(modifier);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory componentProcessor(final ComponentProcessor<?> processor) {
        this.componentProcessors.add(processor);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory serviceActivator(final Annotation annotation) {
        this.serviceActivators.add(annotation);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory prefix(final String prefix) {
        this.prefixes.add(prefix);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory prefixes(final Set<String> prefixes) {
        this.prefixes.addAll(prefixes);
        return this.self();
    }

    @Override
    public HartshornApplicationFactory prefixes(final String... prefixes) {
        this.prefixes.addAll(HartshornUtils.asSet(prefixes));
        return this.self();
    }

    @Override
    public HartshornApplicationFactory configuration(final InjectConfiguration injectConfiguration) {
        this.injectConfigurations.add(injectConfiguration);
        return this.self();
    }

    @Override
    public HartshornApplicationContext create() {
        this.validate();

        final Logger logger = LoggerFactory.getLogger(this.activator.type());
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        // Alternative to InetAddress.getLocalHost().getHostName()
        final String host = runtimeMXBean.getName().split("@")[1];

        logger.info("Starting application " + this.activator.name() + " on " + host + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        final long applicationStartTimestamp = System.currentTimeMillis();
        final HartshornApplicationManager manager = new HartshornApplicationManager(this.activator, this.applicationLogger, this.applicationProxier, this.applicationFSProvider);
        final ApplicationEnvironment environment = this.applicationEnvironment.apply(manager);

        final HartshornApplicationContext applicationContext = new HartshornApplicationContext(environment, this.componentLocator, this.activator, this.prefixes, this.arguments, this.modifiers);
        manager.applicationContext(applicationContext);

        final ApplicationConfigurator configurator = this.applicationConfigurator;
        configurator.configure(manager);

        for (final LifecycleObserver observer : manager.observers())
            observer.onCreated(applicationContext);

        for (final Annotation serviceActivator : this.serviceActivators)
            applicationContext.addActivator(serviceActivator);

        applicationContext.lookupActivatables();

        this.componentProcessors.forEach(applicationContext::add);
        this.injectionModifiers.forEach(applicationContext::add);

        final Activator activator = this.activatorAnnotation();
        final Set<InjectConfiguration> configurations = Arrays.stream(activator.configs())
                .map(InjectConfig::value)
                .map(TypeContext::of)
                .map(applicationContext::raw)
                .collect(Collectors.toSet());

        configurator.apply(manager, configurations);
        configurator.apply(manager, this.injectConfigurations);

        final Set<String> scanPackages = HartshornUtils.asSet(activator.scanPackages());
        final Collection<String> scanPrefixes = HartshornUtils.merge(this.prefixes, scanPackages);

        if (activator.includeBasePackage())
            scanPrefixes.add(this.activator.type().getPackageName());

        // Always load Hartshorn internals first
        configurator.bind(manager, Hartshorn.PACKAGE_PREFIX);

        for (final String prefix : scanPrefixes)
            configurator.bind(manager, prefix);

        for (final LifecycleObserver observer : manager.observers())
            observer.onStarted(applicationContext);

        final long applicationStartedTimestamp = System.currentTimeMillis();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Runtime shutting down, notifying observers");
            for (final LifecycleObserver observer : manager.observers()) {
                logger.debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                observer.onExit(applicationContext);
            }
        }));

        final double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        final double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        logger.info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        return applicationContext;
    }

    private Activator activatorAnnotation() {
        return this.activator.annotation(Activator.class).get();
    }

    private void validate() {
        if (this.applicationConfigurator == null)
            throw new IllegalArgumentException("Application configurator is not set");
        if (this.applicationProxier == null) throw new IllegalArgumentException("Application proxier is not set");
        if (this.applicationLogger == null) throw new IllegalArgumentException("Application logger is not set");
        if (this.activator == null) throw new IllegalArgumentException("Application activator is not set");
    }

    public HartshornApplicationFactory loadDefaults() {
        return this.applicationLogger(new HartshornApplicationLogger())
                .applicationConfigurator(new HartshornApplicationConfigurator())
                .applicationProxier(new HartshornApplicationProxier())
                .applicationFSProvider(new HartshornApplicationFSProvider())
                .applicationEnvironment(manager -> new HartshornApplicationEnvironment(this.prefixes, manager))
                .componentLocator(ComponentLocatorImpl::new)
                .serviceActivator(new UseBootstrap() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return UseBootstrap.class;
                    }
                }).serviceActivator(new UseProxying() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return UseProxying.class;
                    }
                });
    }
}
