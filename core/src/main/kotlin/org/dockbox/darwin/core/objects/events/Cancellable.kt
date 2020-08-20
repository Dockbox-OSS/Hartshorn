package org.dockbox.darwin.core.objects.events

interface Cancellable : Event {

    fun isCancelled(): Boolean
    fun setCancelled(cancelled: Boolean)

}
