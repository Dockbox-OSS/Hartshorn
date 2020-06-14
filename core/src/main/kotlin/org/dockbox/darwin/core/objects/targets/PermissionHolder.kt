package org.dockbox.darwin.core.objects.targets

interface PermissionHolder : Target {

    fun hasPermission(permission: String): Boolean
    fun hasAnyPermission(vararg permissions: String): Boolean
    fun hasAllPermissions(vararg permissions: String): Boolean

    fun setPermission(permission: String)
    fun setPermissions(vararg permissions: String)

}
