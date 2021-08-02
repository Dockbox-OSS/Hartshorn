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
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.events.annotations.Posting;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.position.BlockFace;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerInteractAirEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerInteractBlockEvent;
import org.dockbox.hartshorn.server.minecraft.events.player.interact.PlayerInteractEntityEvent;
import org.dockbox.hartshorn.server.minecraft.players.ClickType;
import org.dockbox.hartshorn.server.minecraft.players.Hand;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;

import java.util.Optional;

@Posting({PlayerInteractAirEvent.class, PlayerInteractEntityEvent.class, PlayerInteractBlockEvent.class})
public class InteractionEventBridge implements EventBridge {

    @Listener
    public void on(InteractEvent event) {
        if (event instanceof InteractEntityEvent || event instanceof InteractBlockEvent) return;

        ClickType type;
        if (event instanceof InteractItemEvent.Primary) type = ClickType.PRIMARY;
        else if (event instanceof InteractItemEvent.Secondary) type = ClickType.SECONDARY;
        else return;

        final Exceptional<Hand> hand = this.hand(event);
        if (hand.absent()) return;

        final Exceptional<Player> player = this.player(event);
        if (player.absent()) return;

        this.post(new PlayerInteractAirEvent(player.get(), hand.get(), type), event);
    }

    @Listener
    public void on(InteractEntityEvent event) {
        ClickType type;
        if (event instanceof InteractEntityEvent.Primary) type = ClickType.PRIMARY;
        else if (event instanceof InteractEntityEvent.Secondary) type = ClickType.SECONDARY;
        else return;

        Vector3N point = Vector3N.empty();
        if (event instanceof InteractEntityEvent.Secondary.At at) {
            point = SpongeConvert.fromSponge(at.interactionPoint());
        }

        final Entity entity = SpongeConvert.fromSponge(event.entity());

        final Exceptional<Player> player = this.player(event);
        if (player.absent()) return;

        this.post(new PlayerInteractEntityEvent(player.get(), entity, point, type), event);
    }

    @Listener
    public void on(InteractBlockEvent event) {
        ClickType type;
        if (event instanceof InteractBlockEvent.Primary) type = ClickType.PRIMARY;
        else if (event instanceof InteractBlockEvent.Secondary) type = ClickType.SECONDARY;
        else return;

        final BlockFace face = SpongeConvert.fromSponge(event.targetSide());
        final Block block = SpongeConvert.fromSponge(event.block());

        final Exceptional<Hand> hand = this.hand(event);
        if (hand.absent()) return;

        final Exceptional<Player> player = this.player(event);
        if (player.absent()) return;

        this.post(new PlayerInteractBlockEvent(player.get(), hand.get(), type, block, face), event);
    }

    private Exceptional<Hand> hand(Event event) {
        final Optional<HandType> handType = event.context().get(EventContextKeys.USED_HAND);
        if (handType.isEmpty()) return Exceptional.empty();

        final Hand hand = SpongeConvert.fromSponge(handType.get());
        return Exceptional.of(hand);
    }

}
