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

package org.dockbox.hartshorn.cache.context;

import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.Expiration;
import org.dockbox.hartshorn.cache.annotations.Cached;
import org.dockbox.hartshorn.cache.annotations.EvictCache;
import org.dockbox.hartshorn.cache.annotations.UpdateCache;

/**
 * The method-specific cache context containing all required information to
 * manipulate cache methods. Values are typically derived from {@link Cached},
 * {@link EvictCache}, or {@link UpdateCache}.
 */
public interface CacheMethodContext {

    Class<? extends CacheManager> manager();

    String name();

    Expiration expiration();

}
