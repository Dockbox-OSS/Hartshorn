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

package org.dockbox.hartshorn.cache.annotations;

import org.dockbox.hartshorn.cache.CacheManager;
import org.dockbox.hartshorn.cache.SimpleCacheManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method decorator to indicate it can be used to update a cache. This
 * requires the method parameter to be assignable to the type stored in
 * the cache.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UpdateCache {
    /**
     * The ID of the target cache. If this is left empty a name will be
     * generated based on the owning service.
     * @return the cache ID
     */
    String value() default "";

    /**
     * Indicates the cache manager to use. This type can be provided through
     * the active {@link org.dockbox.hartshorn.di.context.ApplicationContext}.
     * @return the type of the cache manager to use
     */
    Class<? extends CacheManager> manager() default SimpleCacheManager.class;
}
