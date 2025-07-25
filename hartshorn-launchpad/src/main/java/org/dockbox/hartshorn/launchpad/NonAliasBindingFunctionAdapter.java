/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.IllegalScopeException;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.binding.AliasBindingFunction;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.inject.collection.CollectorBindingFunction;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.inject.scope.ScopeKey;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.function.CheckedSupplier;

/**
 * An adapter for {@link BindingFunction} that does not support aliasing. This may be used to adapt a binding function
 * to an environment that requires the usage of {@link AliasBindingFunction}. This adapter will delegate all calls to
 * the original binding function, but will throw an {@link UnsupportedOperationException} when an aliasing method is
 * called.
 *
 * @param <T> the type of the binding
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class NonAliasBindingFunctionAdapter<T> implements AliasBindingFunction<T> {

    private final BindingFunction<T> delegate;

    public NonAliasBindingFunctionAdapter(BindingFunction<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public AliasBindingFunction<T> alias(Class<? super T> aliasType) {
        throw new UnsupportedOperationException("Binding function does not support aliasing");
    }

    @Override
    public AliasBindingFunction<T> alias(ComponentKey<? super T> aliasKey) {
        throw new UnsupportedOperationException("Binding function does not support aliasing");
    }

    @Override
    public AliasBindingFunction<T> alias(QualifierKey<T> aliasQualifier) {
        throw new UnsupportedOperationException("Binding function does not support aliasing");
    }

    private AliasBindingFunction<T> currentOrNextAdapter(BindingFunction<T> function) {
        if (function == this.delegate) {
            return this;
        }
        else {
            return new NonAliasBindingFunctionAdapter<>(function);
        }
    }

    @Override
    public AliasBindingFunction<T> installTo(ScopeKey scope) throws IllegalScopeException {
        return this.currentOrNextAdapter(this.delegate.installTo(scope));
    }

    @Override
    public AliasBindingFunction<T> processAfterInitialization(boolean processAfterInitialization) {
        return this.currentOrNextAdapter(this.delegate.processAfterInitialization(processAfterInitialization));
    }

    @Override
    public Binder to(Class<? extends T> type) {
        return this.delegate.to(type);
    }

    @Override
    public Binder to(CheckedSupplier<T> supplier) {
        return this.delegate.to(supplier);
    }

    @Override
    public Binder to(InstantiationStrategy<T> strategy) {
        return this.delegate.to(strategy);
    }

    @Override
    public Binder singleton(T instance) {
        return this.delegate.singleton(instance);
    }

    @Override
    public Binder lazySingleton(Class<T> type) {
        return this.delegate.lazySingleton(type);
    }

    @Override
    public Binder lazySingleton(CheckedSupplier<T> supplier) {
        return this.delegate.lazySingleton(supplier);
    }

    @Override
    public Binder collect(Customizer<CollectorBindingFunction<T>> collector) {
        return this.delegate.collect(collector);
    }

    @Override
    public AliasBindingFunction<T> priority(int priority) {
        return this.currentOrNextAdapter(this.delegate.priority(priority));
    }
}
