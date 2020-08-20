package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.Permission

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
