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

package org.dockbox.hartshorn.application.context;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;

import org.dockbox.hartshorn.application.ContextualEnvironmentBinderConfiguration;
import org.dockbox.hartshorn.application.DefaultBindingConfigurer;
import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.EnvironmentBinderConfiguration;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.ServiceActivatorContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.TypeReferenceLookupComponentLocator;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ApplicationContext} implementation that delegates to a {@link ComponentLocator} and {@link ComponentProvider}.
 * This implementation is used to allow for custom implementations of these interfaces, while still allowing for the
 * {@link ApplicationContext} to function in a predictable manner.
 *
 * <p>Details like component processors and context initialization are not handled as they are specific to the
 * {@link ApplicationContext} implementation. This implementation is intended to be used as a base class for
 * {@link ApplicationContext} implementations.
 *
 * <p>While the lifecycle during startup is not explicitly managed by this implementation, it does provide handling for
 * {@link LifecycleObserver#onExit(ApplicationContext)}. This allows for the {@link ApplicationContext} to be closed
 * in a predictable manner.
 *
 * <p>Bindings are configured using a {@link ContextualEnvironmentBinderConfiguration}. This configuration is used to
 * bind all components that are provided by- and delegated to the {@link ApplicationContext}- and
 * {@link ApplicationEnvironment} instances. Additional bindings can be added by providing a
 * {@link DefaultBindingConfigurer} to the {@link DelegatingApplicationContext.Configurer}.
 *
 * @author Guus Lieben
 *
 * @see ApplicationContext
 * @see ComponentLocator
 * @see ComponentProvider
 * @see ApplicationEnvironment
 * @see DelegatingApplicationContext.Configurer
 * @see ContextualEnvironmentBinderConfiguration
 *
 * @since 0.5.0
 */
public abstract class DelegatingApplicationContext extends DefaultApplicationAwareContext implements
        ApplicationContext {

    private static final Logger LOG = LoggerFactory.getLogger(DelegatingApplicationContext.class);

    private final transient Properties environmentValues;
    private final transient ComponentProvider componentProvider;
    private final transient ComponentLocator locator;
    private final transient ApplicationEnvironment environment;

    private boolean isClosed = false;
    protected boolean isRunning = false;

    protected DelegatingApplicationContext(SingleElementContext<? extends ApplicationEnvironment> initializerContext, Configurer configurer) {
        super(null);
        this.environment = initializerContext.input();

        if (this.environment instanceof ModifiableContextCarrier modifiableContextCarrier) {
            modifiableContextCarrier.applicationContext(this);
        }

        this.prepareInitialization();

        this.environmentValues = this.environment.rawArguments();

        SingleElementContext<ApplicationContext> applicationInitializerContext = initializerContext.transform(this);
        this.locator = configurer.componentLocator.initialize(applicationInitializerContext);
        this.componentProvider = configurer.componentProvider.initialize(initializerContext.transform(this.locator));

        EnvironmentBinderConfiguration configuration = new ContextualEnvironmentBinderConfiguration();

        DefaultBindingConfigurer bindingConfigurer = configurer.defaultBindings.initialize(applicationInitializerContext);
        for (DefaultBindingConfigurerContext configurerContext : initializerContext.all(DefaultBindingConfigurerContext.class)) {
            bindingConfigurer = bindingConfigurer.compose(configurerContext.configurer());
        }
        configuration.configureBindings(this.environment, bindingConfigurer, this);
    }

    /**
     * Prepares the initialization of the {@link ApplicationContext}. This method is called before any bindings are
     * configured. This method is intended to be overridden by implementations to perform any initialization that is
     * required before bindings are configured.
     */
    protected abstract void prepareInitialization();

    /**
     * Checks if the {@link ApplicationContext} is running. If it is, an {@link IllegalModificationException} is thrown.
     * This method is intended to be called by implementations to prevent modifications to the {@link ApplicationContext}
     * after it has been started.
     */
    protected void checkRunning() {
        if (this.isRunning) {
            throw new IllegalModificationException("Application context cannot be modified after it has been started");
        }
    }

    @Override
    public Properties properties() {
        return this.environmentValues;
    }

    @Override
    public Option<String> property(String key) {
        return Option.of(this.environmentValues.get(key)).map(String::valueOf);
    }

    @Override
    public Set<Annotation> activators() {
        return this.first(ServiceActivatorContext.class)
                .map(ServiceActivatorContext::activators)
                .orElseGet(Set::of);
    }

    @Override
    public <A> Option<A> activator(Class<A> activator) {
        return this.first(ServiceActivatorContext.class)
                .map(context -> context.activator(activator));
    }

    @Override
    public boolean hasActivator(Class<? extends Annotation> activator) {
        return this.first(ServiceActivatorContext.class)
                .map(context -> context.hasActivator(activator))
                .orElseGet(() -> false);
    }

    @Override
    public void handle(Throwable throwable) {
        this.environment().handle(throwable);
    }

    @Override
    public void handle(String message, Throwable throwable) {
        this.environment().handle(message, throwable);
    }

    @Override
    public ExceptionHandler printStackTraces(boolean stacktraces) {
        return this.environment().printStackTraces(stacktraces);
    }

    @Override
    public <T> T get(ComponentKey<T> key, ComponentRequestContext requestContext) {
        return this.componentProvider.get(key, requestContext);
    }

    @Override
    public <C> BindingFunction<C> bind(ComponentKey<C> key) {
        if (this.componentProvider instanceof Binder binder) {
            BindingFunction<C> function = binder.bind(key);
            return new DelegatingApplicationBindingFunction<>(this, function);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public <C> Binder bind(BindingHierarchy<C> hierarchy) {
        if (this.componentProvider instanceof Binder binder) {
            return binder.bind(hierarchy);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.environment;
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key) {
        if (this.componentProvider instanceof HierarchicalComponentProvider provider) {
            return provider.hierarchy(key);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public MultiMap<Scope, BindingHierarchy<?>> hierarchies() {
        if (this.componentProvider instanceof HierarchicalComponentProvider provider) {
            return provider.hierarchies();
        }
        return new ArrayListMultiMap<>();
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            throw new ContextClosedException(ApplicationContext.class);
        }
        ApplicationEnvironment environment = this.environment();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            Set<LifecycleObserver> observers = observable.observers(LifecycleObserver.class);
            LOG.info("Runtime shutting down, notifying {} observers", observers.size());
            for (LifecycleObserver observer : observers) {
                LOG.debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                try {
                    observer.onExit(this);
                }
                catch (Throwable e) {
                    handle("Error notifying " + observer.getClass().getSimpleName() + " of shutdown", e);
                }
            }
            this.isClosed = true;
            this.isRunning = false;
        }
    }

    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

    /**
     * @return the {@link ComponentLocator} that is used by this {@link ApplicationContext} to locate components
     */
    public ComponentLocator locator() {
        return this.locator;
    }

    /**
     * @return the {@link ComponentProvider} that is used by this {@link ApplicationContext} to provide components
     */
    public ComponentProvider componentProvider() {
        return this.componentProvider;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this;
    }

    /**
     * Configuration for the {@link DelegatingApplicationContext}. This configuration is used to configure the
     * various components required by the {@link DelegatingApplicationContext} to function.
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private ContextualInitializer<ApplicationContext, ? extends ComponentLocator> componentLocator = ContextualInitializer.of(TypeReferenceLookupComponentLocator::new);
        private ContextualInitializer<ComponentLocator, ? extends ComponentProvider> componentProvider = ScopeAwareComponentProvider.create(Customizer.useDefaults());
        private ContextualInitializer<ApplicationContext, ? extends DefaultBindingConfigurer> defaultBindings = ContextualInitializer.of(DefaultBindingConfigurer::empty);

        /**
         * Configures the {@link ComponentLocator} that is used by the {@link DelegatingApplicationContext} to locate
         * components.
         *
         * @param componentLocator the {@link ComponentLocator} to use
         * @return the current instance
         */
        public Configurer componentLocator(ComponentLocator componentLocator) {
            return this.componentLocator(ContextualInitializer.of(componentLocator));
        }

        /**
         * Configures the {@link ComponentLocator} that is used by the {@link DelegatingApplicationContext} to locate
         * components.
         *
         * @param componentLocator the {@link ComponentLocator} to use
         * @return the current instance
         */
        public Configurer componentLocator(ContextualInitializer<ApplicationContext, ? extends ComponentLocator> componentLocator) {
            this.componentLocator = componentLocator;
            return this;
        }

        /**
         * Configures the {@link ComponentProvider} that is used by the {@link DelegatingApplicationContext} to provide
         * component instances.
         *
         * @param componentProvider the {@link ComponentProvider} to use
         * @return the current instance
         */
        public Configurer componentProvider(ComponentProvider componentProvider) {
            return this.componentProvider(ContextualInitializer.of(componentProvider));
        }

        /**
         * Configures the {@link ComponentProvider} that is used by the {@link DelegatingApplicationContext} to provide
         * component instances.
         *
         * @param componentProvider the {@link ComponentProvider} to use
         * @return the current instance
         */
        public Configurer componentProvider(ContextualInitializer<ComponentLocator, ? extends ComponentProvider> componentProvider) {
            this.componentProvider = componentProvider;
            return this;
        }

        /**
         * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
         * configure bindings that should be available by default.
         *
         * @param defaultBindings the {@link DefaultBindingConfigurer} to use
         * @return the current instance
         */
        public Configurer defaultBindings(DefaultBindingConfigurer defaultBindings) {
            return this.defaultBindings(ContextualInitializer.of(defaultBindings));
        }

        /**
         * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
         * configure bindings that should be available by default.
         *
         * @param defaultBindings the {@link DefaultBindingConfigurer} to use
         * @return the current instance
         */
        public Configurer defaultBindings(BiConsumer<ApplicationContext, Binder> defaultBindings) {
            return this.defaultBindings(context -> binder -> defaultBindings.accept(context.input(), binder));
        }

        /**
         * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
         * configure bindings that should be available by default.
         *
         * @param defaultBindings the {@link DefaultBindingConfigurer} to use
         * @return the current instance
         */
        public Configurer defaultBindings(ContextualInitializer<ApplicationContext, ? extends DefaultBindingConfigurer> defaultBindings) {
            this.defaultBindings = defaultBindings;
            return this;
        }
    }
}
