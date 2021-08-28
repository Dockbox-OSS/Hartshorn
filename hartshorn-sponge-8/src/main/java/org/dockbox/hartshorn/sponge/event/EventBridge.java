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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.events.parents.Cancellable;
import org.dockbox.hartshorn.events.parents.Event;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.SpongeContextCarrier;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import java.util.Optional;

import javax.inject.Inject;

import lombok.Getter;

public abstract class EventBridge implements SpongeContextCarrier {

    @Inject
    @Getter
    private ApplicationContext context;

    protected void post(final Event event, final org.spongepowered.api.event.Event cause) {
        final Event posted = event.with(this.applicationContext()).post();
        if (posted instanceof Cancellable cancellable && cause instanceof org.spongepowered.api.event.Cancellable causeCancellable) {
            if (cancellable.cancelled()) causeCancellable.setCancelled(true);
        }
    }

    protected Exceptional<Player> player(final org.spongepowered.api.event.Event event) {
        final Optional<ServerPlayer> serverPlayer = event.cause().first(ServerPlayer.class);
        if (serverPlayer.isEmpty()) return Exceptional.empty();

        final Player player = SpongeAdapter.fromSponge(serverPlayer.get());
        return Exceptional.of(player);
    }

}
