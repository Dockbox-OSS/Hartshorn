package org.dockbox.darwin.core.util.player

import org.dockbox.darwin.core.objects.user.Player
import java.util.*

interface PlayerStorageService {

    fun getOnlinePlayers(): List<Player>
    fun getPlayer(uuid: UUID): Optional<Player>

}
