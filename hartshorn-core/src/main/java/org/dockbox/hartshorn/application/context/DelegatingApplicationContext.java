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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.ContextualEnvironmentBinderConfiguration;
import org.dockbox.hartshorn.application.DefaultBindingConfigurer;
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
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;

public abstract class DelegatingApplicationContext extends DefaultApplicationAwareContext implements
        ApplicationContext {

    private final transient Properties environmentValues;
    private final transient ComponentProvider componentProvider;
    private final transient ComponentLocator locator;
    private final transient ApplicationEnvironment environment;

    private boolean isClosed = false;
    protected boolean isRunning = false;

    protected DelegatingApplicationContext(ApplicationEnvironment environment, Configurer configurer) {
        super(null);
        this.environment = environment;

        if (environment instanceof ModifiableContextCarrier modifiableContextCarrier) {
            modifiableContextCarrier.applicationContext(this);
        }

        this.prepareInitialization();

        this.environmentValues = environment.rawArguments();
        this.locator = configurer.componentLocator.initialize(this);
        this.componentProvider = configurer.componentProvider.initialize(this.locator);

        EnvironmentBinderConfiguration configuration = new ContextualEnvironmentBinderConfiguration();
        configuration.configureBindings(environment, configurer.defaultBindings.initialize(this), this);
    }

    protected abstract void prepareInitialization();

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
    public ExceptionHandler stacktraces(boolean stacktraces) {
        return this.environment().stacktraces(stacktraces);
    }

    @Override
    public <T> T get(ComponentKey<T> key) {
        return this.componentProvider.get(key);
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
    public void setDebugActive(boolean active) {
        this.environment().setDebugActive(active);
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            throw new ContextClosedException(ApplicationContext.class);
        }
        ApplicationEnvironment environment = this.environment();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            Set<LifecycleObserver> observers = observable.observers(LifecycleObserver.class);
            this.log().info("Runtime shutting down, notifying {} observers", observers.size());
            for (LifecycleObserver observer : observers) {
                this.log().debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                try {
                    observer.onExit(this);
                }
                catch (Throwable e) {
                    this.log().error("Error notifying " + observer.getClass().getSimpleName() + " of shutdown", e);
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

    public ComponentLocator locator() {
        return this.locator;
    }

    public ComponentProvider componentProvider() {
        return this.componentProvider;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this;
    }

    protected static class Configurer {

        private ContextualInitializer<ApplicationContext, ? extends ComponentLocator> componentLocator = TypeReferenceLookupComponentLocator::new;
        private ContextualInitializer<ComponentLocator, ? extends ComponentProvider> componentProvider = ScopeAwareComponentProvider.create(Customizer.useDefaults());
        private ContextualInitializer<ApplicationContext, ? extends DefaultBindingConfigurer> defaultBindings = ContextualInitializer.of(DefaultBindingConfigurer::empty);

        public Configurer componentLocator(ComponentLocator componentLocator) {
            return this.componentLocator(ContextualInitializer.of(componentLocator));
        }

        public Configurer componentLocator(ContextualInitializer<ApplicationContext, ? extends ComponentLocator> componentLocator) {
            this.componentLocator = componentLocator;
            return this;
        }

        public Configurer componentProvider(ComponentProvider componentProvider) {
            return this.componentProvider(ContextualInitializer.of(componentProvider));
        }

        public Configurer componentProvider(ContextualInitializer<ComponentLocator, ? extends ComponentProvider> componentProvider) {
            this.componentProvider = componentProvider;
            return this;
        }

        public Configurer defaultBindings(DefaultBindingConfigurer defaultBindings) {
            return this.defaultBindings(ContextualInitializer.of(defaultBindings));
        }

        public Configurer defaultBindings(BiConsumer<ApplicationContext, Binder> defaultBindings) {
            return this.defaultBindings(applicationContext -> binder -> defaultBindings.accept(applicationContext, binder));
        }

        public Configurer defaultBindings(ContextualInitializer<ApplicationContext, ? extends DefaultBindingConfigurer> defaultBindings) {
            this.defaultBindings = defaultBindings;
            return this;
        }
    }
}
