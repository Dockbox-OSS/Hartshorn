package org.dockbox.darwin.core.util.text

import org.dockbox.darwin.core.i18n.Permission
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.server.Server
import org.dockbox.darwin.core.text.Text
import org.dockbox.darwin.core.util.player.PlayerStorageService
import java.util.function.Consumer
import java.util.function.Predicate

class SimpleBroadcastService : BroadcastService {

    override fun broadcastPublic(message: Text) =
            Server.getInstance(PlayerStorageService::class.java).getOnlinePlayers().forEach(Consumer { receivers: Player? -> message.send(receivers!!) })

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
            Server.getInstance(PlayerStorageService::class.java).getOnlinePlayers().stream().filter(filter).forEach { receivers: Player? -> message.send(receivers!!) }
}
