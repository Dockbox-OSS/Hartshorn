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

package org.dockbox.hartshorn.test.objects.inventory;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.hartshorn.server.minecraft.players.inventory.InventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JUnitInventory extends PlayerInventory {

    private final Map<Integer, InventoryRow> rows = HartshornUtils.emptyMap();
    private final Map<Slot, Item> specialSlots = HartshornUtils.emptyMap();

    private final int rowCount = 3;
    private final int columnCount = 9;

    @Override
    public Item getSlot(int row, int column) {
        return this.rows.get(row).getSlot(column);
    }

    @Override
    public Item getSlot(Slot slot) {
        return this.specialSlots.getOrDefault(slot, MinecraftItems.getInstance().getAir());
    }

    @Override
    public void setSlot(Item item, int row, int column) {
        this.rows.get(row).setSlot(item, column);
    }

    @Override
    public void setSlot(Item item, int index) {
        this.setSlot(item, this.row(index), this.column(index));
    }

    @Override
    public void setSlot(Item item, Slot slot) {
        this.specialSlots.put(slot, item);
    }

    @Override
    public Collection<Item> getAllItems() {
        List<Item> items = HartshornUtils.emptyList();
        for (InventoryRow row : this.rows.values()) items.addAll(row.getAllItems());
        items.addAll(this.specialSlots.values());
        return HartshornUtils.asUnmodifiableList(items);
    }

    @Override
    public boolean give(Item item) {
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (this.getSlot(row, column).isAir()) {
                    this.setSlot(item, row, column);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int capacity() {
        return this.rowCount * this.columnCount;
    }

    @Override
    public Item getSlot(int index) {
        return this.getSlot(this.row(index), this.column(index));
    }

    @Override
    public Exceptional<InventoryRow> getRow(int index) {
        if (index < this.rowCount) {
            return Exceptional.of(new JUnitInventoryRow(index, this));
        }
        return Exceptional.none();
    }

    private int row(int index) {
        return (index - (index % 3)) / 9;
    }

    private int column(int index) {
        return index % 9;
    }
}
