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

package org.dockbox.hartshorn.playeractions;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.events.annotations.Listener;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.i18n.text.actions.ClickAction;
import org.dockbox.hartshorn.i18n.text.actions.HoverAction;
import org.dockbox.hartshorn.playersettings.PlayerSettings;
import org.dockbox.hartshorn.regions.RegionMembership;
import org.dockbox.hartshorn.regions.plots.Plot;
import org.dockbox.hartshorn.regions.plots.PlotKeys;
import org.dockbox.hartshorn.server.minecraft.DefaultServerResources;
import org.dockbox.hartshorn.server.minecraft.dimension.Worlds;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.events.entity.SpawnSource;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerMoveEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerSettingsChangedEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerTeleportEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerInteractEntityEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerSummonEntityEvent;
import org.dockbox.hartshorn.server.minecraft.players.Gamemode;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.List;

import javax.inject.Inject;

@Service
public class PlayerActions {

    @Inject
    private Worlds worlds;
    @Inject
    private PlayerActionResources resources;
    @Inject
    private DefaultServerResources serverResources;
    @Value("services.player-actions.whitelist")
    private List<String> whitelist;

    @Listener
    public void on(final PlayerTeleportEvent event) {
        this.verifySpectatorTeleportation(event);
        this.verifyPlotAccess(event);
    }

    private void verifySpectatorTeleportation(final PlayerTeleportEvent event) {
        if (event.subject().gamemode() == Gamemode.SPECTATOR) {
            if (event.subject().hasPermission("hartshorn.playeractions.bypass.spectator")) return;
            if (this.whitelist.contains(event.origin().world().name())) return;

            event.cancelled(true);
            event.subject().sendWithPrefix(this.resources.spectatorNotAllowed());
        }
    }

    private void verifyPlotAccess(final PlayerTeleportEvent event) {
        final Player player = event.subject();
        final Location target = event.destination();
        final Exceptional<Plot> plotTarget = target.get(PlotKeys.PLOT);

        if (plotTarget.absent()) return;
        final Plot plot = plotTarget.get();
        if (plot.hasMembership(player, RegionMembership.DENIED)) {
            event.cancelled(true);
            player.sendWithPrefix(this.resources.deniedFromPlot());
        }
    }

    @Listener
    public void on(final PlayerInteractEntityEvent event) {
        if (event.entity() instanceof Player) return; // Allowed
        final Player player = event.subject();
        event.cancelled(this.cancelEvent(player, event.entity()));
    }

    private boolean cancelEvent(final Player player, final Entity entity) {
        final Exceptional<Plot> targetPlot = entity.location().get(PlotKeys.PLOT);
        if (targetPlot.absent()) {
            player.sendWithPrefix(this.resources.outsidePlot());
            return true;
        }
        else {
            final Plot plot = targetPlot.get();
            if (!plot.hasAnyMembership(player, RegionMembership.MEMBER, RegionMembership.TRUSTED, RegionMembership.OWNER)) {
                player.sendWithPrefix(this.resources.interactionError());
                return true;
            }
        }
        return false;
    }

    @Listener
    public void on(final PlayerSummonEntityEvent event) {
        final Player player = event.player();
        final SpawnSource source = event.source();
        if (SpawnSource.PLACEMENT.equals(source) || SpawnSource.SPAWN_EGG.equals(source)) {
            event.cancelled(this.cancelEvent(player, event.entity()));
        }
    }

    @Listener
    public void on(final PlayerMoveEvent event) {
        if (event instanceof PlayerTeleportEvent) return; // Allow players to teleport out of the world
        if (event.subject().hasPermission("hartshorn.playeractions.navigate")) return;

        if (event.subject().world().worldUniqueId().equals(this.worlds.rootUniqueId())) {
            event.cancelled(true);
            event.subject().send(this.resources.moveError());
        }
    }

    @Listener
    public void on(final PlayerSettingsChangedEvent event) {
        final Player target = event.subject();

        final Boolean receiving = PlayerSettings.RECEIVING_NOTIFICATIONS.get(target);

        if (receiving) {
            final Language settingsLanguage = event.settings().language();
            final Language preferenceLanguage = target.language();

            if (!settingsLanguage.equals(preferenceLanguage)) {
                final Text notification = this.resources.languageNotification(settingsLanguage.nameLocalized())
                        .translate(target)
                        .asText();

                notification.onClick(ClickAction.executeCallback(t -> {
                    target.language(settingsLanguage);
                    target.send(this.serverResources
                            .languageUpdated(settingsLanguage.nameLocalized())
                    );
                }));

                notification.onHover(HoverAction.showText(this.resources
                        .languageNotificationHover(settingsLanguage.nameLocalized())
                        .translate(target)
                        .asText())
                );
                target.sendWithPrefix(notification);
            }
        }
    }

}
