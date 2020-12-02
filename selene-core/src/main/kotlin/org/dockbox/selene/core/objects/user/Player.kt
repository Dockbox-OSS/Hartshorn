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

package org.dockbox.selene.core.objects.user

import com.boydti.fawe.`object`.FawePlayer
import java.util.*
import org.dockbox.selene.core.i18n.common.Language
import org.dockbox.selene.core.i18n.permissions.AbstractPermission
import org.dockbox.selene.core.objects.optional.Exceptional
import org.dockbox.selene.core.objects.targets.CommandSource
import org.dockbox.selene.core.objects.targets.Identifiable
import org.dockbox.selene.core.objects.targets.InventoryHolder
import org.dockbox.selene.core.objects.targets.Locatable
import org.dockbox.selene.core.objects.targets.MessageReceiver
import org.dockbox.selene.core.objects.targets.PermissionHolder
import org.dockbox.selene.core.text.Text

abstract class Player(uniqueId: UUID, name: String) : Identifiable<Player>(uniqueId, name), MessageReceiver, CommandSource, PermissionHolder, Locatable, InventoryHolder {

    abstract fun isOnline(): Boolean
    abstract fun getFawePlayer(): Exceptional<FawePlayer<*>>
    abstract fun kick(message: Text)
    abstract fun getGamemode(): Gamemode
    abstract fun setGamemode(gamemode: Gamemode)
    abstract fun getLanguage(): Language
    abstract fun setLanguage(lang: Language)

    override fun hasPermission(permission: AbstractPermission): Boolean {
        return hasPermission(permission.get())
    }

    override fun hasAnyPermission(vararg permissions: AbstractPermission): Boolean {
        for (permission in permissions) if (hasPermission(permission)) return true
        return false
    }

    override fun hasAllPermissions(vararg permissions: AbstractPermission): Boolean {
        for (permission in permissions) if (!hasPermission(permission)) return false
        return true
    }

    override fun setPermission(permission: AbstractPermission, value: Boolean) {
        setPermission(permission.get(), value)
    }

    override fun setPermissions(value: Boolean, vararg permissions: AbstractPermission) {
        for (permission in permissions) setPermission(permission, value)
    }
}
