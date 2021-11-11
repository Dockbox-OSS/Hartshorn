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

public interface Context {

    <C extends Context> void add(C context);

    <N extends NamedContext> void add(N context);

    <C extends Context> void add(String name, C context);

    <C extends Context> Exceptional<C> first(ApplicationContext applicationContext, Class<C> context);

    Exceptional<Context> first(String name);

    <N extends Context> Exceptional<N> first(String name, Class<N> context);

    <C extends Context> List<C> all(Class<C> context);

    List<Context> all(String name);

    <N extends Context> List<N> all(String name, Class<N> context);

}
