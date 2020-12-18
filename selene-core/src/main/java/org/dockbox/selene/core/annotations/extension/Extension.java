/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.annotations.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface to mark a type as a extension.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {
    /**
     * The identifier of the extension, in snake_case. This is typically used to obtain the extension instance, header, or
     * context through {@link org.dockbox.selene.core.extension.ExtensionManager}.
     *
     * @return the identifier
     */
    String id();

    /**
     * The human-readable name of the extension.
     *
     * @return the name
     */
    String name();

    /**
     * The version of the extension.
     *
     * @return the version
     */
    String version() default "unknown";

    /**
     * The human-readable description of the extension.
     *
     * @return the human-readable description
     */
    String description();

    /**
     * The url of the homepage for the extension. Defaults to the homepage for Selene.
     *
     * @return the url of the homepage
     */
    String url() default "https://github.com/GuusLieben/Selene";

    /**
     * The authors of the extension. Preferably using GitHub usernames, though real names or other aliases are permitted.
     *
     * @return the authors of the extension
     */
    String[] authors();

    /**
     * The qualified names of packages required to be present by the extension. For example {@code java.lang}.
     *
     * @return the packages requires by the extension.
     */
    String[] dependencies() default {};

    /**
     * Marks whether or not the extension requires access to Native Minecraft Sources (NMS). If the platform does not
     * support NMS the extension will not be loaded.
     *
     * @return true if the extension requires NMS access.
     */
    boolean requiresNMS() default false;

    /**
     * The unique ID (UUID) of the extension. In the format of {@link java.util.UUID}. For example:
     * {@code 123e4567-e89b-12d3-a456-426614174000}
     *
     * @return the unique ID
     */
    String uniqueId();
}
