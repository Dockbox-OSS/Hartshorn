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

    // Generic inventories, sorted by row count
    GENERIC_5_ROWS(5, 9),
    GENERIC_4_ROWS(4, 9),
    GENERIC_2_ROWS(2, 9),

    // Special non-grid inventories, sorted by size
    CRAFTING_BENCH(10, true),
    BREWING_STAND(5, false), // Technically true, but hasOutput assumes a single output, not three
    BLAST_FURNACE(3, true),
    FURNACE(3, true),
    ANVIL(3, true),
    ENCHANTMENT_TABLE(2, true),
    STONE_CUTTER(2, true),
    GRINDSTONE(3, true),
    MERCHANT(3, true),
    LOOM(4, true),
    CARTOGRAPHY_TABLE(3, true),
    BEACON(1, false),
    ;

    private final int rows;
    private final int columns;
    private final int size;
    private final boolean hasOutput;

    InventoryType(final int rows, final int columns) {
        this.rows = rows;
        this.columns = columns;
        this.size = rows * columns;
        this.hasOutput = false;
    }

    InventoryType(final int size, final boolean hasOutput) {
        this.rows = -1;
        this.columns = -1;
        this.size = size;
        this.hasOutput = hasOutput;
    }
}
