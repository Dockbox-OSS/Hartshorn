/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.IllegalScopeException;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.collection.CollectorBindingFunction;
import org.dockbox.hartshorn.inject.collection.ComponentCollection;
import org.dockbox.hartshorn.inject.collection.HierarchyCollectorBindingFunction;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.LazySingletonInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.ObjectContainer;
import org.dockbox.hartshorn.inject.provider.PrototypeConstructorInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.SingletonInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.SupplierInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.inject.scope.ScopeModuleContext;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.jspecify.annotations.NonNull;

import java.util.function.Function;

/**
 * A {@link BindingFunction} that configures a {@link BindingHierarchy} for a specific key. The
 * hierarchy is provided by the owning {@link Binder}.
 *
 * @param <T> The type of the component that is bound.
 *
 * @see Binder
 * @see BindingHierarchy
 * 
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public class HierarchyBindingFunction<T> implements AliasBindingFunction<T> {

    private final AliasableBindingHierarchy<T> hierarchy;
    private final HierarchicalBinder binder;
    private final SingletonCache singletonCache;
    private final ScopeModuleContext moduleContext;
    private final BindingAliasNormalizer bindingAliasNormalizer;

    private Scope scope;
    private ScopeKey scopeKey;

    private int priority = Priority.DEFAULT_PRIORITY;
    private boolean processAfterInitialization = true;

    public HierarchyBindingFunction(
        AliasableBindingHierarchy<T> hierarchy,
        HierarchicalBinder binder,
        SingletonCache singletonCache,
        Scope scope,
        ScopeModuleContext moduleContext,
        BindingAliasNormalizer bindingAliasNormalizer
    ) {
        this.hierarchy = hierarchy;
        this.binder = binder;
        this.singletonCache = singletonCache;

        this.scope = scope;
        this.scopeKey = scope.installableScopeType();

        this.moduleContext = moduleContext;
        this.bindingAliasNormalizer = bindingAliasNormalizer;
    }

    /**
     * Returns the binding hierarchy that is being configured by this binding function. If the
     * binding has been installed to a specific scope, the hierarchy for that scope is returned.
     *
     * @return the binding hierarchy
     */
    protected BindingHierarchy<T> hierarchy() {
        if (this.scopeKey != null && !this.moduleContext.isApplicationScope(this.scopeKey)) {
            return new AliasableBindingHierarchyAdapter<>(this.moduleContext.hierarchy(
                    this.scopeKey, this.hierarchy.key().mutable().scope(this.scope).build()
            ));
        }
        else {
            return this.hierarchy;
        }
    }

    /**
     * Returns the binder that owns this binding function.
     *
     * @return the owning binder
     */
    protected Binder binder() {
        return this.binder;
    }

    /**
     * Returns the singleton cache used by this binding function.
     *
     * @return the singleton cache
     */
    protected SingletonCache singletonCache() {
        return this.singletonCache;
    }

    @Override
    public AliasBindingFunction<T> alias(Class<? super T> aliasType) {
        return this.alias(this.bindingAliasNormalizer.alias(this.hierarchy().key(), aliasType));
    }

    @Override
    public AliasBindingFunction<T> alias(QualifierKey<T> aliasQualifier) {
        return this.alias(this.bindingAliasNormalizer.alias(this.hierarchy().key(),
            aliasQualifier));
    }

    @Override
    public AliasBindingFunction<T> alias(ComponentKey<? super T> aliasKey) {
        BindingHierarchy<T> hierarchy = this.hierarchy();
        if (hierarchy instanceof AliasableBindingHierarchy<T> aliasableBindingHierarchy) {
            aliasableBindingHierarchy.alias(aliasKey);
        }
        else {
            throw new UnsupportedOperationException("Delegate hierarchy does not support aliasing");
        }
        return this;
    }

    @Override
    public AliasBindingFunction<T> installTo(ScopeKey scopeKey) throws IllegalScopeException {
        boolean expandingApplicationScope = this.moduleContext.isApplicationScope(this.scopeKey)
            || this.moduleContext.isApplicationScope(this.scope.installableScopeType());

        if (!expandingApplicationScope) {
            throw new IllegalScopeException("Cannot install binding to child scope "
                + scopeKey.name()
                + " as the binding is already installed to child scope "
                + this.scope.installableScopeType().name());
        }
        // Permitted, as default application scope may be expanded. Defined child scopes can not be
        // expanded, so this is a safe check
        if (!this.moduleContext.isApplicationScope(scopeKey)) {
            this.scope = null;
        }
        this.scopeKey = scopeKey;
        return this;
    }

    @Override
    public AliasBindingFunction<T> priority(int priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public AliasBindingFunction<T> processAfterInitialization(boolean processAfterInitialization) {
        this.processAfterInitialization = processAfterInitialization;
        return this;
    }

    @Override
    public Binder to(Class<? extends T> type) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException(
                ("Cannot overwrite singleton binding for %s in a hierarchy, "
                    + "ensure the new binding is a singleton").formatted(
                    this.hierarchy().key()));
        }
        ComponentKey<? extends T> key = this.buildComponentKey(type);
        return this.add(PrototypeConstructorInstantiationStrategy.forPrototype(key));
    }

    @NonNull
    private ComponentKey<? extends T> buildComponentKey(Class<? extends T> type) {
        return ComponentKey.builder(type).scope(this.scope).build();
    }

    @Override
    public Binder to(CheckedSupplier<T> supplier) {
        if (this.singletonCache.contains(this.hierarchy().key())) {
            throw new IllegalModificationException(
                ("Cannot overwrite singleton binding for %s in a hierarchy, "
                    + "ensure the new binding is a singleton").formatted(
                    this.hierarchy().key()));
        }
        return this.add(new SupplierInstantiationStrategy<>(supplier));
    }

    @Override
    public Binder to(InstantiationStrategy<T> strategy) {
        return this.add(strategy);
    }

    @Override
    public Binder singleton(T instance) {
        if (instance == null) {
            throw new IllegalModificationException("Cannot bind null instance");
        }
        if (this.processAfterInitialization) {
            return this.add(new SingletonInstantiationStrategy<>(instance));
        }
        else {
            // If no processing should happen, then we can immediately cache the instance
            this.singletonCache.put(this.hierarchy.key(), instance);
            for (ComponentKey<? super T> alias : this.hierarchy.aliases()) {
                this.singletonCache.put(alias, instance);
            }
            return this.binder();
        }
    }

    @Override
    public Binder lazySingleton(Class<T> type) {
        ComponentKey<? extends T> key = this.buildComponentKey(type);
        return this.add(PrototypeConstructorInstantiationStrategy.forSingleton(key));
    }

    @Override
    public Binder lazySingleton(CheckedFunction<Scope, T> supplier) {
        return this.add(new LazySingletonInstantiationStrategy<>(supplier));
    }

    @Override
    public Binder collect(Customizer<CollectorBindingFunction<T>> collector) {
        ComponentKey<ComponentCollection<T>> collectionComponentKey =
            this.createCollectionComponentKey();

        BindingHierarchy<ComponentCollection<T>> existingHierarchy =
            this.hierarchy(collectionComponentKey);

        HierarchyCollectorBindingFunction<T> function = new HierarchyCollectorBindingFunction<>(
                this.binder,
                existingHierarchy,
                this.priority
        );
        collector.configure(function);
        return this.binder;
    }

    private <R> BindingHierarchy<R> hierarchy(ComponentKey<R> key) {
        if (!this.moduleContext.isApplicationScope(this.scopeKey)) {
            return this.moduleContext.hierarchy(this.scopeKey, key);
        }
        return this.binder.hierarchy(key);
    }

    private ComponentKey<ComponentCollection<T>> createCollectionComponentKey() {
        return this.hierarchy().key().mutable()
                .scope(this.scope)
                .collector().build();
    }

    /**
     * Adds the provided {@link InstantiationStrategy} to the hierarchy, preserving the configured
     * {@link #processAfterInitialization} setting.
     *
     * @param strategy the instantiation strategy to add
     *
     * @return the binder
     */
    protected Binder add(InstantiationStrategy<T> strategy) {
        strategy =
            strategy.map(new ProcessAfterInitializationFunction<>(this.processAfterInitialization));
        this.hierarchy().add(this.priority, strategy);
        return this.binder();
    }

    /**
     * A function that indicates whether the provided {@link ObjectContainer} should be processed
     * after initialization. If {@code true}, the container will be marked as non-processed, meaning
     * it will be processed once the container initializes the instance.
     *
     * <p>Practically this function is a lambda, but for the sake of reportability, it is defined
     * as a dedicated record.
     *
     * @param processAfterInitialization whether the container should be processed after
     * initialization
     * @param <T> the type of the component in the container
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    private record ProcessAfterInitializationFunction<T>(boolean processAfterInitialization)
        implements Function<ObjectContainer<T>, ObjectContainer<T>> {

        @Override
        public ObjectContainer<T> apply(ObjectContainer<T> container) {
            container.processed(!this.processAfterInitialization);
            return container;
        }

        @Override
        public String toString() {
            return ObjectDescriber.of(this)
                .field("processAfterInitialization", this.processAfterInitialization)
                .describe();
        }
    }
}
