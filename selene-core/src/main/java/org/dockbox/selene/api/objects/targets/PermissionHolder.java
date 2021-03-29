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

package org.dockbox.selene.api.objects.targets;

import org.dockbox.selene.api.i18n.permissions.Permission;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;

public interface PermissionHolder extends Identifiable {

    // An none context targets only global permissions
    PermissionContext GLOBAL = PermissionContext.builder().build();

    PermissionContext activeContext();

    boolean hasPermission(String permission);

    boolean hasAnyPermission(String... permissions);

    boolean hasAllPermissions(String... permissions);

    boolean hasPermission(Permission permission);

    boolean hasAnyPermission(Permission... permissions);

    boolean hasAllPermissions(Permission... permissions);

    void setPermission(String permission, org.dockbox.selene.api.objects.tuple.Tristate state);

    void setPermissions(org.dockbox.selene.api.objects.tuple.Tristate state, String... permissions);

    void setPermission(Permission permission, org.dockbox.selene.api.objects.tuple.Tristate state);

    void setPermissions(org.dockbox.selene.api.objects.tuple.Tristate state, Permission... permissions);
}
