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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Named;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The default implementation of the {@link BindingHierarchy} interface. This uses a specified {@link Key} to
 * identify the binding hierarchy, and stores the bindings in a {@link TreeMap}.
 *
 * @param <C> The type of type to provide.
 * @author Guus Lieben
 * @since 21.4
 * @see BindingHierarchy
 */
@RequiredArgsConstructor
public class NativeBindingHierarchy<C> implements BindingHierarchy<C> {

    @Getter private final Key<C> key;
    @Getter private final ApplicationContext applicationContext;
    private final TreeMap<Integer, Provider<C>> bindings = new TreeMap<>(Collections.reverseOrder());

    @Override
    public List<Provider<C>> providers() {
        return List.copyOf(this.bindings.values());
    }

    @Override
    public BindingHierarchy<C> add(final Provider<C> provider) {
        return this.add(-1, provider);
    }

    @Override
    public BindingHierarchy<C> add(final int priority, final Provider<C> provider) {
        // Default providers may be overwritten without further warnings
        if (this.bindings.containsKey(priority) && priority != -1) {
            this.applicationContext().log().warn(("There is already a provider for %s with priority %d. It will be overwritten! " +
                    "To avoid unexpected behavior, ensure the priority is not already present. Current hierarchy: %s").formatted(this.key()
                    .type().name(), priority, this));
        }
        this.bindings.put(priority, provider);
        return this;
    }

    @Override
    public BindingHierarchy<C> addNext(final Provider<C> provider) {
        int next = -1;
        if (!this.bindings.isEmpty()) next = this.bindings.lastKey()+1;
        return this.add(next, provider);
    }

    @Override
    public BindingHierarchy<C> merge(final BindingHierarchy<C> hierarchy) {
        final BindingHierarchy<C> merged = new NativeBindingHierarchy<>(this.key(), this.applicationContext);
        // Low priority, other
        for (final Entry<Integer, Provider<C>> entry : hierarchy) {
            merged.add(entry.getKey(), entry.getValue());
        }
        // High priority, self
        for (final Entry<Integer, Provider<C>> entry : this) {
            merged.add(entry.getKey(), entry.getValue());
        }
        return merged;
    }

    @Override
    public int size() {
        return this.bindings.size();
    }

    @Override
    public Exceptional<Provider<C>> get(final int priority) {
        return Exceptional.of(this.bindings.getOrDefault(priority, null));
    }

    @Override
    public String toString() {
        final String contract = this.key().type().name();
        final Named named = this.key().name();
        String name = "";
        if (named != null) {
            name = "::" + named.value();
        }

        // The priorities are stored high to low, however we want to display them as low-to-high.
        final List<Entry<Integer, Provider<C>>> entries = new ArrayList<>(this.bindings.entrySet());
        Collections.reverse(entries);

        final String hierarchy = entries.stream()
                .map(entry -> {
                    final Provider<C> value = entry.getValue();
                    String target = value.toString();
                    if (value instanceof ContextDrivenProvider contextDrivenProvider) {
                        target = contextDrivenProvider.context().name();
                    }
                    return "%s: %s".formatted(String.valueOf(entry.getKey()), target);
                })
                .collect(Collectors.joining(" -> "));

        return "Hierarchy[%s%s]: %s".formatted(contract, name, hierarchy);
    }

    @NonNull
    @Override
    public Iterator<Entry<Integer, Provider<C>>> iterator() {
        return this.bindings.entrySet().iterator();
    }
}
