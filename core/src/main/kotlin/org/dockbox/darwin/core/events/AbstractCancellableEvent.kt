package org.dockbox.darwin.core.events

import org.dockbox.darwin.core.objects.events.Cancellable

abstract class AbstractCancellableEvent : Cancellable {
    private var isCancelled = false
    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        isCancelled = cancelled
    }
}
