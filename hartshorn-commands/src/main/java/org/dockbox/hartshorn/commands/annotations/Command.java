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

package org.dockbox.hartshorn.commands.annotations;

import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.di.ComponentType;
import org.dockbox.hartshorn.di.annotations.component.ComponentLike;
import org.dockbox.hartshorn.di.annotations.component.ComponentAlias;
import org.dockbox.hartshorn.di.services.ComponentAspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used to mark a method or class as a command holder.
 *
 * @see <a href="https://github.com/GuusLieben/Hartshorn/wiki/Commands">Commands</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@ComponentLike(singleton = Tristate.TRUE, type = ComponentType.FUNCTIONAL)
public @interface Command {
    /**
     * The aliases for the command.
     *
     * @return the aliases
     */
    @ComponentAlias(ComponentAspect.ID)
    String[] value() default "";

    /**
     * The arguments context for the command. If the default value is used no arguments will be validated,
     * delivering the same result as making it equal to the primary alias.
     *
     * @return the arguments context for the command.
     * @see <a
     *         href="https://github.com/GuusLieben/Hartshorn/wiki/Commands#defining-command-usage">Commands#defining-command-arguments</a>
     */
    String arguments() default "";

    /**
     * The permissions for the command.
     *
     * @return the permission required for the command.
     */
    String permission() default "";

    @ComponentAlias(ComponentAspect.OWNER)
    Class<?> parent() default Void.class;
}
