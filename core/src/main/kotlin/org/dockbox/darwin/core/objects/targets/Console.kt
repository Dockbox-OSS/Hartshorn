package org.dockbox.darwin.core.objects.targets

abstract class Console : CommandSource, PermissionHolder {

    override fun hasPermission(permission: String): Boolean = true

    override fun hasAnyPermission(vararg permissions: String): Boolean = true

    override fun hasAllPermissions(vararg permissions: String): Boolean = true

    override fun setPermission(permission: String) = Unit

    override fun setPermissions(vararg permissions: String) = Unit


}
