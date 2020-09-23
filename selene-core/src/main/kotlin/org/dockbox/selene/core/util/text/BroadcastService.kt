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

package org.dockbox.selene.core.util.text

import org.dockbox.selene.core.i18n.permissions.Permission
import org.dockbox.selene.core.objects.user.Player
import org.dockbox.selene.core.text.Text
import java.util.function.Predicate

interface BroadcastService {

    fun broadcastPublic(message: Text)
    fun broadcastWithFilter(message: Text, filter: Predicate<Player>)

    fun broadcastForPermission(message: Text, permission: Permission)
    fun broadcastForPermission(message: Text, permission: String)
    fun broadcastForPermissionWithFilter(message: Text, permission: Permission, filter: Predicate<Player>)
    fun broadcastForPermissionWithFilter(message: Text, permission: String, filter: Predicate<Player>)

}
