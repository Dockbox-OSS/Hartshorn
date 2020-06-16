package org.dockbox.darwin.core.objects.targets

import java.util.*

open class Identifiable(open var uniqueId: UUID, open var name: String) : Target {

}
