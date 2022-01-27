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
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.HartshornApplicationContext;
import org.dockbox.hartshorn.core.context.HartshornApplicationEnvironment;
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
 * @see HartshornApplicationLogger
 * @see HartshornApplicationConfigurator
 * @see JavassistApplicationProxier
 * @see HartshornApplicationFSProvider
 * @see HartshornApplicationEnvironment
 * @see HartshornExceptionHandler
 * @see ComponentLocatorImpl
 * @see HartshornClasspathResourceLocator
 * @see InjectorMetaProvider
 */
public class HartshornApplicationFactory extends AbstractActivatingApplicationFactory<HartshornApplicationFactory, HartshornApplicationContext, HartshornApplicationManager> {

    @Getter
    private final HartshornApplicationFactory self = this;

    @Override
    public HartshornApplicationFactory loadDefaults() {
        return this.applicationLogger(new HartshornApplicationLogger())
                .applicationConfigurator(new HartshornApplicationConfigurator())
                .applicationProxier(new JavassistApplicationProxier())
                .applicationFSProvider(new HartshornApplicationFSProvider())
                .applicationEnvironment(HartshornApplicationEnvironment::new)
                .exceptionHandler(new HartshornExceptionHandler())
                .prefixContext(ReflectionsPrefixContext::new)
                .componentLocator(ComponentLocatorImpl::new)
                .resourceLocator(HartshornClasspathResourceLocator::new)
                .metaProvider(InjectorMetaProvider::new)
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

    @Override
    protected Activator activatorAnnotation() {
        return this.activator.annotation(Activator.class).get();
    }

    @Override
    protected HartshornApplicationManager createManager() {
        return new HartshornApplicationManager(
                this.activator,
                this.applicationLogger,
                this.applicationProxier,
                this.applicationFSProvider,
                this.exceptionHandler
        );
    }

    @Override
    protected HartshornApplicationContext createContext(final ApplicationEnvironment environment) {
        return new HartshornApplicationContext(
                environment,
                this.componentLocator,
                this.resourceLocator,
                this.metaProvider,
                this.activator,
                this.arguments,
                this.modifiers
        );
    }
}
