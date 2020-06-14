package org.dockbox.darwin.core.objects.location

import java.util.*

abstract class World(open var worldUniqueId: UUID, open var name: String) {

    abstract fun getPlayerCount(): Int
    abstract fun unload(): Boolean

}
