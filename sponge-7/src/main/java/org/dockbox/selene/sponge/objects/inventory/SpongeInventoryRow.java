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
import org.dockbox.selene.api.objects.inventory.InventoryRow;
import org.dockbox.selene.api.objects.inventory.PlayerInventory;
import org.dockbox.selene.api.objects.inventory.Slot;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.common.item.inventory.query.operation.InventoryTypeQueryOperation;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpongeInventoryRow implements InventoryRow {

    private static final Supplier<Item> air = () -> Selene.getItems().getAir();
    private static final Function<org.spongepowered.api.item.inventory.Slot, Item> slotLookup =
            slot -> {
                return slot.peek()
                        .map(SpongeConversionUtil::fromSponge)
                        .map(referencedItem -> (Item) referencedItem)
                        .orElseGet(air);
            };

    private final SpongePlayerInventory inventory;
    private final int rowIndex;
    private final SpongePlayer spongePlayer;

    public SpongeInventoryRow(
            SpongePlayerInventory inventory, int rowIndex, SpongePlayer spongePlayer) {
        this.inventory = inventory;
        this.rowIndex = rowIndex;
        this.spongePlayer = spongePlayer;
    }

    @Override
    public Item getSlot(int row, int column) {
        if (row != this.rowIndex) return Selene.getItems().getAir();
        return this.getSlot(column);
    }

    @Override
    public Item getSlot(Slot slot) {
        return Selene.getItems().getAir();
    }

    @Override
    public void setSlot(Item item, int row, int column) {
        if (row != this.rowIndex) return;
        this.setSlot(item, column);
    }

    @Override
    public void setSlot(Item item, int index) {
        this.internalGetSlot(index).ifPresent(slot -> slot.set(SpongeConversionUtil.toSponge(item)));
    }

    @Override
    public void setSlot(Item item, Slot slot) {
        // Nothing happens
    }

    @Override
    public Collection<Item> getAllItems() {
        return this.internalGetRow()
                .map(
                        row ->
                                StreamSupport.stream(row.slots().spliterator(), false)
                                        .map(slot -> (org.spongepowered.api.item.inventory.Slot) slot)
                                        .map(slotLookup)
                                        .collect(Collectors.toList()))
                .orElseGet(SeleneUtils::emptyList);
    }

    @Override
    public boolean give(Item item) {
        return this.internalGetRow()
                .map(
                        row -> {
                            ItemStack stack = SpongeConversionUtil.toSponge(item);
                            return Type.SUCCESS == row.offer(stack).getType();
                        })
                .orElse(false);
    }

    @Override
    public int capacity() {
        return 9;
    }

    @Override
    public Item getSlot(int index) {
        return this.internalGetSlot(index).map(slotLookup).orElseGet(air);
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(int index) {
        return this.internalGetRow().map(row -> row.getSlot(new SlotIndex(index)).orElse(null));
    }

    private Exceptional<? extends org.spongepowered.api.item.inventory.type.InventoryRow>
    internalGetRow() {
        return this.spongePlayer
                .getSpongePlayer()
                .map(
                        player -> {
                            if (3 == this.rowIndex) {
                                return player
                                        .getInventory()
                                        .<Hotbar>query(new InventoryTypeQueryOperation(Hotbar.class));
                            }
                            else {
                                MainPlayerInventory main =
                                        player
                                                .getInventory()
                                                .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));
                                return Exceptional.of(main.getRow(this.rowIndex)).orNull();
                            }
                        });
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }
}
