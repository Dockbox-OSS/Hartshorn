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

import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerTeleportEvent;
import org.dockbox.selene.api.objects.player.Gamemode;

@Module(id = "playeractions", name = "Player Actions", description = "Intercepts and verifies the validity of several player actions",
        authors = "GuusLieben")
public class PlayerActions {

    /*
        TODO:
         - Gamemode check on teleport
           - Blacklist of locations where the check should be skipped
           - Skip if on plot with gamemode: Spectator
         - Teleport player if moving in root world (unless permitted)
         - Teleport player if denied on a (P2 managed) world
         - Check plot on entity spawning
         - Check plot on entity interaction
     */

    @Listener
    public static void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTarget().getGamemode() != Gamemode.SPECTATOR) return;
        if (event.getTarget().hasPermission(PlayerActionPermissions.SPECTATOR_BYPASS)) return;
        event.setCancelled(true);
        event.getTarget().sendWithPrefix(PlayerActionResources.SPECTATOR_TELEPORT_NOT_ALLOWED);
    }

}
