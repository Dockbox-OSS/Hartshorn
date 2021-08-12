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

package org.dockbox.hartshorn.cache;

import org.dockbox.hartshorn.di.properties.Attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Injector property which can be provided to {@link Cache} entries to set
 * the cache expiration when the cache instance is enabled.
 */
@AllArgsConstructor
public class ExpirationAttribute implements Attribute<Expiration> {
    @Getter private final Expiration value;
}
