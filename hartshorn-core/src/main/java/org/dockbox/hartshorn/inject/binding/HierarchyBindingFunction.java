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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.Scope;
import org.dockbox.hartshorn.component.ScopeKey;
import org.dockbox.hartshorn.component.ScopeModuleContext;
import org.dockbox.hartshorn.inject.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.ContextAwareComponentSupplier;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.LazySingletonProvider;
import org.dockbox.hartshorn.inject.ObjectContainer;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.SingletonProvider;
import org.dockbox.hartshorn.inject.SupplierProvider;
import org.dockbox.hartshorn.inject.binding.collection.CollectionBindingHierarchy;
import org.dockbox.hartshorn.inject.binding.collection.CollectorBindingFunction;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.binding.collection.HierarchyCollectorBindingFunction;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link BindingFunction} that configures a {@link BindingHierarchy} for a specific key. The hierarchy is
 * provided by the owning {@link Binder}.
 *
 * @param <T> The type of the component that is bound.
 *
 * @author Guus Lieben
 * @since 0.4.1
 *
 * @see Binder
 * @see BindingHierarchy
 */
public class HierarchyBindingFunction<T> implements BindingFunction<T> {

    private final BindingHierarchy<T> hierarchy;
    private final HierarchicalBinder binder;
    private final SingletonCache singletonCache;
    private final ComponentInstanceFactory instanceFactory;
    private final ScopeModuleContext moduleContext;
    private Scope scope;

    private ScopeKey scopeModule;
    private int priority = -1;
    private boolean processAfterInitialization = true;

    public HierarchyBindingFunction(
            BindingHierarchy<T> hierarchy,
            HierarchicalBinder binder,
            SingletonCache singletonCache,
            ComponentInstanceFactory instanceFactory,
            Scope scope,
            ScopeModuleContext moduleContext) {
        this.hierarchy = hierarchy;
        this.binder = binder;
        this.singletonCache = singletonCache;

        this.instanceFactory = instanceFactory;
        this.scope = scope;
        this.moduleContext = moduleContext;
    }

    protected BindingHierarchy<T> hierarchy() {
        if (this.scopeModule != null) {
            return this.moduleContext.hierarchy(this.scopeModule, this.hierarchy.key());
        }
        else {
            return this.hierarchy;
        }
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
    public BindingFunction<T> installTo(ScopeKey scope) throws IllegalScopeException {
        if (this.scope != null && this.scope != Scope.DEFAULT_SCOPE && (!(this.scope instanceof ApplicationContext))) {
            throw new IllegalScopeException("Cannot install binding to scope " + scope.name() + " as the binding is already installed to scope " + this.scope.installableScopeType().name());
        }
        // Permitted, as default application scope may be expanded. Defined child scopes can not be expanded, so this is a safe check
        if (this.scope instanceof ApplicationContext && !ApplicationContext.class.isAssignableFrom(scope.scopeType().type())) {
            this.scope = null;
        }
        this.scopeModule = scope;
        return this;
    }

    @Override
    public BindingFunction<T> priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public BindingFunction<T> processAfterInitialization(boolean processAfterInitialization) {
        this.processAfterInitialization = processAfterInitialization;
        return this;
    }

    @Override
    public Binder to(Class<? extends T> type) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException("Cannot overwrite singleton binding for %s in a hierarchy, ensure the new binding is a singleton".formatted(this.hierarchy().key()));
        }
        ComponentKey<? extends T> key = this.buildComponentKey(type);
        return this.add(new ContextDrivenProvider<>(key));
    }

    @NonNull
    private ComponentKey<? extends T> buildComponentKey(Class<? extends T> type) {
        return ComponentKey.builder(type).scope(this.scope).build();
    }

    @Override
    public Binder to(CheckedSupplier<T> supplier) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException("Cannot overwrite singleton binding for %s in a hierarchy, ensure the new binding is a singleton".formatted(this.hierarchy().key()));
        }
        return this.add(new SupplierProvider<>(supplier));
    }

    @Override
    public Binder to(Provider<T> provider) {
        return this.add(provider);
    }

    @Override
    public Binder singleton(T instance) {
        if (instance == null) {
            throw new IllegalModificationException("Cannot bind null instance");
        }
        if (this.processAfterInitialization) {
            return this.add(new SingletonProvider<>(instance));
        }
        else {
            // If no processing should happen, then we can immediately cache the instance
            this.singletonCache.put(this.hierarchy.key(), instance);
            return this.binder();
        }
    }

    @Override
    public Binder lazySingleton(Class<T> type) {
        return this.lazyContainerSingleton(() -> {
            ComponentKey<T> key = ComponentKey.builder(type).scope(this.scope).build();
            Option<ObjectContainer<T>> object = this.instanceFactory().instantiate(key, ComponentRequestContext.createForComponent());
            return object.orNull();
        });
    }

    @Override
    public Binder lazySingleton(CheckedSupplier<T> supplier) {
        return this.lazyContainerSingleton(() -> {
            T instance = supplier.get();
            if (instance == null) {
                throw new IllegalModificationException("Cannot bind null instance");
            }
            return new ComponentObjectContainer<>(instance);
        });
    }

    @Override
    public Binder collect(Customizer<CollectorBindingFunction<T>> collector) {
        BindingHierarchy<T> existingHierarchy = this.hierarchy();
        ComponentKey<ComponentCollection<T>> collectionComponentKey = this.createCollectionComponentKey();

        BindingHierarchy<ComponentCollection<T>> existingCollectionHierarchy = this.binder.hierarchy(collectionComponentKey);
        if (existingCollectionHierarchy instanceof CollectionBindingHierarchy<T> collectionBindingHierarchy) {
            Binder updatedBinder = this.binder.bind(collectionBindingHierarchy);
            CollectorBindingFunction<T> function = new HierarchyCollectorBindingFunction<>(updatedBinder, collectionBindingHierarchy, this.priority);
            collector.configure(function);
            return updatedBinder;
        }
        else {
            throw new IllegalStateException("Cannot create collector binding function for hierarchy " + existingHierarchy.key() + " as it is not a collection binding hierarchy");
        }
    }

    private ComponentKey<ComponentCollection<T>> createCollectionComponentKey() {
        return this.hierarchy().key().mutable().collector().build();
    }

    protected Binder lazyContainerSingleton(CheckedSupplier<ObjectContainer<T>> supplier) {
        return this.add(new LazySingletonProvider<>(supplier));
    }

    protected Binder add(Provider<T> provider) {
        provider = provider.map(container -> {
            container.processed(!this.processAfterInitialization);
            return container;
        });
        this.hierarchy().add(this.priority, provider);
        return this.binder();
    }
}
