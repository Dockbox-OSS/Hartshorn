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

package org.dockbox.hartshorn.core.annotations.activate;

import org.dockbox.hartshorn.core.ApplicationBootstrap;
import org.dockbox.hartshorn.core.annotations.inject.InjectConfig;
import org.dockbox.hartshorn.core.proxy.ProxyApplicationBootstrap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a class can be used as activation source by providing the
 * required metadata.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Activator {

    /**
     * @return The bootstrap type which should be used for the current activator.
     */
    Class<? extends ApplicationBootstrap> value() default ProxyApplicationBootstrap.class;

    /**
     * @return The default prefix for the activator. If this is left empty the package of
     *         the activation source is used
     */
    String prefix() default "";

    /**
     * @return The applicable {@link InjectConfig configurations} which should be used for
     *         this activator
     */
    InjectConfig[] configs() default {};
}
