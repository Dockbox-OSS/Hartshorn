package org.dockbox.darwin.core.objects.events

import org.dockbox.darwin.core.objects.targets.Target

interface Targetable : Event {

    fun getTarget(): Target
    fun setTarget(target: Target)

}
