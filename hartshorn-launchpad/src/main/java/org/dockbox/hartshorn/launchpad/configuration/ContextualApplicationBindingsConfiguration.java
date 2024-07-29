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

package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.provider.DelegatingScopeAwareComponentProvider;
import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.provider.SingletonCacheComponentProvider;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.DefaultBindingConfigurer;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.HierarchicalComponentProvider;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.DelegatingApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.launchpad.environment.FileSystemProvider;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.launchpad.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

/**
 * The default {@link ApplicationBindingsConfiguration} used by the {@link DelegatingApplicationContext}. This configuration
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
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ContextualApplicationBindingsConfiguration implements ApplicationBindingsConfiguration {

    @Override
    public void configureBindings(InjectionCapableApplication application, DefaultBindingConfigurer configurer, Binder binder) {
        // Application environment
        binder.bind(InjectorEnvironment.class).singleton(application.environment());
        if (application.environment() instanceof ApplicationEnvironment applicationEnvironment) {
            binder.bind(ApplicationEnvironment.class).singleton(applicationEnvironment);
            binder.bind(FileSystemProvider.class).singleton(applicationEnvironment.fileSystem());
            binder.bind(ClasspathResourceLocator.class).singleton(applicationEnvironment.classpath());
            binder.bind(ComponentRegistry.class)
                    .processAfterInitialization(false)
                    .singleton(applicationEnvironment.componentRegistry());
        }
        binder.bind(Introspector.class).singleton(application.environment().introspector());
        binder.bind(AnnotationLookup.class).singleton(application.environment().introspector().annotations());
        binder.bind(ProxyLookup.class).singleton(application.environment().proxyOrchestrator());
        binder.bind(ProxyOrchestrator.class).singleton(application.environment().proxyOrchestrator());

        if (application instanceof ObservableApplicationEnvironment observableEnvironment) {
            binder.bind(LifecycleObservable.class).singleton(observableEnvironment);
        }

        // Application context
        binder.bind(InjectionCapableApplication.class).singleton(application);
        if (application instanceof ApplicationContext applicationContext) {
            binder.bind(ApplicationContext.class).singleton(applicationContext);
            binder.bind(ExceptionHandler.class).singleton(applicationContext);
        }
        binder.bind(ComponentProvider.class).singleton(application.defaultProvider());

        if (application instanceof DelegatingApplicationContext delegatingApplicationContext) {
            ComponentProvider componentProvider = delegatingApplicationContext.componentProvider();
            binder.bind(Scope.class)
                    .processAfterInitialization(false)
                    .singleton(componentProvider.scope());

            if (componentProvider instanceof DelegatingScopeAwareComponentProvider scopeAwareComponentProvider) {
                HierarchicalComponentProvider applicationProvider = scopeAwareComponentProvider.applicationProvider();

                if (applicationProvider instanceof SingletonCacheComponentProvider singletonCacheComponentProvider) {
                    binder.bind(SingletonCache.class)
                            .processAfterInitialization(false)
                            .lazySingleton(singletonCacheComponentProvider::singletonCache);
                }
            }
        }

        // Common bindings
        binder.bind(Binder.class).singleton(binder);

        // Custom default bindings. Runs last to allow for modification of default bindings.
        configurer.configure(binder);
    }
}
