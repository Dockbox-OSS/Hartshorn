package org.dockbox.darwin.core.objects.user

import com.boydti.fawe.`object`.FawePlayer
import org.dockbox.darwin.core.objects.targets.*
import org.dockbox.darwin.core.text.Text
import java.util.*

abstract class Player(uniqueId: UUID, name: String) : Identifiable(uniqueId, name), MessageReceiver, CommandSource, PermissionHolder, Locatable {

    abstract fun isOnline(): Boolean
    abstract fun getFawePlayer(): Optional<FawePlayer<*>>
    abstract fun kick(message: Text)
    abstract fun getGamemode(): Gamemode
    abstract fun setGamemode(gamemode: Gamemode)

}
