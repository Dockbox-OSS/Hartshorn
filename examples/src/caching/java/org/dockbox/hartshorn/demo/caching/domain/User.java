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

package org.dockbox.hartshorn.demo.caching.domain;

import org.dockbox.hartshorn.core.keys.KeyHolder;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A simple bean type which can carry {@link org.dockbox.hartshorn.core.keys.Key keys}, as it implements
 * {@link KeyHolder}. {@link org.dockbox.hartshorn.core.keys.Key Keys} are not persistent, and depend on
 * a managed data store.
 */
@Getter
@AllArgsConstructor
public class User implements KeyHolder<User> {

    // For the KeyHolder
    private final ApplicationContext applicationContext;
    private final String name;
    private final int age;
}
