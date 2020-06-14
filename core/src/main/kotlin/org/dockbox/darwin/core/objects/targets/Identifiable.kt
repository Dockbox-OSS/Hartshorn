package org.dockbox.darwin.core.objects.targets

import java.util.*

open class Identifiable : Target {

    open lateinit var uniqueId: UUID
    open lateinit var name: String

}
