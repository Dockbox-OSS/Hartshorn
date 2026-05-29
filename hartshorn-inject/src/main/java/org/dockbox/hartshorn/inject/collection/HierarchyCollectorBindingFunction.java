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

package org.dockbox.hartshorn.inject.collection;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.inject.provider.PrototypeConstructorInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.LazySingletonInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.PrototypeInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.SingletonInstantiationStrategy;
import org.dockbox.hartshorn.inject.provider.SupplierInstantiationStrategy;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.util.function.CheckedFunction;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

/**
 * A {@link CollectorBindingFunction} that configures a {@link BindingHierarchy} for a compatible
 * {@link CollectionBindingHierarchy}. The hierarchy is provided by the owning {@link Binder}.
 *
 * @param <T> The type of the component that is bound.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class HierarchyCollectorBindingFunction<T> implements CollectorBindingFunction<T> {

    private final Binder binder;
    private final BindingHierarchy<ComponentCollection<T>> hierarchy;
    private final int priority;

    public HierarchyCollectorBindingFunction(
        Binder binder,
        BindingHierarchy<ComponentCollection<T>> hierarchy,
        int priority
    ) {
        this.binder = binder;
        this.hierarchy = hierarchy;
        this.priority = priority;
    }

    @Override
    public Binder provider(InstantiationStrategy<T> strategy) {
        this.hierarchy.get(this.priority)
                .ofType(CollectionInstantiationStrategy.class)
                .peek(collector -> collector.add(strategy))
                .orElseThrow(() -> {
                    return new IllegalStateException(
                        "Cannot add provider to collection hierarchy: "
                            + "No collection provider found at priority " + this.priority
                    );
                });
        return this.binder;
    }

    @Override
    public Binder supplier(CheckedSupplier<T> supplier) {
        return this.provider(new SupplierInstantiationStrategy<>(supplier));
    }

    @Override
    public Binder supplier(PrototypeInstantiationStrategy<T> supplier) {
        return this.provider(supplier);
    }

    @Override
    public Binder singleton(T instance) {
        return this.provider(new SingletonInstantiationStrategy<>(instance));
    }

    @Override
    public Binder type(Class<? extends T> type) {
        ComponentKey<? extends T> componentKey = this.hierarchy.key().mutable().type(type).build();
        return this.provider(PrototypeConstructorInstantiationStrategy.forPrototype(componentKey));
    }

    @Override
    public Binder lazySingleton(CheckedFunction<Scope, T> supplier) {
        return this.provider(new LazySingletonInstantiationStrategy<>(scope -> {
            T instance = supplier.apply(scope);
            if (instance == null) {
                throw new IllegalModificationException("Cannot bind null instance");
            }
            return instance;
        }));
    }
}
