package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.i18n.Permission

interface PermissionHolder : Target {

    fun hasPermission(permission: String): Boolean
    fun hasAnyPermission(vararg permissions: String): Boolean
    fun hasAllPermissions(vararg permissions: String): Boolean

    fun hasPermission(permission: Permission): Boolean
    fun hasAnyPermission(vararg permissions: Permission): Boolean
    fun hasAllPermissions(vararg permissions: Permission): Boolean

    fun setPermission(permission: String)
    fun setPermissions(vararg permissions: String)

    fun setPermission(permission: Permission)
    fun setPermissions(vararg permissions: Permission)

}
