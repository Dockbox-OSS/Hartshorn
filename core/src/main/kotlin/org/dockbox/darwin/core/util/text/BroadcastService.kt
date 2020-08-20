package org.dockbox.darwin.core.util.text

import org.dockbox.darwin.core.i18n.Permission
import org.dockbox.darwin.core.objects.user.Player
import org.dockbox.darwin.core.text.Text
import java.util.function.Predicate

interface BroadcastService {

    fun broadcastPublic(message: Text)
    fun broadcastWithFilter(message: Text, filter: Predicate<Player>)

    fun broadcastForPermission(message: Text, permission: Permission)
    fun broadcastForPermission(message: Text, permission: String)
    fun broadcastForPermissionWithFilter(message: Text, permission: Permission, filter: Predicate<Player>)
    fun broadcastForPermissionWithFilter(message: Text, permission: String, filter: Predicate<Player>)

}
