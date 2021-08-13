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

package org.dockbox.hartshorn.sponge.inventory.panes;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

import java.util.Map;
import java.util.Optional;

public class SpongeStaticPane implements StaticPane {

    private final InventoryMenu menu;
    private final Map<Integer, Element> listeningElements = HartshornUtils.emptyConcurrentMap();

    public SpongeStaticPane(final InventoryMenu menu) {
        this.menu = menu;
        this.menu.registerSlotClick((cause, container, slot, slotIndex, clickType) -> {
            if (this.listeningElements.containsKey(slotIndex)) {
                final Optional<ServerPlayer> player = cause.first(ServerPlayer.class);
                if (player.isEmpty()) return false;
                final Player origin = SpongeConvert.fromSponge(player.get());
                this.listeningElements.get(slotIndex).perform(origin);
            }
            return false;
        });
    }

    @Override
    public void open(final Player player) {
        final Exceptional<ServerPlayer> serverPlayer = SpongeConvert.toSponge(player);
        // Only open if the player is still online, it's possible the user logged off
        serverPlayer.present(this.menu::open);
    }

    @Override
    public void set(final Element element, final int index) {
        this.set(element.item(), index);
        if (element.listening()) this.listeningElements.put(index, element);
    }

    @Override
    public void set(final Item item, final int index) {
        this.menu.inventory().set(index, SpongeConvert.toSponge(item));
    }

    @Override
    public void update(final InventoryLayout layout) {
        layout.elements().forEach((index, element) -> this.set(element, index));
    }
}
