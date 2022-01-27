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

package org.dockbox.hartshorn.core.annotations.inject;

import org.dockbox.hartshorn.core.ComponentType;
import org.dockbox.hartshorn.core.annotations.Extends;
import org.dockbox.hartshorn.core.annotations.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Named;

/**
 * Indicates the annotated type is to be bound to the specified type. This creates a basic entry
 * to the active {@link org.dockbox.hartshorn.core.binding.BindingHierarchy} for the specified
 * target {@link org.dockbox.hartshorn.core.Key}. The {@link org.dockbox.hartshorn.core.Key} is
 * created from the {@link #value()} and the {@link Named} value of {@link #named()}.
 *
 * <p>By default, the binding is created with the default priority of {@code -1}, but this can be
 * changed by specifying the {@link #priority()} value.
 *
 * <p>This annotation can be repeated, to create multiple bindings for the different
 * {@link org.dockbox.hartshorn.core.Key}s.
 *
 * <p>This acts as a shortcut for components which implement a specific interface and wish to
 * be bound to that interface.
 *
 * @author Guus Lieben
 * @since 21.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
@Component(lazy = true)
public @interface ComponentBinding {

    /**
     * @see Component#id()
     */
    String id() default "";

    /**
     * @see Component#name()
     */
    String name() default "";

    /**
     * @see Component#singleton()
     */
    boolean singleton() default false;

    /**
     * @see Component#type()
     */
    ComponentType type() default ComponentType.INJECTABLE;

    /**
     * @see Component#permitProxying()
     */
    boolean permitProxying() default true;

    /**
     * The type to bind to.
     * @return The type to bind to.
     */
    Class<?> value();

    /**
     * The priority of the binding.
     * @return The priority of the binding.
     */
    int priority() default -1;

    /**
     * The {@link Named} annotation to use for the binding {@link org.dockbox.hartshorn.core.Key}.
     * @return The {@link Named} annotation to use for the binding {@link org.dockbox.hartshorn.core.Key}.
     */
    Named named() default @Named;
}
