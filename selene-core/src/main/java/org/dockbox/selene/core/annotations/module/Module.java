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

package org.dockbox.selene.core.annotations.module;

import org.dockbox.selene.core.module.ModuleManager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to mark a type as a module.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Module {
    /**
     * The identifier of the module, in snake_case. This is typically used to obtain the module instance, header, or
     * context through {@link ModuleManager}.
     *
     * @return the identifier
     */
    String id();

    /**
     * The human-readable name of the module.
     *
     * @return the name
     */
    String name();

    /**
     * The human-readable description of the module.
     *
     * @return the human-readable description
     */
    String description();

    /**
     * The authors of the module. Preferably using GitHub usernames, though real names or other aliases are permitted.
     *
     * @return the authors of the module
     */
    String[] authors();

    /**
     * The qualified names of packages required to be present by the module. For example {@code java.lang}.
     *
     * @return the packages requires by the module.
     */
    String[] dependencies() default {};
}
