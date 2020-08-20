package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.Permission

abstract class Console : CommandSource, PermissionHolder {

    override fun hasPermission(permission: String): Boolean = true

    override fun hasAnyPermission(vararg permissions: String): Boolean = true

    override fun hasAllPermissions(vararg permissions: String): Boolean = true

    override fun hasPermission(permission: Permission): Boolean = true

    override fun hasAnyPermission(vararg permissions: Permission): Boolean = true

    override fun hasAllPermissions(vararg permissions: Permission): Boolean = true

    override fun setPermission(permission: String) = Unit

    override fun setPermissions(vararg permissions: String) = Unit

    override fun setPermission(permission: Permission) = Unit

    override fun setPermissions(vararg permissions: Permission) = Unit

}
