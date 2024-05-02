/*
 * Copyright 2019-2024 the original author or authors.
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
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.environment.FileSystemProvider;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentRegistry;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.SingletonCacheComponentProvider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.SingletonCache;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

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
 *     <li>{@link ComponentRegistry}, if the {@link ApplicationContext application context} is an instance of {@link DelegatingApplicationContext}</li>
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
public class ContextualEnvironmentBinderConfiguration implements EnvironmentBinderConfiguration {

    @Override
    public void configureBindings(ApplicationEnvironment environment, DefaultBindingConfigurer configurer, Binder binder) {
        // Application context
        binder.bind(ComponentProvider.class).singleton(environment.applicationContext());
        binder.bind(ExceptionHandler.class).singleton(environment.applicationContext());
        binder.bind(ApplicationContext.class).singleton(environment.applicationContext());
        binder.bind(ApplicationPropertyHolder.class).singleton(environment.applicationContext());

        if (environment.applicationContext() instanceof DelegatingApplicationContext delegatingApplicationContext) {
            binder.bind(ComponentRegistry.class)
                    .processAfterInitialization(false)
                    .singleton(delegatingApplicationContext.componentRegistry());

            ComponentProvider componentProvider = delegatingApplicationContext.componentProvider();
            binder.bind(Scope.class)
                    .processAfterInitialization(false)
                    .singleton(componentProvider.scope());

            if (componentProvider instanceof ScopeAwareComponentProvider scopeAwareComponentProvider) {
                HierarchicalComponentProvider applicationProvider = scopeAwareComponentProvider.applicationProvider();

                if (applicationProvider instanceof SingletonCacheComponentProvider singletonCacheComponentProvider) {
                    binder.bind(SingletonCache.class)
                            .processAfterInitialization(false)
                            .lazySingleton(singletonCacheComponentProvider::singletonCache);
                }
            }
        }

        // Application environment
        binder.bind(Introspector.class).singleton(environment.introspector());
        binder.bind(ApplicationEnvironment.class).singleton(environment);
        binder.bind(ProxyLookup.class).singleton(environment.proxyOrchestrator());
        binder.bind(ProxyOrchestrator.class).singleton(environment.proxyOrchestrator());
        binder.bind(FileSystemProvider.class).singleton(environment.fileSystem());
        binder.bind(AnnotationLookup.class).singleton(environment.introspector().annotations());
        binder.bind(ClasspathResourceLocator.class).singleton(environment.classpath());

        if (environment instanceof ObservableApplicationEnvironment observableEnvironment) {
            binder.bind(LifecycleObservable.class).singleton(observableEnvironment);
        }

        // Common bindings
        binder.bind(Binder.class).singleton(binder);

        // Custom default bindings. Runs last to allow for modification of default bindings.
        configurer.configure(binder);
    }
}
