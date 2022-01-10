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
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.core.context.PrefixContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractActivatingApplicationFactory<
        Self extends ApplicationFactory<Self, C>,
        C extends SelfActivatingApplicationContext,
        M extends ObservableApplicationManager & ModifiableContextCarrier
        > extends AbstractApplicationFactory<Self, C> {

    private enum FactoryState {
        WAITING, CREATING
    }

    private FactoryState state = FactoryState.WAITING;
    @Getter(AccessLevel.PROTECTED)
    private Logger logger;

    @Override
    public C create() {
        if (this.state == FactoryState.CREATING) {
            throw new IllegalStateException("Application factory is already creating a new application context");
        }
        this.state = FactoryState.CREATING;
        this.validate();

        this.logger = LoggerFactory.getLogger(this.activator.type());
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        // Alternative to InetAddress.getLocalHost().getHostName()
        final String host = runtimeMXBean.getName().split("@")[1];

        this.logger().info("Starting application " + this.activator.name() + " on " + host + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        final long applicationStartTimestamp = System.currentTimeMillis();
        final M manager = this.createManager();

        final PrefixContext prefixContext = this.prefixContext.apply(manager);
        this.prefixes.forEach(prefixContext::prefix);
        final ApplicationEnvironment environment = this.applicationEnvironment.apply(prefixContext, manager);

        final C applicationContext = this.createContext(environment);

        manager.applicationContext(applicationContext);

        applicationContext.addActivator(new ServiceImpl());

        final ApplicationConfigurator configurator = this.applicationConfigurator;
        configurator.configure(manager);

        for (final Annotation serviceActivator : this.serviceActivators)
            applicationContext.addActivator(serviceActivator);

        // Always load Hartshorn internals first
        configurator.bind(manager, Hartshorn.PACKAGE_PREFIX);

        final Activator activator = this.activatorAnnotation();
        final Set<String> scanPackages = Set.of(activator.scanPackages());
        final Collection<String> scanPrefixes = HartshornUtils.merge(this.prefixes, scanPackages);

        if (activator.includeBasePackage())
            scanPrefixes.add(this.activator.type().getPackageName());

        final Set<InjectConfiguration> configurations = Arrays.stream(activator.configs())
                .map(InjectConfig::value)
                .map(TypeContext::of)
                .map(applicationContext::get)
                .collect(Collectors.toSet());

        configurator.apply(manager, configurations);
        configurator.apply(manager, this.injectConfigurations);

        for (final String prefix : scanPrefixes)
            configurator.bind(manager, prefix);

        applicationContext.processPrefixQueue();
        applicationContext.lookupActivatables();

        this.componentPreProcessors.forEach(applicationContext::add);
        applicationContext.process();

        this.componentPostProcessors.forEach(applicationContext::add);

        for (final LifecycleObserver observer : manager.observers())
            observer.onStarted(applicationContext);

        final long applicationStartedTimestamp = System.currentTimeMillis();



        final double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        final double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        this.logger().info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        this.state = FactoryState.WAITING;

        return applicationContext;
    }

    protected void validate() {
        if (this.applicationConfigurator == null) throw new IllegalArgumentException("Application configurator is not set");
        if (this.applicationProxier == null) throw new IllegalArgumentException("Application proxier is not set");
        if (this.applicationLogger == null) throw new IllegalArgumentException("Application logger is not set");
        if (this.activator == null) throw new IllegalArgumentException("Application activator is not set");
        if (this.exceptionHandler == null) throw new IllegalArgumentException("Exception handler is not set");
        if (this.componentLocator == null) throw new IllegalArgumentException("Component locator is not set");
        if (this.resourceLocator == null) throw new IllegalArgumentException("Resource locator is not set");
        if (this.metaProvider == null) throw new IllegalArgumentException("Meta provider is not set");
    }

    protected void registerHooks(final C applicationContext, final M manager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.logger().info("Runtime shutting down, notifying observers");
            for (final LifecycleObserver observer : manager.observers()) {
                this.logger().debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                observer.onExit(applicationContext);
            }
        }));
    }

    public abstract Self loadDefaults();

    protected abstract M createManager();

    protected abstract C createContext(final ApplicationEnvironment environment);

    protected abstract Activator activatorAnnotation();
}
