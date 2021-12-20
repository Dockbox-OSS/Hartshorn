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

package org.dockbox.hartshorn.core.boot;

/**
 * The utility type to grant easy access to project metadata.
 *
 * @author Guus Lieben
 * @since 4.0.0
 */
public final class Hartshorn {

    /**
     * The default package prefix to use when scanning Hartshorn internals.
     */
    public static final String PACKAGE_PREFIX = "org.dockbox.hartshorn";
    /**
     * The (human-readable) display name of Hartshorn.
     */
    public static final String PROJECT_NAME = "Hartshorn";
    /**
     * The simplified identifier for Hartshorn-default identifiers.
     */
    public static final String PROJECT_ID = "hartshorn";
    /**
     * The semantic version of the current/latest release of Hartshorn
     */
    public static final String VERSION = "4.2.5";

    private Hartshorn() {}
}

