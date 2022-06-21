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
import org.dockbox.hartshorn.application.environment.ApplicationFSProviderImpl;
import org.dockbox.hartshorn.application.environment.ClassLoaderClasspathResourceLocator;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.DelegatingApplicationManager;
import org.dockbox.hartshorn.application.environment.StandardApplicationArgumentParser;
import org.dockbox.hartshorn.application.scan.ReflectionsPrefixContext;
import org.dockbox.hartshorn.component.ComponentLocatorImpl;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.component.HierarchicalApplicationComponentProvider;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.InjectorMetaProvider;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.logging.logback.LogbackApplicationLogger;
import org.dockbox.hartshorn.proxy.UseProxying;
import org.dockbox.hartshorn.proxy.cglib.CglibApplicationProxier;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.function.Function;

public class StandardApplicationFactory extends AbstractApplicationFactory<StandardApplicationFactory, ApplicationContext> {

    private enum FactoryState {
        WAITING, CREATING
    }

    private FactoryState state = FactoryState.WAITING;
    protected Function<Logger, ApplicationContextConstructor<ApplicationContext>> constructor;

    public StandardApplicationFactory constructor(final Function<Logger, ApplicationContextConstructor<ApplicationContext>> constructor) {
        this.constructor = constructor;
        return this.self();
    }

    @Override
    public ApplicationContext create() {
        if (this.state == FactoryState.CREATING) {
            throw new IllegalStateException("Application factory is already creating a new application context");
        }
        this.state = FactoryState.CREATING;
        this.validate();

        final Logger logger = LoggerFactory.getLogger(this.configuration().activator().type());

        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        logger.info("Starting application " + this.configuration().activator().name() + " on " + this.host(runtimeMXBean) + " using Java " + runtimeMXBean.getVmVersion() + " with PID " + runtimeMXBean.getPid());

        final long applicationStartTimestamp = System.currentTimeMillis();
        final ApplicationContext applicationContext = this.constructor.apply(logger).createContext(this.configuration());
        final long applicationStartedTimestamp = System.currentTimeMillis();

        final double startupTime = ((double) (applicationStartedTimestamp - applicationStartTimestamp)) / 1000;
        final double jvmUptime = ((double) runtimeMXBean.getUptime()) / 1000;

        logger.info("Started " + Hartshorn.PROJECT_NAME + " in " + startupTime + " seconds (JVM running for " + jvmUptime + ")");

        this.state = FactoryState.WAITING;

        return applicationContext;
    }

    protected String host(final RuntimeMXBean runtimeMXBean) {
        // Alternative to InetAddress.getLocalHost().getHostName()
        return runtimeMXBean.getName().split("@")[1];
    }

    protected void validate() {
        this.require(this.configuration().activator, "Application activator");
        this.require(this.configuration().applicationConfigurator, "Application configurator");
        this.require(this.configuration().applicationProxier, "Application proxier");
        this.require(this.configuration().exceptionHandler, "Exception handler");
        this.require(this.configuration().componentLocator, "Component locator");
        this.require(this.configuration().resourceLocator, "Resource locator");
        this.require(this.configuration().metaProvider, "Meta provider");
        this.require(this.configuration().applicationFSProvider, "Filesystem provider");
        this.require(this.configuration().argumentParser, "Argument parser");
        this.require(this.configuration().applicationLogger, "Application logger");
        this.require(this.configuration().applicationEnvironment, "Application environment");
        this.require(this.configuration().componentProvider, "Component provider");
        this.require(this.configuration().componentPopulator, "Component populator");
        this.require(this.configuration().prefixContext, "Prefix context");
        this.require(this.configuration().activatorHolder, "Activator holder");
        this.require(this.configuration().conditionMatcher, "Condition matcher");
    }

    protected void require(final Object instance, final String type) {
        if (instance == null) throw new IllegalArgumentException(type + " is not set");
    }

    public StandardApplicationFactory deduceActivator() {
        return this.activator(this.deduceApplicationActivator());
    }

    public StandardApplicationFactory loadDefaults() {
        return this.constructor(StandardApplicationContextConstructor::new)
                .manager(ctx -> new DelegatingApplicationManager(ctx.configuration()))
                .applicationLogger(ctx -> new LogbackApplicationLogger())
                .applicationConfigurator(ctx -> new EnvironmentDrivenApplicationConfigurator())
                .applicationProxier(ctx -> new CglibApplicationProxier())
                .applicationFSProvider(ctx -> new ApplicationFSProviderImpl())
                .applicationEnvironment(ctx -> new ContextualApplicationEnvironment(ctx.configuration().prefixContext(ctx), ctx.manager()))
                .exceptionHandler(ctx -> new LoggingExceptionHandler())
                .prefixContext(ctx -> new ReflectionsPrefixContext(ctx.manager()))
                .componentLocator(ComponentLocatorImpl::new)
                .resourceLocator(ctx -> new ClassLoaderClasspathResourceLocator(ctx.applicationContext()))
                .metaProvider(ctx -> new InjectorMetaProvider(ctx.applicationContext()))
                .componentProvider(ctx -> new HierarchicalApplicationComponentProvider(ctx.applicationContext(), ctx.componentLocator(), ctx.metaProvider()))
                .componentPopulator(ctx -> new ContextualComponentPopulator(ctx.applicationContext()))
                .argumentParser(ctx -> new StandardApplicationArgumentParser())
                .activatorHolder(ctx -> new StandardActivatorHolder(ctx.applicationContext()))
                .conditionMatcher(ctx -> new ConditionMatcher(ctx.applicationContext()))
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
                }).serviceActivator(new UseServiceProvision() {
                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return UseServiceProvision.class;
                    }
                });
    }

    private TypeContext<?> deduceApplicationActivator() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (final StackTraceElement element : stackTrace) {
            if ("main".equals(element.getMethodName())) {
                final TypeContext<?> context = TypeContext.lookup(element.getClassName());
                if (context.annotation(Activator.class).present()) {
                    return context;
                }
            }
        }
        return null;
    }

    @Override
    public StandardApplicationFactory self() {
        return this;
    }
}
