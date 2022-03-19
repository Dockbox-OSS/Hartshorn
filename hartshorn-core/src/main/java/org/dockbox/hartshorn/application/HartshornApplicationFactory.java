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
import org.dockbox.hartshorn.application.context.StandardDelegatingApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProviderImpl;
import org.dockbox.hartshorn.application.environment.ClassLoaderClasspathResourceLocator;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.DelegatingApplicationManager;
import org.dockbox.hartshorn.inject.InjectorMetaProvider;
import org.dockbox.hartshorn.proxy.HartshornApplicationProxier;
import org.dockbox.hartshorn.proxy.UseProxying;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.component.HierarchicalApplicationComponentProvider;
import org.dockbox.hartshorn.application.scan.ReflectionsPrefixContext;
import org.dockbox.hartshorn.component.ComponentLocatorImpl;
import org.dockbox.hartshorn.logging.CallerLookupApplicationLogger;

import java.lang.annotation.Annotation;

/**
 * The default implementation of the {@link HartshornApplicationFactory} interface. This implementation is responsible for
 * creating the {@link ApplicationContext} and the {@link ApplicationEnvironment} instances.
 *
 * <p>This implementation uses the default implementations of all required components by default when {@link #loadDefaults()}
 * is called.
 *
 * @author Guus Lieben
 * @since 21.9
 *
 * @see CallerLookupApplicationLogger
 * @see EnvironmentDrivenApplicationConfigurator
 * @see ApplicationFSProviderImpl
 * @see ContextualApplicationEnvironment
 * @see LoggingExceptionHandler
 * @see HartshornApplicationProxier
 * @see ComponentLocatorImpl
 * @see ClassLoaderClasspathResourceLocator
 * @see InjectorMetaProvider
 */
public class HartshornApplicationFactory extends AbstractActivatingApplicationFactory<HartshornApplicationFactory, StandardDelegatingApplicationContext, DelegatingApplicationManager> {

    public HartshornApplicationFactory self() {
        return this;
    }

    @Override
    public HartshornApplicationFactory loadDefaults() {
        return this.applicationLogger(new CallerLookupApplicationLogger())
                .applicationConfigurator(new EnvironmentDrivenApplicationConfigurator())
                .applicationProxier(new HartshornApplicationProxier())
                .applicationFSProvider(new ApplicationFSProviderImpl())
                .applicationEnvironment(ContextualApplicationEnvironment::new)
                .exceptionHandler(new LoggingExceptionHandler())
                .prefixContext(ReflectionsPrefixContext::new)
                .componentLocator(ComponentLocatorImpl::new)
                .resourceLocator(ClassLoaderClasspathResourceLocator::new)
                .metaProvider(InjectorMetaProvider::new)
                .componentProvider(HierarchicalApplicationComponentProvider::new)
                .componentPopulator(ContextualComponentPopulator::new)
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

    @Override
    protected Activator activatorAnnotation() {
        return this.activator.annotation(Activator.class).get();
    }

    @Override
    protected DelegatingApplicationManager createManager() {
        return new DelegatingApplicationManager(
                this.activator,
                this.applicationLogger,
                this.applicationProxier,
                this.applicationFSProvider,
                this.exceptionHandler
        );
    }

    @Override
    protected StandardDelegatingApplicationContext createContext(final ApplicationEnvironment environment) {
        return new StandardDelegatingApplicationContext(
                environment,
                this.componentLocator,
                this.resourceLocator,
                this.metaProvider,
                this.componentProvider,
                this.componentPopulator,
                this.activator,
                this.arguments,
                this.modifiers
        );
    }
}
