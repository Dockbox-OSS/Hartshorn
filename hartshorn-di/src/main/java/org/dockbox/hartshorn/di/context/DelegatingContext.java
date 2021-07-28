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

package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;

import java.util.List;

public interface DelegatingContext<D extends Context> extends Context {

    D get();

    @Override
    default <C extends Context> void add(C context) {
        this.get().add(context);
    }

    @Override
    default <N extends NamedContext> void add(N context) {
        this.get().add(context);
    }

    @Override
    default <C extends Context> void add(String name, C context) {
        this.get().add(name, context);
    }

    @Override
    default <C extends Context> Exceptional<C> first(Class<C> context) {
        return this.get().first(context);
    }

    @Override
    default Exceptional<Context> first(String name) {
        return this.get().first(name);
    }

    @Override
    default <N extends Context> Exceptional<N> first(String name, Class<N> context) {
        return this.get().first(name, context);
    }

    @Override
    default <C extends Context> List<C> all(Class<C> context) {
        return this.get().all(context);
    }

    @Override
    default List<Context> all(String name) {
        return this.get().all(name);
    }

    @Override
    default <N extends Context> List<N> all(String name, Class<N> context) {
        return this.get().all(name, context);
    }
}
