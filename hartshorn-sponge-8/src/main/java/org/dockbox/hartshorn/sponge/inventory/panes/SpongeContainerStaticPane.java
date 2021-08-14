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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.Map;
import java.util.function.BiConsumer;

public class SpongeContainerStaticPane extends SpongeStaticPane {

    private Container container;
    private final Map<Integer, ItemStack> delayed = HartshornUtils.emptyMap();

    public SpongeContainerStaticPane(final InventoryMenu menu, final InventoryType type,
                                     final BiConsumer<Player, StaticPane> onClose) {
        super(menu, type, onClose);
    }

    @Override
    public void open(final Player player) {
        final Exceptional<ServerPlayer> serverPlayer = SpongeConvert.toSponge(player);
        // Only open if the player is still online, it's possible the user logged off
        final Exceptional<Container> container = serverPlayer.map(this.menu::open)
                .map(o -> o.orElse(null));
        if (container.present()) {
            this.container = container.get();
            this.delayed.forEach((i, item) -> this.container.set(i, item));
        }
    }

    @Override
    public void set(final Item item, final int index) {
        this.delayed.put(index, SpongeConvert.toSponge(item));
    }

    @Override
    protected ViewableInventory inventory() {
        if (this.container != null) {
            final Exceptional<ViewableInventory> viewable = Exceptional.of(this.container.asViewable());
            if (viewable.present()) return viewable.get();
        }
        Hartshorn.log().warn("Attempted to access inventory while it was not loaded, this may cause issues");
        return super.inventory();
    }
}
