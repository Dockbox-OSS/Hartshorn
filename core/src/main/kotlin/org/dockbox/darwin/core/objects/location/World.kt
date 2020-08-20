package org.dockbox.darwin.core.objects.location

import org.dockbox.darwin.core.util.uuid.UUIDUtil
import java.util.*

abstract class World(open var worldUniqueId: UUID, open var name: String) {

    abstract fun getPlayerCount(): Int
    abstract fun unload(): Boolean

    companion object {
        val empty: World = EmptyWorld()
    }

    private class EmptyWorld : World(UUIDUtil.empty, "EMPTY") {
        override fun getPlayerCount(): Int = 0
        override fun unload(): Boolean = true
    }
}
