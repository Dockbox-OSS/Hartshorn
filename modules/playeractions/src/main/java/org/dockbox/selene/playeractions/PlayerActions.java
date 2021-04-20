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

import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.events.entity.SpawnSource;
import org.dockbox.selene.api.events.player.PlayerMoveEvent;
import org.dockbox.selene.api.events.player.PlayerTeleportEvent;
import org.dockbox.selene.api.events.player.interact.PlayerInteractEntityEvent;
import org.dockbox.selene.api.events.player.interact.PlayerSummonEntityEvent;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.PlotKeys;
import org.dockbox.selene.plots.PlotMembership;

import javax.inject.Inject;

@Module(id = "playeractions", name = "Player Actions", description = "Intercepts and verifies the validity of several player actions",
        authors = "GuusLieben")
public class PlayerActions {

    @Inject
    private PlayerActionConfiguration configuration;

    @Inject
    private Worlds worlds;

    @Listener
    public void on(PlayerTeleportEvent event) {
        this.verifySpectatorTeleportation(event);
        this.verifyPlotAccess(event);
    }

    private void verifySpectatorTeleportation(PlayerTeleportEvent event) {
        if (event.getTarget().getGamemode() == Gamemode.SPECTATOR) {
            if (event.getTarget().hasPermission(PlayerActionPermissions.SPECTATOR_BYPASS)) return;
            if (this.configuration.getTeleportWhitelist().contains(event.getOldLocation().getWorld().getName())) return;

            event.setCancelled(true);
            event.getTarget().sendWithPrefix(PlayerActionResources.SPECTATOR_TELEPORT_NOT_ALLOWED);
        }
    }

    private void verifyPlotAccess(PlayerTeleportEvent event) {
        Player player = event.getTarget();
        Location target = event.getNewLocation();
        Exceptional<Plot> plotTarget = target.get(PlotKeys.PLOT);

        if (plotTarget.absent()) return;
        Plot plot = plotTarget.get();
        if (plot.hasMembership(player, PlotMembership.DENIED)) {
            event.setCancelled(true);
            player.sendWithPrefix(PlayerActionResources.DENIED_FROM_TARGET_PLOT);
        }
    }

    @Listener
    public void on(PlayerInteractEntityEvent event) {
        if (event.getEntity() instanceof Player) return; // Allowed
        Player player = event.getTarget();
        event.setCancelled(this.cancelEvent(player, event.getEntity()));
    }

    @Listener
    public void on(PlayerSummonEntityEvent event) {
        Player player = event.getPlayer();
        SpawnSource source = event.getSource();
        if (SpawnSource.PLACEMENT.equals(source) || SpawnSource.SPAWN_EGG.equals(source)) {
            event.setCancelled(this.cancelEvent(player, event.getEntity()));
        }
    }

    private boolean cancelEvent(Player player, Entity entity) {
        Exceptional<Plot> targetPlot = entity.getLocation().get(PlotKeys.PLOT);
        if (targetPlot.absent()) {
            player.sendWithPrefix(PlayerActionResources.OUTSIDE_PLOT);
            return true;
        } else {
            Plot plot = targetPlot.get();
            if (!plot.hasAnyMembership(player, PlotMembership.MEMBER, PlotMembership.TRUSTED, PlotMembership.OWNER)) {
                player.sendWithPrefix(PlayerActionResources.CANNOT_INTERACT);
                return true;
            }
        }
        return false;
    }

    @Listener
    public void on(PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) return; // Allow players to teleport out of the world
        if (event.getTarget().hasPermission(PlayerActionPermissions.NAVIGATE_DEFAULT_WORLD)) return;

        if (event.getTarget().getWorld().getWorldUniqueId().equals(this.worlds.getRootWorldId())) {
            event.setCancelled(true);
            event.getTarget().send(PlayerActionResources.CANNOT_MOVE_HERE);
        }
    }

}
