package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.location.Location;

public interface EntityFactory {

    ArmorStand armorStand(Location location);

    ItemFrame itemFrame(Location location);

}
