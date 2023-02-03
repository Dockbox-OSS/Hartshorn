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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.IllegalModificationException;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeModuleContext;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.LazySingletonProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.SingletonProvider;
import org.dockbox.hartshorn.inject.SupplierProvider;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.option.Option;

public class HierarchyBindingFunction<T> implements BindingFunction<T> {

    private final BindingHierarchy<T> hierarchy;
    private final Binder binder;
    private final SingletonCache singletonCache;
    private final ComponentInstanceFactory instanceFactory;
    private final ScopeModuleContext moduleContext;
    private Scope scope;

    private Class<? extends Scope> scopeModule;
    private int priority = -1;

    public HierarchyBindingFunction(
            final BindingHierarchy<T> hierarchy,
            final Binder binder,
            final SingletonCache singletonCache,
            final ComponentInstanceFactory instanceFactory,
            final Scope scope,
            final ScopeModuleContext moduleContext) {
        this.hierarchy = hierarchy;
        this.binder = binder;
        this.singletonCache = singletonCache;

        this.instanceFactory = instanceFactory;
        this.scope = scope;
        this.moduleContext = moduleContext;
    }

    protected BindingHierarchy<T> hierarchy() {
        if (this.scopeModule != null) return this.moduleContext.hierarchy(this.scopeModule, this.hierarchy.key());
        else return this.hierarchy;
    }

    protected Binder binder() {
        return this.binder;
    }

    protected SingletonCache singletonCache() {
        return this.singletonCache;
    }

    protected ComponentInstanceFactory instanceFactory() {
        return this.instanceFactory;
    }

    @Override
    public BindingFunction<T> installTo(final Class<? extends Scope> scope) {
        if (this.scope != null && this.scope != Scope.DEFAULT_SCOPE && (!(this.scope instanceof ApplicationContext))) {
            throw new IllegalModificationException("Cannot install binding to scope " + scope.getName() + " as the binding is already installed to scope " + this.scope.getClass().getName());
        }
        // Permitted, as default application scope may be expanded. Defined child scopes can not be expanded, so this is a safe check
        if (this.scope instanceof ApplicationContext && !ApplicationContext.class.isAssignableFrom(scope)) {
            this.scope = null;
        }
        this.scopeModule = scope;
        return this;
    }

    @Override
    public BindingFunction<T> priority(final int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public Binder to(final Class<? extends T> type) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException("Cannot overwrite singleton binding for %s in a hierarchy, ensure the new binding is a singleton".formatted(this.hierarchy().key()));
        }
        final ComponentKey<? extends T> key = ComponentKey.builder(type).scope(this.scope).build();
        return this.add(new ContextDrivenProvider<>(key));
    }

    @Override
    public Binder to(final CheckedSupplier<T> supplier) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException("Cannot overwrite singleton binding for %s in a hierarchy, ensure the new binding is a singleton".formatted(this.hierarchy().key()));
        }
        return this.add(new SupplierProvider<>(supplier));
    }

    @Override
    public Binder to(final Provider<T> provider) {
        return this.add(provider);
    }

    @Override
    public Binder singleton(final T t) {
        // Set 'processed' to false to ensure that the singleton is processed the first time it is requested. As the object
        // container is reused, this will only happen once.
        return this.add(new SingletonProvider<>(t, false));
    }

    @Override
    public Binder lazySingleton(final Class<T> type) {
        this.lazyContainerSingleton(() -> {
            final ComponentKey<T> key = ComponentKey.builder(type).scope(this.scope).build();
            final Option<ObjectContainer<T>> object = this.instanceFactory().instantiate(key);
            return object.orNull();
        });
        return this.binder();
    }

    @Override
    public Binder lazySingleton(final CheckedSupplier<T> supplier) {
        this.lazyContainerSingleton(() -> {
            final T instance = supplier.get();
            return new ObjectContainer<>(instance, false);
        });
        return this.binder();
    }

    public Binder lazyContainerSingleton(final CheckedSupplier<ObjectContainer<T>> supplier) {
        return this.add(new LazySingletonProvider<>(supplier));
    }

    private Binder add(final Provider<T> provider) {
        this.hierarchy().add(this.priority, provider);
        return this.binder();
    }
}
