package org.dockbox.selene.core.external.region;

import org.dockbox.selene.core.objects.tuple.Vector3N;

public class Clipboard {

    private final Region region;
    private final Vector3N origin;

    public Clipboard(Region region, Vector3N origin) {
        this.region = region;
        this.origin = origin;
    }

    public Region getRegion() {
        return this.region;
    }

    public Vector3N getOrigin() {
        return this.origin;
    }
}
