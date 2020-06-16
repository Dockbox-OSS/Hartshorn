package org.dockbox.darwin.core.events

import org.dockbox.darwin.core.objects.events.Targetable
import org.dockbox.darwin.core.objects.targets.Target

abstract class AbstractTargetEvent(private var target: Target) : Targetable {
    override fun getTarget(): Target {
        return target
    }

    override fun setTarget(target: Target) {
        this.target = target
    }

}
