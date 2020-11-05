/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.sponge.listeners;

import com.google.inject.Inject;

import org.dockbox.selene.core.events.chat.SendChatEvent;
import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerAuthEvent;
import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerJoinEvent;
import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerLeaveEvent;
import org.dockbox.selene.core.events.player.PlayerMoveEvent.PlayerSwitchWorldEvent;
import org.dockbox.selene.core.events.player.PlayerMoveEvent.PlayerTeleportEvent;
import org.dockbox.selene.core.events.player.PlayerMoveEvent.PlayerWarpEvent;
import org.dockbox.selene.core.objects.events.Cancellable;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.Warp;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import io.github.nucleuspowered.nucleus.api.events.NucleusWarpEvent;

public class SpongePlayerListener {

    @Inject
    private EventBus bus;

    @Listener
    public void onPlayerConnected(ClientConnectionEvent.Join joinEvent, @First Player sp) {
        Event event = new PlayerJoinEvent(SpongeConversionUtil.fromSponge(sp));
        this.bus.post(event);
    }

    @Listener
    public void onPlayerDisconnected(ClientConnectionEvent.Disconnect disconnectEvent, @First Player sp) {
        Event event = new PlayerLeaveEvent(SpongeConversionUtil.fromSponge(sp));
        this.bus.post(event);
    }

    @Listener
    public void onPlayerAuthenticating(ClientConnectionEvent.Auth authEvent, @Getter(
            "getConnection") RemoteConnection connection) {
        Event event = new PlayerAuthEvent(connection.getAddress(), connection.getVirtualHost());
        this.bus.post(event);
    }

    @Listener
    public void onPlayerWarp(NucleusWarpEvent.Use warpEvent, @Getter("getTargetUser") User user) {
        Warp warp = SpongeConversionUtil.fromSponge(warpEvent.getWarp());
        Cancellable event = new PlayerWarpEvent(
                SpongeConversionUtil.fromSponge(user),
                warp
        );
        this.bus.post(event);
        warpEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport teleportEvent, @First Player player, @Getter(
            "getFromTransform") Transform<World> from, @Getter("getToTransform") Transform<World> to) {
        Location fromLocation = SpongeConversionUtil.fromSponge(from.getLocation());
        Location toLocation = SpongeConversionUtil.fromSponge(to.getLocation());

        Cancellable event = new PlayerTeleportEvent(
                SpongeConversionUtil.fromSponge(player),
                fromLocation, toLocation);
        this.bus.post(event);
        teleportEvent.setCancelled(event.isCancelled());

        if (!fromLocation.getWorld().equals(toLocation.getWorld()) && !event.isCancelled()) {
            Cancellable worldEvent = new PlayerSwitchWorldEvent(
                    SpongeConversionUtil.fromSponge(player),
                    fromLocation.getWorld(), toLocation.getWorld());
            this.bus.post(worldEvent);

            teleportEvent.setCancelled(worldEvent.isCancelled());
        }
    }

    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat chatEvent, @First Player player, @Getter("getMessage") Text message) {
        Cancellable event = new SendChatEvent(
                SpongeConversionUtil.fromSponge(player),
                SpongeConversionUtil.fromSponge(chatEvent.getMessage())
        );
        this.bus.post(event);
        chatEvent.setCancelled(event.isCancelled());
    }

}
