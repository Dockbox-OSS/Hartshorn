/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.application.context.DelegatingApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.slf4j.Logger;

/**
 * The default {@link EnvironmentBinderConfiguration} used by the {@link DelegatingApplicationContext}. This configuration
 * binds all components that are provided by- and delegated to the {@link ApplicationContext}- and {@link ApplicationEnvironment}
 * instances.
 *
 * <p>Additional bindings can be added by providing a {@link DefaultBindingConfigurer} to the {@link DelegatingApplicationContext.Configurer}.
 * These additional bindings are processed after the default bindings, and can be used to override default bindings. This is
 * useful when the default bindings are not sufficient, or when the default bindings are not desired.
 *
 * <p>Bindings for specific implementations will optionally be registered for the following types:
 * <ul>
 *     <li>{@link LifecycleObservable}, if the {@link ApplicationEnvironment environment} is an instance of {@link ObservableApplicationEnvironment}</li>
 *     <li>{@link ComponentLocator}, if the {@link ApplicationContext application context} is an instance of {@link DelegatingApplicationContext}</li>
 * </ul>
 *
 * @see DefaultBindingConfigurer
 * @see DelegatingApplicationContext.Configurer#defaultBindings(DefaultBindingConfigurer)
 * @see ObservableApplicationEnvironment
 * @see DelegatingApplicationContext
 *
 * @author Guus Lieben
 * @since 0.5.0
 */
@LogExclude
public class ContextualEnvironmentBinderConfiguration implements EnvironmentBinderConfiguration {

    @Override
    public void configureBindings(ApplicationEnvironment environment, DefaultBindingConfigurer configurer, Binder binder) {
        // Application context
        binder.bind(ComponentProvider.class).singleton(environment.applicationContext());
        binder.bind(ExceptionHandler.class).singleton(environment.applicationContext());
        binder.bind(ApplicationContext.class).singleton(environment.applicationContext());
        binder.bind(ApplicationPropertyHolder.class).singleton(environment.applicationContext());

        if (environment.applicationContext() instanceof DelegatingApplicationContext delegatingApplicationContext) {
            binder.bind(ComponentLocator.class)
                    .processAfterInitialization(false)
                    .singleton(delegatingApplicationContext.locator());
        }

        // Application environment
        binder.bind(Introspector.class).singleton(environment);
        binder.bind(ApplicationEnvironment.class).singleton(environment);
        binder.bind(ProxyLookup.class).singleton(environment);
        binder.bind(ApplicationLogger.class).singleton(environment);
        binder.bind(ApplicationProxier.class).singleton(environment);
        binder.bind(ApplicationFSProvider.class).singleton(environment);
        binder.bind(AnnotationLookup.class).singleton(environment);
        binder.bind(ClasspathResourceLocator.class).singleton(environment);

        if (environment instanceof ObservableApplicationEnvironment observableEnvironment) {
            binder.bind(LifecycleObservable.class).singleton(observableEnvironment);
        }

        // Dynamic components
        binder.bind(Logger.class).to(environment.applicationContext()::log);

        // Custom default bindings. Runs last to allow for modification of default bindings.
        configurer.configure(binder);
    }
}
