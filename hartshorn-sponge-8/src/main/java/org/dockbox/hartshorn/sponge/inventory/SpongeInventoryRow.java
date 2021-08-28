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

package org.dockbox.hartshorn.sponge.inventory;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.inventory.AbstractInventoryRow;
import org.dockbox.hartshorn.sponge.game.SpongePlayer;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;

import java.util.Collection;

public class SpongeInventoryRow extends AbstractInventoryRow implements SpongeInventory {

    private final SpongePlayer player;

    public SpongeInventoryRow(final SpongePlayerInventory inventory, final int rowIndex, final SpongePlayer player) {
        super(rowIndex, inventory);
        this.player = player;
    }

    @Override
    public void slot(final Item item, final int index) {
        this.internalGetSlot(index).present(slot -> slot.set(SpongeAdapter.toSponge(item)));
    }

    @Override
    public Item slot(final int index) {
        return this.internalGetSlot(index).map(SLOT_LOOKUP).get(() -> AIR.apply(this.applicationContext()));
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(final int index) {
        return this.internalGetRow().map(row -> row.slot(index).orElse(null));
    }

    private Exceptional<? extends org.spongepowered.api.item.inventory.type.InventoryRow> internalGetRow() {
        return this.player.player().map(player -> {
            if (3 == this.rowIndex()) return player.inventory().hotbar();
            else return player.inventory().storage().row(this.rowIndex()).orElse(null);
        });
    }

    @Override
    public Collection<Item> items() {
        return this.internalGetRow().map(this::items).get(HartshornUtils::emptyList);
    }

    @Override
    public boolean give(final Item item) {
        return this.internalGetRow().map(row -> {
            final ItemStack stack = SpongeAdapter.toSponge(item);
            return Type.SUCCESS == row.offer(stack).type();
        }).or(false);
    }

}
