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

import org.dockbox.selene.core.PlatformConversionService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.inventory.InventoryRow;
import org.dockbox.selene.core.objects.inventory.PlayerInventory;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;
import org.spongepowered.common.item.inventory.query.operation.InventoryTypeQueryOperation;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpongePlayerInventory extends PlayerInventory {

    private static final int inventorySize = 36;
    private static final Supplier<Item> air = () -> Selene.getItems().getAir();
    private static final Function<org.spongepowered.api.item.inventory.Slot, Item> slotLookup = slot -> {
        return slot.peek().map(PlatformConversionService::map)
            .map(Item.class::cast)

            .orElseGet(air);
    };

    private final SpongePlayer player;

    public SpongePlayerInventory(SpongePlayer player) {
        this.player = player;
    }

    @Override
    public Item getSlot(int row, int column) {
        return this.internalGetSlot(row, column)
            .map(slotLookup)
            .orElseGet(air);
    }

    @Override
    public Item getSlot(int index) {
        return this.internalGetSlot(index)
            .map(slotLookup)
            .orElseGet(air);
    }

    @Override
    public Item getSlot(Slot slot) {
        return this.internalGetSlot(slot)
            .map(slotLookup)
            .orElseGet(air);
    }

    @Override
    public void setSlot(Item item, int row, int column) {
        this.internalGetSlot(row, column).ifPresent(slot -> {
            slot.set(PlatformConversionService.map(item));
        });
    }

    @Override
    public void setSlot(Item item, int index) {
        this.internalGetSlot(index).ifPresent(slot -> {
            slot.set(PlatformConversionService.map(item));
        });
    }

    @Override
    public void setSlot(Item item, Slot slotType) {
        this.internalGetSlot(slotType).ifPresent(slot -> {
            slot.set(PlatformConversionService.map(item));
        });
    }

    @Override
    public Collection<Item> getAllItems() {
        return this.player.getSpongePlayer().map(player -> {
            return StreamSupport.stream(player.getInventory().slots().spliterator(), false)
                .filter(inventory -> inventory instanceof org.spongepowered.api.item.inventory.Slot)
                .map(slot -> (org.spongepowered.api.item.inventory.Slot) slot)
                .map(slotLookup)
                .collect(Collectors.toList());
        }).orElseGet(SeleneUtils::emptyList);
    }

    @Override
    public boolean give(Item item) {
        return this.player.getSpongePlayer().map(player -> {
            MainPlayerInventory inventory = player.getInventory().query(new InventoryTypeQueryOperation(MainPlayerInventory.class));
            ItemStack stack = PlatformConversionService.map(item);
            InventoryTransactionResult result = inventory.getHotbar().offer(stack);
            if (Type.SUCCESS == result.getType()) return true;

            return Type.SUCCESS == inventory.offer(stack).getType();
        }).orElse(false);
    }

    @Override
    public int capacity() {
        return inventorySize;
    }

    @Override
    public Exceptional<InventoryRow> getRow(int index) {
        if (4 >= index) {
            return Exceptional.of(new SpongeInventoryRow(this, index, this.player));
        }
        return Exceptional.empty();
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(int row, int column) {
        return this.player.getSpongePlayer().map(player -> {
            if (3 > row) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                    .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid()
                    .getSlot(column, row)
                    .orElse(null);

            } else if (3 == row) { // Hotbar
                Hotbar hotbar = player.getInventory()
                    .query(new InventoryTypeQueryOperation(Hotbar.class));
                return hotbar.getSlot(new SlotIndex(column))
                    .orElse(null);

            } else throw new IllegalArgumentException("Slot index [row=" + row + ", col=" + column + "] is out of bounds (row: 0-3, col: 0-8)");
        });
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(int index) {
        final int gridSize = 27;
        return this.player.getSpongePlayer().map(player -> {
            if (gridSize > index) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                    .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid()
                    .getSlot(new SlotIndex(index))
                    .orElse(null);

            } else if (inventorySize > index) { // Hotbar
                Hotbar hotbar = player.getInventory()
                    .query(new InventoryTypeQueryOperation(Hotbar.class));
                // -27 to correct for the grid gap (main grid is excluded once we get the Hotbar inventory, and is 3x9 slots)
                return hotbar.getSlot(new SlotIndex(index - gridSize))
                    .orElse(null);

            } else throw new IllegalArgumentException("Slot index " + index + " is out of bounds (0-35)");
        });
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(Slot slot) {
        return this.player.getSpongePlayer().map(player -> {
            EquipmentInventory equipment = player.getInventory()
                .query(new InventoryTypeQueryOperation(EquipmentInventory.class));
            EquipmentType equipmentType = PlatformConversionService.map(slot);
            return equipment.getSlot(equipmentType).orElse(null);
        });
    }
}
