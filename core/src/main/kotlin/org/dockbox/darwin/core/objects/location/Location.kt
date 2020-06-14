package org.dockbox.darwin.core.objects.location

import org.dockbox.darwin.core.objects.tuple.Vector3D

abstract class Location(open var vectorLoc: Vector3D, open var world: World)
