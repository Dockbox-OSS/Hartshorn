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

import org.dockbox.hartshorn.application.context.SelfActivatingApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.component.ComponentContainer;
import org.dockbox.hartshorn.component.ComponentType;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.inject.binding.InjectConfig;
import org.dockbox.hartshorn.inject.binding.InjectConfiguration;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractActivatingApplicationFactory<
        Self extends ApplicationFactory<Self, C>,
        C extends SelfActivatingApplicationContext,
        M extends ObservableApplicationManager & ModifiableContextCarrier
        > extends AbstractApplicationFactory<Self, C> {

    private enum FactoryState {
        WAITING, CREATING
    }

    private FactoryState state = FactoryState.WAITING;
    private Logger logger;

    protected Logger logger() {
        return this.logger;
    }

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
        final Collection<String> scanPrefixes = CollectionUtilities.merge(this.prefixes, scanPackages);

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

        this.componentPreProcessors.forEach(applicationContext::add);
        applicationContext.process();

        this.componentPostProcessors.forEach(applicationContext::add);

        for (final LifecycleObserver observer : manager.observers())
            observer.onStarted(applicationContext);

        for (final ComponentContainer container : applicationContext.locator().containers(ComponentType.FUNCTIONAL)) {
            if (container.singleton() && !container.lazy()) {
                applicationContext.get(container.type());
            }
        }

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
            try {
                applicationContext.close();
            } catch (final IOException e) {
                this.logger().error("Failed to close application context", e);
            }
        }, "ShutdownHook"));
    }

    public abstract Self loadDefaults();

    protected abstract M createManager();

    protected abstract C createContext(final ApplicationEnvironment environment);

    protected abstract Activator activatorAnnotation();
}
