package org.dockbox.darwin.core.events.player

import org.dockbox.darwin.core.objects.events.Targetable
import org.dockbox.darwin.core.objects.targets.Target

abstract class PlayerConnectionEvent(private val target: Target) : Targetable {

    override fun getTarget(): Target {
        return this.target
    }

    override fun setTarget(target: Target) {
        throw UnsupportedOperationException("Cannot change target of connection event")
    }

    class Join(target: Target) : PlayerConnectionEvent(target)
    class Leave(target: Target) : PlayerConnectionEvent(target)
    class Ping(target: Target) : PlayerConnectionEvent(target)

}
