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

package org.dockbox.selene.core.server;

import org.dockbox.selene.core.util.SeleneUtils;

import java.util.List;
import java.util.UUID;

public final class SeleneInformation {

    public static final String GLOBAL_BYPASS = "selene.admin.bypass-all";
    public static final String PACKAGE_PREFIX = "org.dockbox.selene";
    public static final List<UUID> GLOBALLY_PERMITTED = SeleneUtils.COLLECTION.asList(
            UUID.fromString("6047d264-7769-4e50-a11e-c8b83f65ccc4"),
            UUID.fromString("cb6411bb-31c9-4d69-8000-b98842ce0a0a"),
            UUID.fromString("b7fb5e32-73ee-4f25-b256-a763c8739192")
    );
    /**
     * Constant value holding the GitHub username(s) of the author(s) of {@link Selene}. This does not include names of
     * extension developers.
     */
    public static final String[] AUTHORS = {"GuusLieben"};

    private SeleneInformation() {}
}
