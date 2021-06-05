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

package org.dockbox.hartshorn.sponge.objects.inventory;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.inventory.AbstractInventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.InventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.sponge.objects.targets.SpongePlayer;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
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

public class SpongePlayerInventory extends PlayerInventory implements SpongeInventory {

    private static final int inventorySize = 36;
    private final SpongePlayer player;

    public SpongePlayerInventory(SpongePlayer player) {
        this.player = player;
    }

    @Override
    public Item getSlot(int row, int column) {
        return this.internalGetSlot(row, column).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    @Override
    public void setSlot(Item item, int row, int column) {
        this.internalGetSlot(row, column).present(slot -> slot.set(SpongeConversionUtil.toSponge(item)));
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(
            int row, int column) {
        return this.player.getSpongePlayer().map(player -> {
            if (3 > row) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                        .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid().getSlot(column, row).orElse(null);

            }
            else if (3 == row) { // Hotbar
                Hotbar hotbar = player.getInventory().query(new InventoryTypeQueryOperation(Hotbar.class));
                return hotbar.getSlot(new SlotIndex(column)).orElse(null);

            }
            else throw new IllegalArgumentException("Slot index [row=" + row + ", col=" + column + "] is out of bounds (row: 0-3, col: 0-8)");
        });
    }

    @Override
    public Item getSlot(Slot slot) {
        return this.internalGetSlot(slot).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    @Override
    public void setSlot(Item item, Slot slotType) {
        this.internalGetSlot(slotType).present(slot -> slot.set(SpongeConversionUtil.toSponge(item)));
    }

    @Override
    public int capacity() {
        return inventorySize;
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(Slot slot) {
        return this.player.getSpongePlayer().map(player -> {
            EquipmentInventory equipment = ((org.spongepowered.api.item.inventory.entity.PlayerInventory) player.getInventory()).getEquipment();
            EquipmentType equipmentType = SpongeConversionUtil.toSponge(slot);
            return equipment.getSlot(equipmentType).orElse(null);
        });
    }

    @Override
    public void setSlot(Item item, int index) {
        this.internalGetSlot(index).present(slot -> slot.set(SpongeConversionUtil.toSponge(item)));
    }

    @Override
    public Item getSlot(int index) {
        return this.internalGetSlot(index).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(int index) {
        final int gridSize = 27;
        return this.player.getSpongePlayer().map(player -> {
            if (gridSize > index) { // Main inventory
                MainPlayerInventory main = player.getInventory()
                        .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));

                return main.getGrid().getSlot(new SlotIndex(index)).orElse(null);

            }
            else if (inventorySize > index) { // Hotbar
                Hotbar hotbar = player.getInventory().query(new InventoryTypeQueryOperation(Hotbar.class));

                // -27 to correct for the grid gap (main grid is excluded once we get the Hotbar
                // inventory, and is 3x9 slots)
                return hotbar.getSlot(new SlotIndex(index - gridSize)).orElse(null);

            }
            else throw new IllegalArgumentException("Slot index " + index + " is out of bounds (0-35)");
        });
    }

    @Override
    public Collection<Item> getAllItems() {
        return this.player.getSpongePlayer()
                .map(player -> this.getAllItems(player.getInventory()))
                .get(HartshornUtils::emptyList);
    }

    @Override
    public boolean give(Item item) {
        return this.player.getSpongePlayer().map(player -> {
            MainPlayerInventory inventory = player.getInventory()
                    .query(new InventoryTypeQueryOperation(MainPlayerInventory.class));
            ItemStack stack = SpongeConversionUtil.toSponge(item);
            InventoryTransactionResult result = inventory.getHotbar().offer(stack);
            if (Type.SUCCESS == result.getType()) return true;

            return Type.SUCCESS == inventory.offer(stack).getType();
        }).or(false);
    }

    @Override
    public Exceptional<InventoryRow> getRow(int index) {
        if (4 >= index) {
            return Exceptional.of(new SpongeInventoryRow(this, index, this.player));
        }
        return Exceptional.none();
    }
}
