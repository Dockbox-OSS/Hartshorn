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

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.inject.Provider;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

/**
 * A hierarchical representation of type providers. Each entry is represented by a {@link Entry}
 * containing the priority represented by a {@link Integer} as its key, and a {@link Provider} as
 * its value. When the hierarchy is iterated, the {@link Provider} with the highest priority will
 * be at the start of the {@link java.util.Iterator}, and the {@link Provider} with the lowest
 * priority will be at the end of the {@link java.util.Iterator}.
 *
 * <p>This means hierarchies are always in a 'high to low' key order, meaning the highest priority
 * will be provided first.
 *
 * <p>A hierarchy should be used to store the binding priorities for a specific type of type {@code C}.
 *
 * @param <C> The type of type {@code C} that the hierarchy is for.
 *
 * @since 0.4.3
 *
 * @author Guus Lieben
 */
public interface BindingHierarchy<C> extends Iterable<Entry<Integer, Provider<C>>>, ContextCarrier {

    /**
     * Gets all providers in the order of their priorities.
     *
     * @return All providers.
     */
    List<Provider<C>> providers();

    /**
     * Adds the given {@link Provider} with priority {@code -1}. If another provider already exists
     * with this priority, it will be overwritten.
     *
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> add(Provider<C> provider);

    /**
     * Adds the given {@link Provider} with the given {@code priority}. If another provider already
     * exists with this priority, it will be overwritten.
     *
     * @param priority The priority of the provider.
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> add(int priority, Provider<C> provider);

    /**
     * Adds the given {@link Provider} to the end of the hierarchy, using the current highest priority,
     * plus one. For example if the hierarchy contains providers with priorities 1, 2, and 3, this will
     * result in the added provider having priority 4.
     *
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> addNext(Provider<C> provider);

    /**
     * Merges the given {@link BindingHierarchy} into the current hierarchy. If both hierarchies contain
     * {@link Provider providers} with the same priority, the one of the current hierarchy will be
     * preferred. The returned hierarchy is a new instance, the current hierarchy will not be modified.
     *
     * @param hierarchy The hierarchy to merge with.
     * @return A new hierarchy, containing providers from both the current and given hierarchies.
     */
    BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy);

    /**
     * Gets the current size of the hierarchy, indicating the amount of registered {@link Provider providers}.
     *
     * @return The amount of registered providers.
     */
    int size();

    /**
     * Gets the {@link Provider} at the given priority, if it exists.
     *
     * @param priority The priority of the potential provider.
     * @return The provider if it exists, or {@link Option#empty()}
     */
    Option<Provider<C>> get(int priority);

    /**
     * Gets the priority of the highest priority provider in the hierarchy.
     *
     * @return The highest priority, or {@code -1} if the hierarchy is empty.
     */
    int highestPriority();

    /**
     * Gets all priorities in the hierarchy, in ascending order.
     *
     * @return All priorities.
     */
    SortedSet<Integer> priorities();

    /**
     * Gets the {@link ComponentKey} of the current hierarchy, containing a {@link Class}
     * of type {@code C}, and potential qualifiers.
     *
     * @return The key of the current hierarchy.
     * @see ComponentKey
     */
    ComponentKey<C> key();
}
