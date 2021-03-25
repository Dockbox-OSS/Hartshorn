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

package org.dockbox.selene.test.util;

import org.dockbox.selene.api.i18n.permissions.AbstractPermission;
import org.dockbox.selene.api.objects.targets.PermissionHolder;
import org.dockbox.selene.api.objects.tuple.Tristate;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.i18n.Permission;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class JUnitPermissionRegistry {

    private static final Map<UUID, Collection<AbstractPermission>> permissions = SeleneUtils.emptyConcurrentMap();

    public static void setPermission(PermissionHolder holder, AbstractPermission permission) {
        permissions.putIfAbsent(holder.getUniqueId(), SeleneUtils.emptyList());
        permissions.get(holder.getUniqueId()).add(permission);
    }

    // Specific context
    public static boolean hasPermission(PermissionHolder holder, AbstractPermission permission) {
        if (permission.getContext().isAbsent()) return hasPermission(holder, permission.get());
        for (AbstractPermission abstractPermission : permissions.getOrDefault(holder.getUniqueId(), SeleneUtils.emptyList())) {
            if (abstractPermission.getContext().isPresent() && abstractPermission.get().equals(permission.get())) {
                if (abstractPermission.getContext().get().equals(permission.getContext().get())) return true;
            }
        }
        return false;
    }

    // Global context
    public static boolean hasPermission(PermissionHolder holder, String permission) {
        for (AbstractPermission abstractPermission : permissions.getOrDefault(holder.getUniqueId(), SeleneUtils.emptyList())) {
            if (abstractPermission.getContext().isAbsent() && abstractPermission.get().equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public static void setPermission(PermissionHolder holder, String permission, Tristate tristate) {
        if (tristate.booleanValue()) {
            permissions.get(holder.getUniqueId()).add(new Permission(permission));
        }
        else
            for (AbstractPermission abstractPermission : permissions.getOrDefault(holder.getUniqueId(), SeleneUtils.emptyList())) {
                if (abstractPermission.get().equals(permission) && abstractPermission.getContext().isAbsent()) {
                    permissions.get(holder.getUniqueId()).remove(abstractPermission);
                }
            }
    }

    public static void setPermission(PermissionHolder holder, AbstractPermission permission, Tristate tristate) {
        if (tristate.booleanValue()) {
            permissions.get(holder.getUniqueId()).add(permission);
        }
        else
            for (AbstractPermission abstractPermission : permissions.getOrDefault(holder.getUniqueId(), SeleneUtils.emptyList())) {
                if (abstractPermission.equals(permission)) {
                    permissions.get(holder.getUniqueId()).remove(abstractPermission);
                }
            }
    }

}
