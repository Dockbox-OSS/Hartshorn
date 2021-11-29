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

package org.dockbox.hartshorn.core.annotations.proxy;

import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.annotations.component.Component;
import org.dockbox.hartshorn.core.proxy.Phase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used to mark a type as proxy executor. Proxy methods are still required to be
 * decorated with {@link Proxy.Target}. Any non-annotated method will be ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Deprecated(since = "4.2.5", forRemoval = true)
public @interface Proxy {
    /**
     * The target class for the proxy. Can be an interface, abstract, or concrete class.
     *
     * @return The target class
     */
    Class<?> value();

    /**
     * The annotation used to mark a method as proxy executor. When {@link #method()} is unchanged,
     * the name of the annotated method will be used when looking up the target method in the target
     * type. Any parameters not decorated with {@link Instance} will be used during lookup, in the
     * exact order they are defined.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.METHOD)
    @interface Target {
        /** Whether to overwrite the return value of the target method. */
        boolean overwrite() default true;

        /**
         * At which {@link Phase} the proxy executor should be performed.
         *
         * @return The phase
         * @see Phase
         */
        Phase at() default Phase.OVERWRITE;

        /**
         * The priority of the proxy executor. The lower the number, the earlier it will be executed
         * during its dedicated {@link Phase}
         */
        int priority() default 10;

        /**
         * The name of the method to target, without parameter or class qualifiers. Only relevant if the
         * name of the annotated method differs from the target method.
         */
        String method() default "";
    }
}
