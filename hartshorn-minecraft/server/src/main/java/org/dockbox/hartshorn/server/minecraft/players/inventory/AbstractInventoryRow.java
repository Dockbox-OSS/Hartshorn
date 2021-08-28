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

package org.dockbox.hartshorn.server.minecraft.players.inventory;

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;

import java.util.function.Function;

import lombok.Getter;

@Getter
public abstract class AbstractInventoryRow implements InventoryRow {

    public static final Function<ApplicationContext, Item> AIR = ctx -> Item.of(ctx, ItemTypes.AIR);

    private final int rowIndex;
    private final PlayerInventory inventory;
    private final ApplicationContext applicationContext;

    public AbstractInventoryRow(final int rowIndex, final PlayerInventory inventory) {
        this.rowIndex = rowIndex;
        this.inventory = inventory;
        this.applicationContext = inventory.applicationContext();
    }

    @Override
    public Item slot(final int row, final int column) {
        if (row != this.rowIndex) return Item.of(this.applicationContext(), ItemTypes.AIR);
        return this.slot(column);
    }

    @Override
    public void slot(final Item item, final int row, final int column) {
        if (row != this.rowIndex) return;
        this.slot(item, column);
    }

    @Override
    public int capacity() {
        return 9;
    }
}
