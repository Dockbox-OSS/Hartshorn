/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.provider.InstantiationStrategy;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link SubscribableBindingHierarchy} is a {@link BindingHierarchy} that can be subscribed to. If the delegate
 * {@link BindingHierarchy} is updated, the given subscriber will be notified. This allows for external components to
 * react to changes in the hierarchy, without having to poll the hierarchy for changes.
 *
 * @param <C> The type of the wrapped {@link BindingHierarchy}.
 *
 * @since 0.4.3
 *
 * @author Guus Lieben
 */
public class SubscribableBindingHierarchy<C> implements PrunableBindingHierarchy<C> {

    private final Consumer<BindingHierarchy<C>> onUpdate;

    private BindingHierarchy<C> real;

    public SubscribableBindingHierarchy(BindingHierarchy<C> real, Consumer<BindingHierarchy<C>> onUpdate) {
        this.real = real;
        this.onUpdate = onUpdate;
    }

    /**
     * @return The wrapped {@link BindingHierarchy}.
     */
    protected BindingHierarchy<C> real() {
        return this.real;
    }

    @Override
    public List<InstantiationStrategy<C>> providers() {
        return this.real().providers();
    }

    @Override
    public BindingHierarchy<C> add(InstantiationStrategy<C> strategy) {
        this.real = this.real().add(strategy);
        return this.hierarchyUpdated();
    }

    @Override
    public BindingHierarchy<C> add(int priority, InstantiationStrategy<C> strategy) {
        this.real = this.real().add(priority, strategy);
        return this.hierarchyUpdated();
    }

    @Override
    public BindingHierarchy<C> addNext(InstantiationStrategy<C> strategy) {
        this.real = this.real().addNext(strategy);
        return this.hierarchyUpdated();
    }

    @Override
    public BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy) {
        this.real = this.real().merge(hierarchy);
        return this.hierarchyUpdated();
    }

    @Override
    public int size() {
        return this.real().size();
    }

    @Override
    public Option<InstantiationStrategy<C>> get(int priority) {
        return this.real().get(priority);
    }

    @Override
    public int highestPriority() {
        return this.real().highestPriority();
    }

    @Override
    public SortedSet<Integer> priorities() {
        return this.real().priorities();
    }

    @Override
    public ComponentKey<C> key() {
        return this.real().key();
    }

    /**
     * Notifies the subscriber of a change in the hierarchy.
     *
     * @return Itself, for chaining.
     */
    private BindingHierarchy<C> hierarchyUpdated() {
        this.onUpdate.accept(this.real());
        return this;
    }

    @NonNull
    @Override
    public Iterator<Entry<Integer, InstantiationStrategy<C>>> iterator() {
        return this.real().iterator();
    }

    @Override
    public String toString() {
        return this.real().toString();
    }

    @Override
    public boolean prune(int priority) {
        if (this.real() instanceof PrunableBindingHierarchy<C> prunableBindingHierarchy) {
            return prunableBindingHierarchy.prune(priority);
        }
        return false;
    }

    @Override
    public int pruneAbove(int priority) {
        if (this.real() instanceof PrunableBindingHierarchy<C> prunableBindingHierarchy) {
            return prunableBindingHierarchy.pruneAbove(priority);
        }
        return 0;
    }

    @Override
    public int pruneBelow(int priority) {
        if (this.real() instanceof PrunableBindingHierarchy<C> prunableBindingHierarchy) {
            return prunableBindingHierarchy.pruneBelow(priority);
        }
        return 0;
    }
}
