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

package org.dockbox.hartshorn.api.i18n;

import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.i18n.permissions.Permission;
import org.dockbox.hartshorn.api.i18n.permissions.PermissionContext;

public interface PermissionHolder extends Identifiable {

    // An empty context targets only global permissions
    PermissionContext GLOBAL = PermissionContext.builder().build();

    PermissionContext activeContext();

    boolean hasPermission(String permission);

    boolean hasAnyPermission(String... permissions);

    boolean hasAllPermissions(String... permissions);

    boolean hasPermission(Permission permission);

    boolean hasAnyPermission(Permission... permissions);

    boolean hasAllPermissions(Permission... permissions);

    void permission(String permission, org.dockbox.hartshorn.api.domain.tuple.Tristate state);

    void permissions(org.dockbox.hartshorn.api.domain.tuple.Tristate state, String... permissions);

    void permission(Permission permission, org.dockbox.hartshorn.api.domain.tuple.Tristate state);

    void permissions(org.dockbox.hartshorn.api.domain.tuple.Tristate state, Permission... permissions);
}
