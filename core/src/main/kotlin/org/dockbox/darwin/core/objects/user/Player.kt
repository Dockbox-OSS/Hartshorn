package org.dockbox.darwin.core.objects.user

import com.boydti.fawe.`object`.FawePlayer
import org.dockbox.darwin.core.i18n.Permission
import org.dockbox.darwin.core.objects.targets.*
import org.dockbox.darwin.core.text.Text
import java.util.*

abstract class Player(uniqueId: UUID, name: String) : Identifiable(uniqueId, name), MessageReceiver, CommandSource, PermissionHolder, Locatable {

    abstract fun isOnline(): Boolean
    abstract fun getFawePlayer(): Optional<FawePlayer<*>>
    abstract fun kick(message: Text)
    abstract fun getGamemode(): Gamemode
    abstract fun setGamemode(gamemode: Gamemode)

    override fun hasPermission(permission: Permission): Boolean {
        return hasPermission(permission.getValue())
    }

    override fun hasAnyPermission(vararg permissions: Permission): Boolean {
        for (permission in permissions) if (hasPermission(permission)) return true
        return false
    }

    override fun hasAllPermissions(vararg permissions: Permission): Boolean {
        for (permission in permissions) if (!hasPermission(permission)) return false
        return true
    }

    override fun setPermission(permission: Permission) {
        setPermission(permission.getValue())
    }

    override fun setPermissions(vararg permissions: Permission) {
        for (permission in permissions) setPermission(permission)
    }
}
