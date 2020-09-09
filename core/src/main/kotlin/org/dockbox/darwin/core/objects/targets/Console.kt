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

package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.permissions.Permission

abstract class Console : CommandSource, PermissionHolder {

    override fun hasPermission(permission: String): Boolean = true

    override fun hasAnyPermission(vararg permissions: String): Boolean = true

    override fun hasAllPermissions(vararg permissions: String): Boolean = true

    override fun hasPermission(permission: Permission): Boolean = true

    override fun hasAnyPermission(vararg permissions: Permission): Boolean = true

    override fun hasAllPermissions(vararg permissions: Permission): Boolean = true

    override fun setPermission(permission: String, value: Boolean) = Unit

    override fun setPermissions(value: Boolean, vararg permissions: String) = Unit

    override fun setPermission(permission: Permission, value: Boolean) = Unit

    override fun setPermissions(value: Boolean, vararg permissions: Permission) = Unit

}
