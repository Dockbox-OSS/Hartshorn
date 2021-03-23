/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.api.events.player;

import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.special.PortalType;
import org.dockbox.selene.api.objects.targets.Target;

public class PlayerPortalEvent extends PlayerTeleportEvent {
    private final PortalType portalType;
    private boolean usesPortal;

    public PlayerPortalEvent(Target target, Location oldLocation, Location newLocation, boolean usesPortal, PortalType portalType) {
        super(target, oldLocation, newLocation);
        this.usesPortal = usesPortal;
        this.portalType = portalType;
    }

    public boolean usesPortal() {
        return usesPortal;
    }

    public PortalType getPortalType() {
        return portalType;
    }

    public void setUsePortal(boolean usePortal) {
        this.usesPortal = usePortal;
    }
}
