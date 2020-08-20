package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.util.uuid.UUIDUtil
import java.util.*

open class Identifiable(open var uniqueId: UUID, open var name: String) : Target {

    object None : Identifiable(UUIDUtil.empty, "None")

}
