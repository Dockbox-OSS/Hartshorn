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
import org.dockbox.selene.core.server.Selene
import org.dockbox.selene.core.text.Text
import org.dockbox.selene.core.util.player.PlayerStorageService
import java.util.function.Consumer
import java.util.function.Predicate

class SimpleBroadcastService : BroadcastService {

    override fun broadcastPublic(message: Text) =
            Selene.getInstance(PlayerStorageService::class.java).getOnlinePlayers().forEach(Consumer { receivers: Player? -> message.send(receivers!!) })

    override fun broadcastWithFilter(message: Text, filter: Predicate<Player>) = sendWithPredicate(message, filter)

    override fun broadcastForPermission(message: Text, permission: Permission) =
            sendWithPredicate(message, Predicate { p: Player -> p.hasPermission(permission) })

    override fun broadcastForPermission(message: Text, permission: String) =
            sendWithPredicate(message, Predicate { p: Player -> p.hasPermission(permission) })

    override fun broadcastForPermissionWithFilter(message: Text, permission: Permission, filter: Predicate<Player>) =
            sendWithPredicate(message, Predicate { p: Player -> filter.test(p) && p.hasPermission(permission) })

    override fun broadcastForPermissionWithFilter(message: Text, permission: String, filter: Predicate<Player>) =
            sendWithPredicate(message, Predicate { p: Player -> filter.test(p) && p.hasPermission(permission) })

    private fun sendWithPredicate(message: Text, filter: Predicate<Player>) =
            Selene.getInstance(PlayerStorageService::class.java).getOnlinePlayers().stream().filter(filter).forEach { receivers: Player? -> message.send(receivers!!) }
}
