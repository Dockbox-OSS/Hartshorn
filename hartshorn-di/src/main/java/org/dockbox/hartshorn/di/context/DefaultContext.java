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
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DefaultContext implements Context {

    protected final transient Set<Context> contexts = HartshornUtils.emptyConcurrentSet();

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> Exceptional<C> first(Class<C> context) {
        return Exceptional.of(this.contexts.stream()
                .filter(c -> c.getClass().equals(context))
                .findFirst())
                .map(c -> (C) c);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends Context> List<C> all(Class<C> context) {
        return HartshornUtils.asUnmodifiableList(this.contexts.stream()
                .filter( c -> c.getClass().equals(context))
                .map(c -> (C) c)
                .collect(Collectors.toList()));
    }

    @Override
    public <C extends Context> void add(C context) {
        if (context != null) this.contexts.add(context);
    }

}
