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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.InjectorMetaProvider;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.activate.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.activate.UseProxying;
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.ContextualComponentPopulator;
import org.dockbox.hartshorn.core.context.StandardDelegatingApplicationContext;
import org.dockbox.hartshorn.core.context.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.core.context.HierarchicalApplicationComponentProvider;
import org.dockbox.hartshorn.core.context.ReflectionsPrefixContext;
import org.dockbox.hartshorn.core.services.ComponentLocatorImpl;

import java.lang.annotation.Annotation;

import lombok.Getter;

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
 * @see JavassistApplicationProxier
 * @see ApplicationFSProviderImpl
 * @see ContextualApplicationEnvironment
 * @see LoggingExceptionHandler
 * @see ComponentLocatorImpl
 * @see ClassLoaderClasspathResourceLocator
 * @see InjectorMetaProvider
 */
public class HartshornApplicationFactory extends AbstractActivatingApplicationFactory<HartshornApplicationFactory, StandardDelegatingApplicationContext, DelegatingApplicationManager> {

    @Getter
    private final HartshornApplicationFactory self = this;

    @Override
    public HartshornApplicationFactory loadDefaults() {
        return this.applicationLogger(new CallerLookupApplicationLogger())
                .applicationConfigurator(new EnvironmentDrivenApplicationConfigurator())
                .applicationProxier(new JavassistApplicationProxier())
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
