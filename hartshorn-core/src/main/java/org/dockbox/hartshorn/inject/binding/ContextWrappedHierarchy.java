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

package org.dockbox.hartshorn.inject.binding;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.util.Result;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * A {@link ContextWrappedHierarchy} is a {@link BindingHierarchy} that wraps another {@link BindingHierarchy}
 * and binds to an active {@link ApplicationContext}. If the wrapped {@link BindingHierarchy} is updated, the
 * wrapped {@link BindingHierarchy} is updated as well, and directly updated in the bound {@link ApplicationContext}.
 *
 * @param <C> The type of the wrapped {@link BindingHierarchy}.
 * @author Guus Lieben
 * @since 21.4
 */
public class ContextWrappedHierarchy<C> implements BindingHierarchy<C> {

    private BindingHierarchy<C> real;
    private final ApplicationContext applicationContext;
    private final Consumer<BindingHierarchy<C>> onUpdate;

    public ContextWrappedHierarchy(final BindingHierarchy<C> real, final ApplicationContext applicationContext, final Consumer<BindingHierarchy<C>> onUpdate) {
        this.real = real;
        this.applicationContext = applicationContext;
        this.onUpdate = onUpdate;
    }

    public BindingHierarchy<C> real() {
        return this.real;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public List<Provider<C>> providers() {
        return this.real().providers();
    }

    @Override
    public BindingHierarchy<C> add(final Provider<C> provider) {
        this.real = this.real().add(provider);
        return this.update();
    }

    @Override
    public BindingHierarchy<C> add(final int priority, final Provider<C> provider) {
        this.real = this.real().add(priority, provider);
        return this.update();
    }

    @Override
    public BindingHierarchy<C> addNext(final Provider<C> provider) {
        this.real = this.real().addNext(provider);
        return this.update();
    }

    @Override
    public BindingHierarchy<C> merge(final BindingHierarchy<C> hierarchy) {
        this.real = this.real().merge(hierarchy);
        return this.update();
    }

    @Override
    public int size() {
        return this.real().size();
    }

    @Override
    public Result<Provider<C>> get(final int priority) {
        return this.real().get(priority);
    }

    @Override
    public Key<C> key() {
        return this.real().key();
    }

    /**
     * Updates the wrapped {@link BindingHierarchy} in the bound {@link ApplicationContext}. This behavior
     * may differ if the {@link #onUpdate} function was provided by an external source.
     *
     * @return Itself, for chaining.
     */
    private BindingHierarchy<C> update() {
        this.onUpdate.accept(this.real());
        return this;
    }

    @NonNull
    @Override
    public Iterator<Entry<Integer, Provider<C>>> iterator() {
        return this.real().iterator();
    }

    @Override
    public String toString() {
        return this.real().toString();
    }
}
