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

package org.dockbox.hartshorn.sponge.event;

import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.server.minecraft.events.packet.PacketEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerAuthEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerJoinEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerLeaveEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.PlayerSettingsChangedEvent;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.server.minecraft.players.SimpleGameSettings;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.player.PlayerChangeClientSettingsEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Auth;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Disconnect;
import org.spongepowered.api.event.network.ServerSideConnectionEvent.Join;

import java.net.InetSocketAddress;

@Posting(value = {
        PlayerAuthEvent.class,
        PlayerJoinEvent.class,
        PlayerLeaveEvent.class,
        PacketEvent.class,
        PlayerSettingsChangedEvent.class
})
public class ConnectionEventBridge implements EventBridge {

    @Listener
    public void on(Auth event) {
        final InetSocketAddress address = event.connection().address();
        final InetSocketAddress host = event.connection().virtualHost();
        this.post(new PlayerAuthEvent(address, host), event);
    }

    @Listener
    public void on(Join event) {
        final Player player = SpongeConvert.fromSponge(event.player());
        this.post(new PlayerJoinEvent(player), event);
    }

    @Listener
    public void on(Disconnect event) {
        final Player player = SpongeConvert.fromSponge(event.player());
        this.post(new PlayerLeaveEvent(player), event);
    }

    @Listener
    public void on(PlayerChangeClientSettingsEvent event) {
        final Player player = SpongeConvert.fromSponge(event.player());
        this.post(new PlayerSettingsChangedEvent(player, new SimpleGameSettings(Language.of(event.locale()))), event);
    }

    /**
     * Placeholder for PacketEvent
     * @param event The event
     */
    public void on(Void event) {
        throw new NotImplementedException();
    }

}
