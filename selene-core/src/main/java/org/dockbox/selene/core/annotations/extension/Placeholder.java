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
 * The interface to mark a extension which is currently not yet implemented, but is scheduled for future implementation.
 * This is only used for reference for developers of Selene, and is therefore not available at compile/runtime.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Placeholder {
    /**
     * The description of the scheduled extension.
     *
     * @return the string
     */
    String description();

    /**
     * The reporter of the extension.
     *
     * @return the string
     */
    String by();

    /**
     * The person who has been assigned to implement the extension.
     *
     * @return the string
     */
    String assignee();

    /**
     * The <a href="https://github.com/GuusLieben/Selene/issues">GitHub issue</a> reporting the scheduled extension.
     *
     * @return the int
     */
    int issue() default -1;
}
