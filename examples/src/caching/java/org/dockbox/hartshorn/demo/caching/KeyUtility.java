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

package org.dockbox.hartshorn.demo.caching;

import org.dockbox.hartshorn.core.keys.Key;
import org.dockbox.hartshorn.core.keys.Keys;
import org.dockbox.hartshorn.demo.caching.domain.User;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.util.Map;

/**
 * A simple utility class containing a simple {@link Key} to indicate the last modification time of a
 * {@link User}. The data is stored in a local {@link Map}, however it is possible to use any data store,
 * whether it is persistent or non-persistent.
 */
public class KeyUtility {

    private static final Map<User, Long> DATA = HartshornUtils.emptyMap();

    public static final Key<User, Long> LAST_MODIFIED = Keys.builder(User.class, Long.class)
            .withGetter(DATA::get)
            .withSetter(DATA::put)
            .build();
}
