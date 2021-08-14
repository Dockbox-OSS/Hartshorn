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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.context.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpongeStaticPane implements StaticPane {

    protected final InventoryMenu menu;
    private final InventoryType type;
    private final Multimap<Integer, Function<ClickContext, Boolean>> listeners = ArrayListMultimap.create();

    public SpongeStaticPane(final InventoryMenu menu, final InventoryType type, final BiConsumer<Player, StaticPane> onClose) {
        this.menu = menu;
        this.type = type;
        this.menu.registerSlotClick((cause, container, slot, slotIndex, clickType) -> {
            if (this.listeners.containsKey(slotIndex)) {
                final Optional<ServerPlayer> player = cause.first(ServerPlayer.class);
                if (player.isEmpty()) return false;

                final Player origin = SpongeConvert.fromSponge(player.get());
                final Item item = Exceptional.of(slot.peekAt(0))
                        .map(SpongeConvert::fromSponge)
                        .map(Item.class::cast)
                        .orElse(ItemTypes.AIR::item)
                        .get();

                final ClickContext context = new ClickContext(origin, item, this);
                boolean permit = true;
                for (final Function<ClickContext, Boolean> function : this.listeners.get(slotIndex)) {
                    if (!function.apply(context)) permit = false;
                }
                return permit;
            }
            return true;
        });
        this.menu.registerClose((cause, container) -> {
            final Optional<ServerPlayer> player = cause.first(ServerPlayer.class);
            if (player.isEmpty()) return;
            final Player origin = SpongeConvert.fromSponge(player.get());
            onClose.accept(origin, this);
        });
    }

    @Override
    public void open(final Player player) {
        final Exceptional<ServerPlayer> serverPlayer = SpongeConvert.toSponge(player);
        // Only open if the player is still online, it's possible the user logged off
        serverPlayer.present(this.menu::open);
    }

    @Override
    public void onClick(final int index, final Function<ClickContext, Boolean> onClick) {
        this.listeners.put(index, onClick);
    }

    @Override
    public void close(final Player player) {
        SpongeConvert.toSponge(player).present(ServerPlayer::closeInventory);
    }

    @Override
    public void set(final Element element, final int index) {
        this.set(element.item(), index);
        if (element.listening()) this.listeners.put(index, element::perform);
    }

    @Override
    public void set(final Item item, final int index) {
        this.inventory().set(index, SpongeConvert.toSponge(item));
    }

    @Override
    public Exceptional<Item> get(final int index) {
        return Exceptional.of(this.inventory().peekAt(index))
                .map(SpongeConvert::fromSponge);
    }

    @Override
    public Exceptional<Item>  output() {
        if (this.type.hasOutput()) {
            final int slot = this.type.size() - 1;
            if (slot == -1)
                return Exceptional.of(new IllegalStateException("Unexpected inventory type with output, but no slots: %s".formatted(this.type.name())));
            else return this.get(slot);
        }
        return Exceptional.empty();
    }

    @Override
    public void update(final InventoryLayout layout) {
        layout.elements().forEach((index, element) -> this.set(element, index));
    }

    protected ViewableInventory inventory() {
        return this.menu.inventory();
    }
}
