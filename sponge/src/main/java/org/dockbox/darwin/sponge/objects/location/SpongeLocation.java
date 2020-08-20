package org.dockbox.darwin.sponge.objects.location;

import org.dockbox.darwin.core.objects.location.Location;
import org.dockbox.darwin.core.objects.location.World;
import org.dockbox.darwin.core.objects.tuple.Vector3D;
import org.jetbrains.annotations.NotNull;

public class SpongeLocation extends Location {
    public SpongeLocation(@NotNull Vector3D vectorLoc, @NotNull World world) {
        super(vectorLoc, world);
    }
}
