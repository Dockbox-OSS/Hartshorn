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

import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerAuthEvent;
import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerJoinEvent;
import org.dockbox.selene.core.events.player.PlayerConnectionEvent.PlayerLeaveEvent;
import org.dockbox.selene.core.events.player.PlayerMoveEvent.PlayerWarpEvent;
import org.dockbox.selene.core.objects.events.Event;
import org.dockbox.selene.core.objects.location.Warp;
import org.dockbox.selene.core.util.events.EventBus;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.RemoteConnection;

import io.github.nucleuspowered.nucleus.api.events.NucleusWarpEvent;

public class SpongePlayerListener {

    @Inject
    private EventBus bus;

    @Listener
    public void onPlayerConnected(ClientConnectionEvent.Join joinEvent) {
        Player sp = joinEvent.getTargetEntity();
        Event event = new PlayerJoinEvent(SpongeConversionUtil.fromSponge(sp).orElse(null));
        this.bus.post(event);
    }

    @Listener
    public void onPlayerDisconnected(ClientConnectionEvent.Disconnect disconnectEvent) {
        Player sp = disconnectEvent.getTargetEntity();
        Event event = new PlayerLeaveEvent(SpongeConversionUtil.fromSponge(sp).orElse(null));
        this.bus.post(event);
    }

    @Listener
    public void onPlayerAuthenticating(ClientConnectionEvent.Auth authEvent) {
        RemoteConnection connection = authEvent.getConnection();
        Event event = new PlayerAuthEvent(connection.getAddress(), connection.getVirtualHost());
        this.bus.post(event);
    }

    @Listener
    public void onPlayerWarp(NucleusWarpEvent.Use warpEvent) {
        Warp warp = SpongeConversionUtil.fromSponge(warpEvent.getWarp());
        User user = warpEvent.getTargetUser();
        Event event = new PlayerWarpEvent(
                new SpongePlayer(user.getUniqueId(), user.getName()),
                warp
        );
        this.bus.post(event);
    }

}
