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
 * @since 4.2.4
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
