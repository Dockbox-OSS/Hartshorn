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

package org.dockbox.hartshorn.di.annotations.activate;

import org.dockbox.hartshorn.di.ApplicationBootstrap;
import org.dockbox.hartshorn.di.adapter.InjectSource;
import org.dockbox.hartshorn.di.adapter.ServiceSource;
import org.dockbox.hartshorn.di.annotations.inject.InjectConfig;

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
     * @return The target enum constant of the {@link InjectSource} provided at
     * {@link #injectSource()}. If {@link #injectSource()} is not an enum type
     * this can be left empty.
     */
    String inject() default "";

    /**
     * @return The {@link InjectSource} type for the current activator. If this
     * targets an enum value, {@link #inject()} should indicate the target constant.
     */
    Class<? extends InjectSource> injectSource();

    /**
     * @return The bootstrap type which should be used for the current activator.
     */
    Class<? extends ApplicationBootstrap> value();

    /**
     * @return The default prefix for the activator. If this is left empty the package of
     * the activation source is used
     */
    String prefix() default "";

    /**
     * @return The applicable {@link InjectConfig configurations} which should be used for
     * this activator
     */
    InjectConfig[] configs() default {};

    /**
     * @return The target enum constant of the {@link ServiceSource} provided at
     * {@link #serviceSource()}. If {@link #serviceSource()} is not an enum type
     * this can be left empty.
     */
    String service() default "";

    /**
     * @return The {@link ServiceSource} type for the current activator. If this
     * targets an enum value, {@link #service()} should indicate the target constant.
     */
    Class<? extends ServiceSource> serviceSource();
}
