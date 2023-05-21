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

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.ServiceActivatorContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.Set;

public abstract class DelegatingApplicationContext extends DefaultApplicationAwareContext implements
        ApplicationContext {

    private final transient Properties environmentValues;
    private final transient ComponentProvider componentProvider;
    private final transient ComponentLocator locator;

    private boolean isClosed = false;
    protected boolean isRunning = false;

    protected DelegatingApplicationContext(InitializingContext context) {
        super(null);
        context = new InitializingContext(context.environment(), this, context.builder());
        this.add(context);

        if (context.environment() instanceof ModifiableContextCarrier modifiable) {
            modifiable.applicationContext(this);
        }

        this.prepareInitialization();

        this.environmentValues = context.argumentParser().parse(context.builder().arguments());
        this.componentProvider = context.componentProvider();
        this.locator = context.componentLocator();

        context.applyTo(this);
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
    public Option<String> property(final String key) {
        return Option.of(this.environmentValues.get(key)).map(String::valueOf);
    }

    @Override
    public Set<Annotation> activators() {
        return this.first(ServiceActivatorContext.class)
                .map(ServiceActivatorContext::activators)
                .orElseGet(Set::of);
    }

    @Override
    public <A> Option<A> activator(final Class<A> activator) {
        return this.first(ServiceActivatorContext.class)
                .map(context -> context.activator(activator));
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        return this.first(ServiceActivatorContext.class)
                .map(context -> context.hasActivator(activator))
                .orElseGet(() -> false);
    }

    @Override
    public void handle(final Throwable throwable) {
        this.environment().handle(throwable);
    }

    @Override
    public void handle(final String message, final Throwable throwable) {
        this.environment().handle(message, throwable);
    }

    @Override
    public ExceptionHandler stacktraces(final boolean stacktraces) {
        return this.environment().stacktraces(stacktraces);
    }

    @Override
    public <T> T get(final ComponentKey<T> key) {
        return this.componentProvider.get(key);
    }

    @Override
    public <C> BindingFunction<C> bind(final ComponentKey<C> key) {
        if (this.componentProvider instanceof Binder binder) {
            final BindingFunction<C> function = binder.bind(key);
            return new DelegatingApplicationBindingFunction<>(this, function);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public <C> Binder bind(final BindingHierarchy<C> hierarchy) {
        if (this.componentProvider instanceof Binder binder) {
            return binder.bind(hierarchy);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.first(InitializingContext.class)
                .map(InitializingContext::environment)
                .orNull();
    }

    @Override
    public <T> BindingHierarchy<T> hierarchy(final ComponentKey<T> key) {
        if (this.componentProvider instanceof HierarchicalComponentProvider provider) {
            return provider.hierarchy(key);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public void setDebugActive(final boolean active) {
        this.environment().setDebugActive(active);
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            throw new ContextClosedException(ApplicationContext.class);
        }
        final ApplicationEnvironment environment = this.environment();
        if (environment instanceof ObservableApplicationEnvironment observable) {
            final Set<LifecycleObserver> observers = observable.observers(LifecycleObserver.class);
            this.log().info("Runtime shutting down, notifying {} observers", observers.size());
            for (final LifecycleObserver observer : observers) {
                this.log().debug("Notifying " + observer.getClass().getSimpleName() + " of shutdown");
                try {
                    observer.onExit(this);
                }
                catch (final Throwable e) {
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
}
