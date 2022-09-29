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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.ActivatorHolder;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.InitializingContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObserver;
import org.dockbox.hartshorn.application.lifecycle.ObservableApplicationManager;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.StandardComponentProvider;
import org.dockbox.hartshorn.context.DefaultApplicationAwareContext;
import org.dockbox.hartshorn.context.ModifiableContextCarrier;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.ProviderContext;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.Result;

import java.lang.annotation.Annotation;
import java.util.Properties;
import java.util.Set;

public abstract class DelegatingApplicationContext extends DefaultApplicationAwareContext implements
        ApplicationContext, HierarchicalComponentProvider {

    private final transient Properties environmentValues;
    private final transient ComponentProvider componentProvider;
    private final transient ActivatorHolder activatorHolder;
    private final transient ComponentLocator locator;

    private boolean isClosed = false;
    protected boolean isRunning = false;

    public DelegatingApplicationContext(InitializingContext context) {
        super(null);
        context = new InitializingContext(context.environment(), this, context.manager(), context.builder());
        this.add(context);

        if (context.manager() instanceof ModifiableContextCarrier modifiable) {
            modifiable.applicationContext(this);
        }

        this.prepareInitialization();

        this.activatorHolder = context.activatorHolder();
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
    public Result<String> property(final String key) {
        return Result.of(this.environmentValues.get(key)).map(String::valueOf);
    }

    @Override
    public Set<Annotation> activators() {
        return this.activatorHolder.activators();
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        return this.activatorHolder.activator(activator);
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        return this.activatorHolder.hasActivator(activator);
    }

    @Override
    public <T> void add(final ProviderContext<T> context) {
        final Key<T> key = context.key();
        final BindingFunction<T> function = this.bind(key);

        if (context.singleton()) {
            if (context.lazy()) {
                function.lazySingleton(context.provider());
            }
            else {
                function.singleton(context.provider().get());
            }
        }
        else {
            function.to(context.provider());
        }
    }

    @Override
    public void handle(final Throwable throwable) {
        this.environment().manager().handle(throwable);
    }

    @Override
    public void handle(final String message, final Throwable throwable) {
        this.environment().manager().handle(message, throwable);
    }

    @Override
    public ExceptionHandler stacktraces(final boolean stacktraces) {
        return this.environment().manager().stacktraces(stacktraces);
    }

    @Override
    public <T> T get(final Key<T> key) {
        return this.componentProvider.get(key);
    }

    @Override
    public <T> T get(final Key<T> key, final boolean enable) {
        return this.componentProvider.get(key, enable);
    }

    @Override
    public <C> BindingFunction<C> bind(final Key<C> key) {
        if (this.componentProvider instanceof StandardComponentProvider provider) {
            final BindingFunction<C> function = provider.bind(key);
            return new DelegatingApplicationBindingFunction<>(this, function);
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
    public <T> BindingHierarchy<T> hierarchy(final Key<T> key) {
        if (this.componentProvider instanceof StandardComponentProvider provider) {
            return provider.hierarchy(key);
        }
        throw new UnsupportedOperationException("This application does not support binding hierarchies");
    }

    @Override
    public void setDebugActive(final boolean active) {
        this.environment().manager().setDebugActive(active);
    }

    @Override
    public void close() {
        if (this.isClosed()) {
            throw new ContextClosedException(ApplicationContext.class);
        }
        this.log().info("Runtime shutting down, notifying observers");
        final ApplicationManager manager = this.environment().manager();
        if (manager instanceof ObservableApplicationManager observable) {
            for (final LifecycleObserver observer : observable.observers(LifecycleObserver.class)) {
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

    public ActivatorHolder activatorHolder() {
        return this.activatorHolder;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this;
    }
}
