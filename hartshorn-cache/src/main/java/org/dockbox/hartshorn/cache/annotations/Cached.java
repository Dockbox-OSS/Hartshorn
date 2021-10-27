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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Method decorator which indicates the return value of the method can and
 * should be cached.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Cached {
    /**
     * The ID of the target cache. If this is left empty a name will be
     * generated based on the owning service.
     *
     * @return the cache ID
     */
    String value() default "";

    /**
     * Indicates whether the cache should automatically expire. If this is
     * left empty the cache will never expire unless manually evicted. The
     * duration indicated is activated the moment the method is first called.
     *
     * @return the lifetime
     */
    Expire expires() default @Expire(amount = -1, unit = TimeUnit.NANOSECONDS);
}
