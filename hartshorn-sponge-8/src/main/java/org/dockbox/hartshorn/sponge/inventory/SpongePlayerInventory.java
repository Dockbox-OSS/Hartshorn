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
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.inventory.AbstractInventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.InventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.sponge.game.SpongePlayer;
import org.dockbox.hartshorn.sponge.util.SpongeAdapter;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult.Type;

import java.util.Collection;

public class SpongePlayerInventory extends PlayerInventory implements SpongeInventory {

    private static final int inventorySize = 36;
    private final SpongePlayer player;

    public SpongePlayerInventory(final SpongePlayer player) {
        this.player = player;
    }

    @Override
    public Item slot(final int row, final int column) {
        return this.internalGetSlot(row, column).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    @Override
    public void slot(final Item item, final int row, final int column) {
        this.internalGetSlot(row, column).present(slot -> slot.set(SpongeAdapter.toSponge(item)));
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(
            final int row, final int column) {
        return this.player.player().map(player -> {
            if (3 > row) { // Main inventory
                return player.inventory().storage().slot(column, row).orElse(null);
            }
            else if (3 == row) { // Hotbar
                final Hotbar hotbar = player.inventory().hotbar();
                return hotbar.slot(column).orElse(null);
            }
            else throw new IllegalArgumentException("Slot index [row=" + row + ", col=" + column + "] is out of bounds (row: 0-3, col: 0-8)");
        });
    }

    @Override
    public Item slot(final Slot slot) {
        return this.internalGetSlot(slot).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    @Override
    public void slot(final Item item, final Slot slotType) {
        this.internalGetSlot(slotType).present(slot -> slot.set(SpongeAdapter.toSponge(item)));
    }

    @Override
    public int capacity() {
        return inventorySize;
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(final Slot slot) {
        return this.player.player().map(player -> {
            final EquipmentInventory equipment = player.inventory().equipment();
            final EquipmentType equipmentType = SpongeAdapter.toSponge(slot);
            return equipment.slot(equipmentType).orElse(null);
        });
    }

    @Override
    public void slot(final Item item, final int index) {
        this.internalGetSlot(index).present(slot -> slot.set(SpongeAdapter.toSponge(item)));
    }

    @Override
    public Item slot(final int index) {
        return this.internalGetSlot(index).map(SLOT_LOOKUP).get(AbstractInventoryRow.AIR);
    }

    private Exceptional<org.spongepowered.api.item.inventory.Slot> internalGetSlot(final int index) {
        final int gridSize = 27;
        return this.player.player().map(player -> {
            if (gridSize > index) { // Main inventory
                return player.inventory().slot(index).orElse(null);

            }
            else if (inventorySize > index) { // Hotbar
                final Hotbar hotbar = player.inventory().hotbar();

                // -27 to correct for the grid gap (main grid is excluded once we get the Hotbar
                // inventory, and is 3x9 slots)
                return hotbar.slot(index - gridSize).orElse(null);

            }
            else throw new IllegalArgumentException("Slot index " + index + " is out of bounds (0-35)");
        });
    }

    @Override
    public Collection<Item> items() {
        return this.inventory()
                .map(inventory -> inventory.slots()
                        .stream()
                        .map(slot -> SpongeAdapter.fromSponge(slot.peek()))
                        .map(Item.class::cast)
                        .toList())
                .orElse(HartshornUtils::emptyList)
                .get();
    }

    @Override
    public boolean give(final Item item) {
        return this.player.player().map(player -> {
            final org.spongepowered.api.item.inventory.entity.PlayerInventory inventory = player.inventory();
            final ItemStack stack = SpongeAdapter.toSponge(item);
            final InventoryTransactionResult result = inventory.hotbar().offer(stack);
            if (Type.SUCCESS == result.type()) return true;
            else return Type.SUCCESS == inventory.offer(stack).type();
        }).or(false);
    }

    private Exceptional<org.spongepowered.api.item.inventory.entity.PlayerInventory> inventory() {
        return this.player.player().map(Player::inventory);
    }

    @Override
    public Exceptional<InventoryRow> row(final int index) {
        if (4 >= index) {
            return Exceptional.of(new SpongeInventoryRow(this, index, this.player));
        }
        return Exceptional.empty();
    }
}
