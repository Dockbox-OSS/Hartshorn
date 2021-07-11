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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.events.annotations.Posting;
import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.events.moderation.IpBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.IpUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.KickEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.NameBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.NameUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerBannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerNotedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerUnbannedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerWarnedEvent;
import org.dockbox.hartshorn.server.minecraft.events.moderation.PlayerWarningExpired;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.player.KickPlayerEvent;
import org.spongepowered.api.event.network.BanIpEvent;
import org.spongepowered.api.event.network.PardonIpEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.PardonUserEvent;
import org.spongepowered.api.service.ban.Ban.IP;
import org.spongepowered.api.service.ban.Ban.Profile;

import java.net.InetAddress;
import java.time.LocalDateTime;

@SuppressWarnings("DuplicatedCode")
@Posting(value = {
        // Ban events
        PlayerBannedEvent.class,
        IpBannedEvent.class,
        NameBannedEvent.class,
        // Unban events
        PlayerUnbannedEvent.class,
        IpUnbannedEvent.class,
        NameUnbannedEvent.class,
        // Remaining events
        KickEvent.class,
        PlayerWarnedEvent.class,
        PlayerWarningExpired.class,
        PlayerNotedEvent.class
})
public class ModerationEventBridge implements EventBridge {

    @Listener
    public void on(BanUserEvent event) {
        final Player player = SpongeConvert.toSponge(event.user());
        final Profile ban = event.ban();
        final Exceptional<LocalDateTime> expiration = Exceptional.of(ban.expirationDate()).map(LocalDateTime::from);
        final LocalDateTime creation = LocalDateTime.from(ban.creationDate());
        final Exceptional<String> reason = Exceptional.of(ban.reason()).map(SpongeConvert::fromSponge).map(Text::toPlain);
        this.post(new PlayerBannedEvent(player, null, reason, expiration, creation), event);
    }

    @Listener
    public void on(BanIpEvent event) {
        final IP ban = event.ban();
        final InetAddress address = ban.address();
        final Exceptional<LocalDateTime> expiration = Exceptional.of(ban.expirationDate()).map(LocalDateTime::from);
        final LocalDateTime creation = LocalDateTime.from(ban.creationDate());
        final Exceptional<String> reason = Exceptional.of(ban.reason()).map(SpongeConvert::fromSponge).map(Text::toPlain);
        this.post(new IpBannedEvent(address, null, reason, expiration, creation), event);
    }

    @Listener
    public void on(PardonUserEvent event) {
        final Player player = SpongeConvert.toSponge(event.user());
        final Profile ban = event.ban();
        final Exceptional<LocalDateTime> expiration = Exceptional.of(ban.expirationDate()).map(LocalDateTime::from);
        final LocalDateTime creation = LocalDateTime.from(ban.creationDate());
        final Exceptional<String> reason = Exceptional.of(ban.reason()).map(SpongeConvert::fromSponge).map(Text::toPlain);
        this.post(new PlayerUnbannedEvent(player, null, reason, creation), event);
    }

    @Listener
    public void on(PardonIpEvent event) {
        final IP ban = event.ban();
        final InetAddress address = ban.address();
        final Exceptional<LocalDateTime> expiration = Exceptional.of(ban.expirationDate()).map(LocalDateTime::from);
        final LocalDateTime creation = LocalDateTime.from(ban.creationDate());
        final Exceptional<String> reason = Exceptional.of(ban.reason()).map(SpongeConvert::fromSponge).map(Text::toPlain);
        this.post(new IpUnbannedEvent(address, null, reason, creation), event);
    }

    @Listener
    public void on(KickPlayerEvent event) {
        final Player player = SpongeConvert.fromSponge(event.player());
        final Exceptional<String> reason = Exceptional.of(event.message()).map(SpongeConvert::fromSponge).map(Text::toPlain);
        this.post(new KickEvent(player, null, reason), event);
    }

    /**
     * Placeholder for NameBannedEvent NameUnbannedEvent, PlayerWarnedEvent, PlayerWarningExpired, and PlayerNotedEvent
     * @param event The event
     */
    public void on(Void event) {
        throw new NotImplementedException();
    }

}
