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

package org.dockbox.hartshorn.inject.binding.collection;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.ComponentObjectContainer;
import org.dockbox.hartshorn.inject.ContextAwareComponentSupplier;
import org.dockbox.hartshorn.inject.ContextDrivenProvider;
import org.dockbox.hartshorn.inject.LazySingletonProvider;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.inject.SingletonProvider;
import org.dockbox.hartshorn.inject.SupplierProvider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.IllegalModificationException;
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
    private final CollectionBindingHierarchy<T> hierarchy;
    private final int priority;

    public HierarchyCollectorBindingFunction(Binder binder, CollectionBindingHierarchy<T> hierarchy, int priority) {
        this.binder = binder;
        this.hierarchy = hierarchy;
        this.priority = priority;
    }

    @Override
    public Binder provider(Provider<T> provider) {
        this.hierarchy.getOrCreateProvider(this.priority).add(provider);
        return this.binder;
    }

    @Override
    public Binder supplier(CheckedSupplier<T> supplier) {
        return this.provider(new SupplierProvider<>(supplier));
    }

    @Override
    public Binder supplier(ContextAwareComponentSupplier<T> supplier) {
        return this.provider(supplier);
    }

    @Override
    public Binder singleton(T instance) {
        return this.provider(new SingletonProvider<>(instance));
    }

    @Override
    public Binder type(Class<? extends T> type) {
        ComponentKey<? extends T> componentKey = this.hierarchy.key().mutable().type(type).build();
        return this.provider(new ContextDrivenProvider<>(componentKey));
    }

    @Override
    public Binder lazySingleton(CheckedSupplier<T> supplier) {
        return this.provider(new LazySingletonProvider<>(() -> {
            T instance = supplier.get();
            if (instance == null) {
                throw new IllegalModificationException("Cannot bind null instance");
            }
            return new ComponentObjectContainer<>(instance);
        }));
    }
}
