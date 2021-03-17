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

package org.dockbox.selene.playeractions;

import org.dockbox.selene.api.WorldStorageService;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.events.player.PlayerMoveEvent;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerTeleportEvent;
import org.dockbox.selene.api.objects.player.Gamemode;

import javax.inject.Inject;

@Module(id = "playeractions", name = "Player Actions", description = "Intercepts and verifies the validity of several player actions",
        authors = "GuusLieben")
public class PlayerActions {

    @Inject
    private PlayerActionConfiguration configuration;

    @Inject
    private WorldStorageService worldStorageService;

    /*
        TODO: // P2 Dependency
         - Teleport player if denied on a (P2 managed) world
         - Check plot on entity spawning
         - Check plot on entity interaction
     */

    @Listener
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTarget().getGamemode() != Gamemode.SPECTATOR) return;
        if (event.getTarget().hasPermission(PlayerActionPermissions.SPECTATOR_BYPASS)) return;
        if (configuration.getTeleportWhitelist().contains(event.getOldLocation().getWorld().getName())) return;

        event.setCancelled(true);
        event.getTarget().sendWithPrefix(PlayerActionResources.SPECTATOR_TELEPORT_NOT_ALLOWED);
    }

    @Listener
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) return; // Allow players to teleport out of the world
        if (event.getTarget().hasPermission(PlayerActionPermissions.NAVIGATE_DEFAULT_WORLD)) return;

        if (event.getTarget().getWorld().getWorldUniqueId().equals(worldStorageService.getRootWorldId())) {
            event.setCancelled(true);
        }
    }

}
