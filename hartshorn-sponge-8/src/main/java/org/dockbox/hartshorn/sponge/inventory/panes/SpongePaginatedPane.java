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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.context.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SpongePaginatedPane implements PaginatedPane {

    protected final InventoryMenu menu;
    private final InventoryType type;
    private final Map<Integer, Function<PaginatedPane, Element>> actions;
    private final Multimap<Integer, Function<ClickContext, Boolean>> listeners = ArrayListMultimap.create();
    private final Map<Integer, List<Element>> pages = HartshornUtils.emptyConcurrentMap();
    private int page = 0;

    public SpongePaginatedPane(final InventoryMenu menu, final InventoryType type, final BiConsumer<Player, PaginatedPane> onClose, final Map<Integer, Function<PaginatedPane, Element>> actions) {
        this.menu = menu;
        this.type = type;
        this.actions = actions;

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
                final List<Function<ClickContext, Boolean>> functions = HartshornUtils.asUnmodifiableList(this.listeners.get(slotIndex));
                for (final Function<ClickContext, Boolean> function : functions) {
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

    private void registerActions() {
        for (final Entry<Integer, Function<PaginatedPane, Element>> entry : this.actions.entrySet()) {
            final int index = entry.getKey();
            final Element element = entry.getValue().apply(this);

            // Imagine a 3x9 inventory, the total size will be 27. The amount of
            // columns is 9, so size - columns = 27 - 9 = 18. 18 will be the first
            // index on the last row. As indices are counted from zero, we can then
            // simply add the index to the row offset to get the right results.
            // Visualized inventory:
            // 0  1  2  3  4  5  6  7  8
            // 9  10 11 12 13 14 15 16 17
            // 18 19 20 21 22 23 24 25 26
            final int offset = this.type.size() - this.type.columns();

            this.menu.inventory().set(offset + index, SpongeConvert.toSponge(element.item()));
            this.onClick(offset + index, element::perform);
        }
    }

    @Override
    public void open(final Player player, final int page) {
        final Exceptional<ServerPlayer> serverPlayer = SpongeConvert.toSponge(player);
        if (serverPlayer.present() && this.pages.containsKey(page)) {
            this.listeners.clear();
            this.page = page;

            final List<Element> elements = this.pages.get(page);

            for (int i = 0; i < elements.size(); i++) {
                final Element element = elements.get(i);
                final Item item = element.item();

                this.menu.inventory().set(i, SpongeConvert.toSponge(item));
                if (element.listening()) this.onClick(i, element::perform);
            }

            this.registerActions();

            final ServerPlayer spongePlayer = serverPlayer.get();

            final Exceptional<Container> open = Exceptional.of(spongePlayer.openInventory());
            if (open.absent() || !open.get().containsInventory(this.menu.inventory()))
                this.menu.open(spongePlayer);
        }
    }

    @Override
    public void elements(final Collection<Element> elements) {
        final int capacity = this.type.size() - this.type.columns();
        final List<List<Element>> pages = Lists.partition(HartshornUtils.asList(elements), capacity);
        for (int i = 0; i < pages.size(); i++) {
            this.pages.put(i, pages.get(i));
        }
    }

    @Override
    public int page() {
        return this.page;
    }

    @Override
    public int pages() {
        return this.pages.size();
    }

    @Override
    public void open(final Player player) {
        this.open(player, 0);
    }

    @Override
    public void onClick(final int index, final Function<ClickContext, Boolean> onClick) {
        this.listeners.put(index, onClick);
    }

    @Override
    public void close(final Player player) {
        SpongeConvert.toSponge(player).present(ServerPlayer::closeInventory);
    }
}
