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

package org.dockbox.selene.core.objects.targets

import org.dockbox.selene.core.i18n.common.ResourceEntry
import org.dockbox.selene.core.i18n.entry.IntegratedResource
import org.dockbox.selene.core.i18n.permissions.AbstractPermission
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.text.Text

/**
 * mid-level Console class containing non-implementation specific logic for permissions and messages.
 *
 */
abstract class Console : CommandSource, PermissionHolder {

    override fun hasPermission(permission: String): Boolean = true

    override fun hasAnyPermission(vararg permissions: String): Boolean = true

    override fun hasAllPermissions(vararg permissions: String): Boolean = true

    override fun hasPermission(permission: AbstractPermission): Boolean = true

    override fun hasAnyPermission(vararg permissions: AbstractPermission): Boolean = true

    override fun hasAllPermissions(vararg permissions: AbstractPermission): Boolean = true

    override fun setPermission(permission: String, value: Boolean) = Unit

    override fun setPermissions(value: Boolean, vararg permissions: String) = Unit

    override fun setPermission(permission: AbstractPermission, value: Boolean) = Unit

    override fun setPermissions(value: Boolean, vararg permissions: AbstractPermission) = Unit

    override fun send(text: ResourceEntry) {
        val formattedValue = IntegratedResource.parse(text.getValue(Selene.getServer().globalConfig.getDefaultLanguage()))
        send(formattedValue)
    }

    override fun send(text: CharSequence) {
        text.split("\n").forEach { send(Text.of(it)) }
    }

    override fun sendWithPrefix(text: ResourceEntry) {
        sendWithPrefix(text.getValue(Selene.getServer().globalConfig.getDefaultLanguage()))
    }

    override fun sendWithPrefix(text: CharSequence) {
        text.split("\n").forEach { sendWithPrefix(Text.of(it)) }
    }

}
