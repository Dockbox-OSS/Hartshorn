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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.server.minecraft.players.inventory.InventoryRow;
import org.dockbox.hartshorn.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import lombok.Getter;

public class JUnitInventory extends PlayerInventory {

    private final Map<Integer, InventoryRow> rows = HartshornUtils.emptyMap();
    private final Map<Slot, Item> specialSlots = HartshornUtils.emptyMap();

    private final int rowCount = 3;
    private final int columnCount = 9;

    @Getter private final ApplicationContext applicationContext;

    public JUnitInventory(final ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public Item slot(final Slot slot) {
        return this.specialSlots.getOrDefault(slot, Item.of(this.applicationContext(), ItemTypes.AIR));
    }

    @Override
    public void slot(final Item item, final Slot slot) {
        this.specialSlots.put(slot, item);
    }

    @Override
    public int capacity() {
        return this.rowCount * this.columnCount;
    }

    @Override
    public void slot(final Item item, final int index) {
        this.slot(item, this.rowIndex(index), this.columnIndex(index));
    }

    @Override
    public Item slot(final int index) {
        return this.slot(this.rowIndex(index), this.columnIndex(index));
    }

    private int rowIndex(final int index) {
        return (index - (index % 3)) / 9;
    }

    private int columnIndex(final int index) {
        return index % 9;
    }

    @Override
    public Collection<Item> items() {
        final List<Item> items = HartshornUtils.emptyList();
        for (final InventoryRow row : this.rows.values()) items.addAll(row.items());
        items.addAll(this.specialSlots.values());
        return HartshornUtils.asUnmodifiableList(items);
    }

    @Override
    public boolean give(final Item item) {
        for (int row = 0; row < this.rowCount; row++) {
            for (int column = 0; column < this.columnCount; column++) {
                if (this.slot(row, column).isAir()) {
                    this.slot(item, row, column);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Item slot(final int row, final int column) {
        return this.rows.get(row).slot(column);
    }

    @Override
    public void slot(final Item item, final int row, final int column) {
        this.rows.get(row).slot(item, column);
    }

    @Override
    public Exceptional<InventoryRow> row(final int index) {
        if (index < this.rowCount) {
            return Exceptional.of(new JUnitInventoryRow(index, this));
        }
        return Exceptional.empty();
    }
}
