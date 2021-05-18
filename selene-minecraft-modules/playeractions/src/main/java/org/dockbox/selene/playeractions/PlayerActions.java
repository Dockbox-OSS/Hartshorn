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

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.config.annotations.Value;
import org.dockbox.selene.plots.Plot;
import org.dockbox.selene.plots.PlotKeys;
import org.dockbox.selene.plots.PlotMembership;
import org.dockbox.selene.server.minecraft.dimension.Worlds;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.entities.Entity;
import org.dockbox.selene.server.minecraft.events.entity.SpawnSource;
import org.dockbox.selene.server.minecraft.events.player.PlayerMoveEvent;
import org.dockbox.selene.server.minecraft.events.player.PlayerTeleportEvent;
import org.dockbox.selene.server.minecraft.events.player.interact.PlayerInteractEntityEvent;
import org.dockbox.selene.server.minecraft.events.player.interact.PlayerSummonEntityEvent;
import org.dockbox.selene.server.minecraft.players.Gamemode;
import org.dockbox.selene.server.minecraft.players.Player;

import java.util.List;

import javax.inject.Inject;

@Module
public class PlayerActions {

    @Inject
    private Worlds worlds;
    @Inject
    private PlayerActionResources resources;

    @Value("modules.player-actions.whitelist")
    private List<String> whitelist;

    @Listener
    public void on(PlayerTeleportEvent event) {
        this.verifySpectatorTeleportation(event);
        this.verifyPlotAccess(event);
    }

    private void verifySpectatorTeleportation(PlayerTeleportEvent event) {
        if (event.getTarget().getGamemode() == Gamemode.SPECTATOR) {
            if (event.getTarget().hasPermission(PlayerActionPermissions.SPECTATOR_BYPASS)) return;
            if (this.whitelist.contains(event.getOldLocation().getWorld().getName())) return;

            event.setCancelled(true);
            event.getTarget().sendWithPrefix(this.resources.getSpectatorNotAllowed());
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
            player.sendWithPrefix(this.resources.getDeniedFromPlot());
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
            player.sendWithPrefix(this.resources.getOutsidePlot());
            return true;
        } else {
            Plot plot = targetPlot.get();
            if (!plot.hasAnyMembership(player, PlotMembership.MEMBER, PlotMembership.TRUSTED, PlotMembership.OWNER)) {
                player.sendWithPrefix(this.resources.getInteractionError());
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
            event.getTarget().send(this.resources.getMoveError());
        }
    }

}
