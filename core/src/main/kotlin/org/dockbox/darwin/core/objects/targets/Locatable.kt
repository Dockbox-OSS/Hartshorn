package org.dockbox.darwin.core.objects.targets

import org.dockbox.darwin.core.objects.location.Location

interface Locatable : Target {

    fun getLocation(): Location
    fun setLocation(location: Location)

}
