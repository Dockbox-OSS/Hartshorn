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

package org.dockbox.selene.commands.annotations;

import org.dockbox.selene.commands.parameter.CustomParameterPattern;
import org.dockbox.selene.commands.parameter.HashtagParameterPattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to indicate that a type can be provided to command definitions. When a type is annotated with this annotation, it can be automatically
 * constructed using its available constructors.
 *
 * <p>Also see <a href="https://github.com/GuusLieben/Selene/wiki/Command-Arguments>Selene/Command Arguments</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parameter {

    Class<? extends CustomParameterPattern> pattern() default HashtagParameterPattern.class;
    String value();
    String usage() default "";

}
