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

package org.dockbox.selene.minecraft.players.inventory;

import org.dockbox.selene.minecraft.item.Item;
import org.dockbox.selene.minecraft.item.storage.MinecraftItems;

import java.util.function.Supplier;

public abstract class AbstractInventoryRow implements InventoryRow {

    public static final Supplier<Item> AIR = () -> MinecraftItems.getInstance().getAir();

    private final int rowIndex;
    private final PlayerInventory inventory;

    public AbstractInventoryRow(int rowIndex, PlayerInventory inventory) {
        this.rowIndex = rowIndex;
        this.inventory = inventory;
    }

    @Override
    public Item getSlot(int row, int column) {
        if (row != this.rowIndex) return MinecraftItems.getInstance().getAir();
        return this.getSlot(column);
    }

    @Override
    public void setSlot(Item item, int row, int column) {
        if (row != this.rowIndex) return;
        this.setSlot(item, column);
    }

    protected int getRowIndex() {
        return this.rowIndex;
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }

    @Override
    public int capacity() {
        return 9;
    }
}
