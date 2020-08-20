package org.dockbox.darwin.core.util.events

import org.dockbox.darwin.core.objects.events.Event
import java.lang.invoke.MethodHandles

interface EventBus {

    fun subscribe(`object`: Any)
    fun subscribe(`object`: Any, lookup: MethodHandles.Lookup)
    fun unsubscribe(`object`: Any)
    fun post(event: Event)

}
