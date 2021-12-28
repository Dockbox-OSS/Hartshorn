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
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ContextWrappedHierarchy<C> implements BindingHierarchy<C> {

    @Getter private BindingHierarchy<C> real;
    @Getter private ApplicationContext applicationContext;
    private final Consumer<BindingHierarchy<C>> onUpdate;

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
    public Exceptional<Provider<C>> get(final int priority) {
        return this.real().get(priority);
    }

    @Override
    public Key<C> key() {
        return this.real().key();
    }

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
