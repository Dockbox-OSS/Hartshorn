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

package org.dockbox.selene.test.objects;

import org.dockbox.selene.api.i18n.permissions.AbstractPermission;
import org.dockbox.selene.api.objects.targets.PermissionHolder;
import org.dockbox.selene.api.util.SeleneUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class JUnitPermissionRegistry {

    private static final Map<UUID, Collection<AbstractPermission>> permissions = SeleneUtils.emptyConcurrentMap();

    public static void setPermission(PermissionHolder holder, AbstractPermission permission) {
        permissions.putIfAbsent(holder.getUniqueId(), SeleneUtils.emptyList());
        permissions.get(holder.getUniqueId()).add(permission);
    }

    public static void hasPermission(PermissionHolder holder, String permission) {
        // TODO
    }

    public static void hasPermission(PermissionHolder holder, AbstractPermission permission) {
        // TODO
    }
}
