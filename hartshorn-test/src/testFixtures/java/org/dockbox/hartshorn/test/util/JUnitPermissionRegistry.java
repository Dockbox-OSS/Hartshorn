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

package org.dockbox.hartshorn.test.util;

import org.dockbox.hartshorn.api.domain.tuple.Tristate;
import org.dockbox.hartshorn.api.i18n.PermissionHolder;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public final class JUnitPermissionRegistry {

    private static final Map<UUID, Collection<Permission>> permissions = HartshornUtils.emptyConcurrentMap();

    public static void permission(PermissionHolder holder, Permission permission) {
        permissions.putIfAbsent(holder.uniqueId(), HartshornUtils.emptyList());
        permissions.get(holder.uniqueId()).add(permission);
    }

    // Specific context
    public static boolean hasPermission(PermissionHolder holder, Permission permission) {
        if (permission.context().absent()) return hasPermission(holder, permission.get());
        for (Permission abstractPermission : permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
            if (abstractPermission.context().present() && abstractPermission.get().equals(permission.get())) {
                if (abstractPermission.context().get().equals(permission.context().get())) return true;
            }
        }
        return false;
    }

    // Global context
    public static boolean hasPermission(PermissionHolder holder, String permission) {
        for (Permission abstractPermission : permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
            if (abstractPermission.context().absent() && abstractPermission.get().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public static void permission(PermissionHolder holder, String permission, Tristate tristate) {
        if (tristate.booleanValue()) {
            permissions.get(holder.uniqueId()).add(Permission.of(permission));
        }
        else
            for (Permission abstractPermission : permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
                if (abstractPermission.get().equals(permission) && abstractPermission.context().absent()) {
                    permissions.get(holder.uniqueId()).remove(abstractPermission);
                }
            }
    }

    public static void permission(PermissionHolder holder, Permission permission, Tristate tristate) {
        if (tristate.booleanValue()) {
            permissions.get(holder.uniqueId()).add(permission);
        }
        else
            for (Permission abstractPermission : permissions.getOrDefault(holder.uniqueId(), HartshornUtils.emptyList())) {
                if (abstractPermission.equals(permission)) {
                    permissions.get(holder.uniqueId()).remove(abstractPermission);
                }
            }
    }

    private JUnitPermissionRegistry() {
    }
}
