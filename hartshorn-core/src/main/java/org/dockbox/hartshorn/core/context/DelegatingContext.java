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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.List;

@Deprecated(since = "4.2.5", forRemoval = true)
public interface DelegatingContext<D extends Context> extends Context {

    @Override
    default <C extends Context> void add(final C context) {
        this.get().add(context);
    }

    D get();

    @Override
    default <N extends NamedContext> void add(final N context) {
        this.get().add(context);
    }

    @Override
    default <C extends Context> void add(final String name, final C context) {
        this.get().add(name, context);
    }

    @Override
    default <C extends Context> Exceptional<C> first(final ApplicationContext applicationContext, final Class<C> context) {
        return this.get().first(applicationContext, context);
    }

    @Override
    default Exceptional<Context> first(final String name) {
        return this.get().first(name);
    }

    @Override
    default <N extends Context> Exceptional<N> first(final String name, final Class<N> context) {
        return this.get().first(name, context);
    }

    @Override
    default <C extends Context> List<C> all(final Class<C> context) {
        return this.get().all(context);
    }

    @Override
    default List<Context> all(final String name) {
        return this.get().all(name);
    }

    @Override
    default <N extends Context> List<N> all(final String name, final Class<N> context) {
        return this.get().all(name, context);
    }
}
