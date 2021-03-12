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

package org.dockbox.selene.api.annotations.command;

import org.dockbox.selene.api.server.SeleneInformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * The annotation used to mark a method or class as a command holder.
 *
 * @see <a href="https://github.com/GuusLieben/Selene/wiki/Commands">Commands</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Command {
    /**
     * The aliases for the command.
     *
     * @return the aliases
     */
    String[] aliases();

    /**
     * The usage context for the command.
     *
     * @return the usage context for the command.
     * @see <a
     *         href="https://github.com/GuusLieben/Selene/wiki/Commands#defining-command-usage">Commands#defining-command-usage</a>
     */
    String usage();

    /**
     * The permissions for the command. Defaults to {@link SeleneInformation#GLOBAL_BYPASS} if not
     * defined.
     *
     * @return the permission required for the command.
     */
    String permission() default SeleneInformation.GLOBAL_BYPASS;

    /**
     * The duration in the {@link Command#cooldownUnit() cooldown unit}. Defaults to -1 if not
     * defined, disabling the cooldown functionality for the command.
     *
     * @return the duration
     */
    long cooldownDuration() default -1;

    /**
     * The cooldown unit. Defaults to {@link ChronoUnit#SECONDS} if not defined.
     *
     * @return the cooldown unit
     */
    ChronoUnit cooldownUnit() default ChronoUnit.SECONDS;

    /**
     * Marks whether or not the command is a child command. This typically only affects methods
     * annoted with this annotation which are inside a class annoted with this annotation.
     *
     * @return true if the command is a child command
     */
    boolean inherit() default true;

    /**
     * Marks whether the command is a standalone command, or adds child commands to a existing
     * registration. This typically only affects classes annoted with this annotation.
     *
     * @return true if this command adds child commands to a existing registration.
     */
    boolean extend() default false;

    /**
     * Marks whether or not the command should require user confirmation before executing.
     *
     * @return true if confirmation is required.
     */
    boolean confirm() default false;
}
