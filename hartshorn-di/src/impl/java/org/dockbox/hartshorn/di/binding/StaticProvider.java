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

package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ApplicationContext;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @deprecated Use {@link ContextDrivenProvider} instead.
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Deprecated(since = "4.1.3", forRemoval = true)
public class StaticProvider<C> implements Provider<C> {

    private final Class<? extends C> target;

    @Override
    public Exceptional<C> provide(final ApplicationContext context) {
        return Exceptional.of(() -> this.target.getConstructor().newInstance());
    }
}
