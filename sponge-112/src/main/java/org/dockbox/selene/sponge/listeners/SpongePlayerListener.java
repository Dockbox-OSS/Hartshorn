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
import org.dockbox.selene.core.events.moderation.BanEvent.IpBannedEvent;
import org.dockbox.selene.core.events.moderation.BanEvent.IpUnbannedEvent;
import org.dockbox.selene.core.events.moderation.BanEvent.NameBannedEvent;
import org.dockbox.selene.core.events.moderation.BanEvent.NameUnbannedEvent;
import org.dockbox.selene.core.events.moderation.BanEvent.PlayerBannedEvent;
import org.dockbox.selene.core.events.moderation.BanEvent.PlayerUnbannedEvent;
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
import org.spongepowered.api.event.network.BanIpEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.PardonIpEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.PardonUserEvent;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.World;

import io.github.nucleuspowered.nucleus.api.events.NucleusMuteEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusNameBanEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusNoteEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusWarnEvent;
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
    public void onPlayerAuthenticating(ClientConnectionEvent.Auth authEvent,
                                       @Getter("getConnection") RemoteConnection connection) {
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
    public void onPlayerTeleport(MoveEntityEvent.Teleport teleportEvent,
                                 @First Player player,
                                 @Getter("getFromTransform") Transform<World> from,
                                 @Getter("getToTransform") Transform<World> to) {
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
    public void onPlayerChat(MessageChannelEvent.Chat chatEvent,
                             @First Player player,
                             @Getter("getMessage") Text message) {
        Cancellable event = new SendChatEvent(
                SpongeConversionUtil.fromSponge(player),
                SpongeConversionUtil.fromSponge(chatEvent.getMessage())
        );
        this.bus.post(event);
        chatEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onPlayerBanned(BanUserEvent event,
                               @First Player player,
                               @Getter("getBan") Ban.Profile profile,
                               @Getter("getSource") Object source
    ) {
        PlayerBannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onIPBanned(BanIpEvent event,
                           @Getter("getBan") Ban.Ip profile,
                           @Getter("getSource") Object source
    ) {
        IpBannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onNameBanned(NucleusNameBanEvent.Banned event,
                             @Getter("getEntry") String name,
                             @Getter("getReason") String reason,
                             @Getter("getSource") Object source
    ) {
        NameBannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onPlayerWarned(NucleusWarnEvent.Warned event,
                               @Getter("getTargetUser") User user,
                               @Getter("getReason") String reason,
                               @Getter("getSource") Object source
    ) {
        // TODO GuusLieben, implement
    }

    @Listener
    public void onPlayerNoted(NucleusNoteEvent.Created event,
                              @Getter("getTargetUser") User user,
                              @Getter("getNote") String note,
                              @Getter("getSource") Object source
    ) {
        // TODO GuusLieben, implement
    }

    @Listener
    public void onPlayerMuted(NucleusMuteEvent.Muted event,
                              @Getter("getTargetUser") User user,
                              @Getter("getReason") Text reason,
                              @Getter("getSource") Object source
    ) {
        // TODO GuusLieben, MultiChat replaces this event. Look into Bungee hooking if possible
    }

    @Listener
    public void onPlayerUnbanned(PardonUserEvent event,
                                 @First Player player,
                                 @Getter("getBan") Ban.Profile profile,
                                 @Getter("getSource") Object source
    ) {
        PlayerUnbannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onIPUnbanned(PardonIpEvent event,
                             @Getter("getBan") Ban.Ip profile,
                             @Getter("getSource") Object source
    ) {
        IpUnbannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onNameUnbanned(NucleusNameBanEvent.Unbanned event,
                               @Getter("getEntry") String name,
                               @Getter("getReason") String reason,
                               @Getter("getSource") Object source
    ) {
        NameUnbannedEvent selene; // TODO GuusLieben, implement
    }

    @Listener
    public void onWarnExpired(NucleusWarnEvent.Expired event,
                              @Getter("getTargetUser") User user,
                              @Getter("getReason") String reason,
                              @Getter("getSource") Object source
    ) {
        // TODO GuusLieben, implement
    }

    @Listener
    public void onMuteExpired(NucleusMuteEvent.Unmuted event,
                              @Getter("getTargetUser") User user,
                              @Getter("getSource") Object source
    ) {
        // TODO GuusLieben, MultiChat replaces this event. Look into Bungee hooking if possible
    }

}
