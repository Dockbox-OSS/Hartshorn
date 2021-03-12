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

package org.dockbox.selene.worldmanagement;

import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerPortalEvent;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.properties.InjectableType;
import org.dockbox.selene.api.server.properties.InjectorProperty;

@Module(id = "worldmanagement", name = "World Management", description = "Manages several aspects of the various worlds on a server", authors = "GuusLieben")
public class WorldManagement implements InjectableType {

    private WorldManagementConfig config;

    @Listener
    public void onPortalUse(PlayerPortalEvent event) {
        if (event.usesPortal() && event.getNewLocation().getWorld().getName().equals(config.getPortalWorldTarget())) {
            event.setUsePortal(false);
            event.setNewLocation(new Location(config.getPortalPosition(), event.getNewLocation().getWorld()));
        }
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        config = Selene.provide(WorldManagementConfig.class);
    }
}
