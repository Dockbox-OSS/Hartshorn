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

package org.dockbox.selene.sponge.objects.inventory;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.objects.inventory.AbstractInventoryRow;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.common.item.inventory.query.operation.InventoryTypeQueryOperation;

import java.util.Collection;

public class SpongeInventoryRow extends AbstractInventoryRow implements SpongeInventory {

    private final SpongePlayer spongePlayer;

    public SpongeInventoryRow(SpongePlayerInventory inventory, int rowIndex, SpongePlayer spongePlayer) {
        super(rowIndex, inventory);
        this.spongePlayer = spongePlayer;
    }

    @Override
    public void setSlot(Item item, int index) {
        this.internalGetSlot(index).present(slot -> slot.set(SpongeConversionUtil.toSponge(item)));
    }

    @Override
    public Item getSlot(int index) {
        return this.internalGetSlot(index).map(SLOT_LOOKUP).get(AIR);
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(int index) {
        return this.internalGetRow().map(row -> row.getSlot(new SlotIndex(index)).orElse(null));
    }

    private Exceptional<? extends org.spongepowered.api.item.inventory.type.InventoryRow> internalGetRow() {
        return this.spongePlayer.getSpongePlayer().map(player -> {
            if (3 == this.getRowIndex()) {
                return player.getInventory()
                        .<Hotbar>query(new InventoryTypeQueryOperation(Hotbar.class));
            }
            else {
                MainPlayerInventory main = player.getInventory()
                        .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));
                return Exceptional.of(main.getRow(this.getRowIndex())).orNull();
            }
        });
    }

    @Override
    public Collection<Item> getAllItems() {
        return this.internalGetRow().map(this::getAllItems).get(SeleneUtils::emptyList);
    }

    @Override
    public boolean give(Item item) {
        return this.internalGetRow().map(row -> {
            ItemStack stack = SpongeConversionUtil.toSponge(item);
            return Type.SUCCESS == row.offer(stack).getType();
        }).or(false);
    }

}
