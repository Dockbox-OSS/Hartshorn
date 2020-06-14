package org.dockbox.darwin.core.objects.user

import com.boydti.fawe.`object`.FawePlayer
import org.dockbox.darwin.core.objects.targets.CommandSource
import org.dockbox.darwin.core.objects.targets.Identifiable
import org.dockbox.darwin.core.objects.targets.MessageReceiver
import org.dockbox.darwin.core.objects.targets.PermissionHolder
import org.dockbox.darwin.core.text.Text
import java.util.*

abstract class Player : Identifiable(), MessageReceiver, CommandSource, PermissionHolder {

    abstract fun isOnline(): Boolean
    abstract fun getFawePlayer(): Optional<FawePlayer<*>>
    abstract fun kick(message: Text)
    abstract fun getGamemode(): Gamemode
    abstract fun setGamemode(gamemode: Gamemode)

}
