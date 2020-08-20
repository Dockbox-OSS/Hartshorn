package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.objects.location.Location
import org.dockbox.darwin.core.objects.location.World

interface Locatable : Target {

    fun getLocation(): Location
    fun setLocation(location: Location)
    fun getWorld(): World

}
