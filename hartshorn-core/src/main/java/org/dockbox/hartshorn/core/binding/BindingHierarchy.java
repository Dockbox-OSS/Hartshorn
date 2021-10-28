/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.Key;

import java.util.List;
import java.util.Map.Entry;

/**
 * A hierarchical representation of type providers. Each entry is represented by a {@link Entry}
 * containing the priority represented by a {@link Integer} as its key, and a {@link Provider} as
 * its value. When the hierarchy is iterated, the {@link Provider} with the highest priority will
 * be at the start of the {@link java.util.Iterator}, and the {@link Provider} with the lowest
 * priority will be at the end of the {@link java.util.Iterator}.
 * <p>This means hierarchies are always in a 'high to low' key order, meaning the highest priority
 * will be provided first.
 * <p>A hierarchy should be used to store the binding priorities for a specific type of type {@code C}.
 * @param <C>
 */
public interface BindingHierarchy<C> extends Iterable<Entry<Integer, Provider<C>>>, ContextCarrier {

    /**
     * Gets all providers in the order of their priorities.
     * @return All providers.
     */
    List<Provider<C>> providers();

    /**
     * Adds the given {@link Provider} with priority {@code -1}. If another provider already exists
     * with this priority, it will be overwritten.
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> add(Provider<C> provider);

    /**
     * Adds the given {@link Provider} with the given {@code priority}. If another provider already
     * exists with this priority, it will be overwritten.
     * @param priority The priority of the provider.
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> add(int priority, Provider<C> provider);

    /**
     * Adds the given {@link Provider} to the end of the hierarchy, using the current highest priority,
     * plus one. For example if the hierarchy contains providers with priorities 1, 2, and 3, this will
     * result in the added provider having priority 4.
     * @param provider The provider to add.
     * @return Itself, for chaining.
     */
    BindingHierarchy<C> addNext(Provider<C> provider);

    /**
     * Merges the given {@link BindingHierarchy} into the current hierarchy. If both hierarchies contain
     * {@link Provider providers} with the same priority, the one of the current hierarchy will be
     * preferred. The returned hierarchy is a new instance, the current hierarchy will not be modified.
     * @param hierarchy The hierarchy to merge with.
     * @return A new hierarchy, containing providers from both the current and given hierarchies.
     */
    BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy);

    /**
     * Gets the current size of the hierarchy, indicating the amount of registered {@link Provider providers}.
     * @return The amount of registered providers.
     */
    int size();

    /**
     * Gets the {@link Provider} at the given priority, if it exists.
     * @param priority The priority of the potential provider.
     * @return The provider if it exists, or {@link Exceptional#empty()}
     */
    Exceptional<Provider<C>> get(int priority);

    /**
     * Gets the {@link Key} of the current hierarchy, containing a {@link org.dockbox.hartshorn.core.context.element.TypeContext}
     * of type {@code C}, and a potential {@link javax.inject.Named} instance.
     * @return The key of the current hierarchy.
     * @see Key
     */
    Key<C> key();
}
