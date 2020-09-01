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

package org.dockbox.darwin.core.objects.user

import com.boydti.fawe.`object`.FawePlayer
import org.dockbox.darwin.core.i18n.Languages
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
    abstract fun getLanguage(): Languages
    abstract fun setLanguage(lang: Languages)

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

    override fun setPermission(permission: Permission, value: Boolean) {
        setPermission(permission.getValue(), value)
    }

    override fun setPermissions(value: Boolean, vararg permissions: Permission) {
        for (permission in permissions) setPermission(permission, value)
    }
}
