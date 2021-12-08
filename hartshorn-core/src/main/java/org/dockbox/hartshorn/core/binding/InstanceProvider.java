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

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A singleton-like provider, which uses an existing instance of type {@code T} to
 * provide the requested instance.
 *
 * @param <T> The type of the instance to provide.
 * @author Guus Lieben
 * @since 4.1.2
 * @see Provider
 * @see SupplierProvider
 * @see ContextDrivenProvider
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InstanceProvider<T> implements Provider<T> {

    private final T instance;

    @Override
    public Exceptional<T> provide(final ApplicationContext context) {
        return Exceptional.of(this.instance);
    }
}
