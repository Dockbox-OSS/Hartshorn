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

package org.dockbox.hartshorn.server.minecraft.inventory;

import lombok.Getter;

@Getter
public enum InventoryType {
    // Grid inventories, sorted by row count first, then column count
    DOUBLE_CHEST(6, 9),
    SHULKER_BOX(3, 9),
    DISPENSER(3, 3),
    CHEST(3, 9),
    SMOKER(3, 9),
    DROPPER(1, 9),
    LECTERN(1, 5),
    HOPPER(1, 5),

    // Special non-grid inventories, sorted by size
    CRAFTING_BENCH(10),
    BREWING_STAND(5),
    BLAST_FURNACE(3),
    FURNACE(3),
    ANVIL(3),
    ENCHANTMENT_TABLE(2),
    STONE_CUTTER(2),
    GRINDSTONE(2),
    MERCHANT(2),
    LOOM(2),
    CARTOGRAPHY_TABLE(1),
    BEACON(1),
    ;

    private final int rows;
    private final int columns;
    private final int size;

    InventoryType(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        this.size = rows * columns;
    }

    InventoryType(final int size) {
        this.rows = -1;
        this.columns = -1;
        this.size = size;
    }
}
