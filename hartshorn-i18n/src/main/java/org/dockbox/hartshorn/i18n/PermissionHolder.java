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

package org.dockbox.hartshorn.i18n;

import org.dockbox.hartshorn.core.domain.Identifiable;
import org.dockbox.hartshorn.core.domain.tuple.Tristate;
import org.dockbox.hartshorn.i18n.permissions.Permission;
import org.dockbox.hartshorn.i18n.permissions.PermissionContext;

@Deprecated(since = "4.2.3")
public interface PermissionHolder extends Identifiable {

    // An empty context targets only global permissions
    PermissionContext GLOBAL = PermissionContext.builder().build();

    @Deprecated(since = "4.2.3")
    PermissionContext activeContext();

    @Deprecated(since = "4.2.3")
    boolean hasPermission(String permission);

    @Deprecated(since = "4.2.3")
    boolean hasAnyPermission(String... permissions);

    @Deprecated(since = "4.2.3")
    boolean hasAllPermissions(String... permissions);

    @Deprecated(since = "4.2.3")
    boolean hasPermission(Permission permission);

    @Deprecated(since = "4.2.3")
    boolean hasAnyPermission(Permission... permissions);

    @Deprecated(since = "4.2.3")
    boolean hasAllPermissions(Permission... permissions);

    @Deprecated(since = "4.2.3")
    void permission(String permission, Tristate state);

    @Deprecated(since = "4.2.3")
    void permissions(Tristate state, String... permissions);

    @Deprecated(since = "4.2.3")
    void permission(Permission permission, Tristate state);

    @Deprecated(since = "4.2.3")
    void permissions(Tristate state, Permission... permissions);
}
