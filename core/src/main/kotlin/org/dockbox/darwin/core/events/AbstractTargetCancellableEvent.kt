package org.dockbox.darwin.core.events

import org.dockbox.darwin.core.objects.events.Cancellable
import org.dockbox.darwin.core.objects.events.Targetable
import org.dockbox.darwin.core.objects.targets.Target

abstract class AbstractTargetCancellableEvent(private var target: Target) : Cancellable, Targetable {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        isCancelled = cancelled
    }

    override fun getTarget(): Target {
        return target
    }

    override fun setTarget(target: Target) {
        this.target = target
    }

}
