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

package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.UUID;

public final class HartshornInformation {

    public static final String GLOBAL_BYPASS = "hartshorn.admin.bypass-all";
    public static final String GLOBAL_PERMITTED = "hartshorn.global.permitted";
    public static final String PACKAGE_PREFIX = "org.dockbox.hartshorn";
    public static final List<UUID> GLOBALLY_PERMITTED = HartshornUtils.asList(
            UUID.fromString("6047d264-7769-4e50-a11e-c8b83f65ccc4"),
            UUID.fromString("cb6411bb-31c9-4d69-8000-b98842ce0a0a"),
            UUID.fromString("b7fb5e32-73ee-4f25-b256-a763c8739192")
    );
    public static final String PROJECT_NAME = "Hartshorn";
    public static final String PROJECT_ID = "hartshorn";

    private HartshornInformation() {}
}
