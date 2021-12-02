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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.context.AutoCreating;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.util.Map;
import java.util.function.Function;

import lombok.Getter;

@Getter
@AutoCreating
public class BackingImplementationContext extends DefaultContext {

    private final Map<Class<?>, Object> implementations = HartshornUtils.emptyConcurrentMap();

    public <P> Exceptional<P> get(final Class<P> type) {
        return Exceptional.of(() -> (P) this.implementations.get(type));
    }

    public <P> P computeIfAbsent(final Class<P> key, @NonNull final Function<? super Class<P>, P> mappingFunction) {
        return (P) this.implementations.computeIfAbsent(key, (Function<? super Class<?>, ?>) mappingFunction);
    }
}
