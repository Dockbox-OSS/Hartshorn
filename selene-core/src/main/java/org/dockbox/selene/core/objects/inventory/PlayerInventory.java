/*
 *  Copyright (C) 2020 Guus Lieben
 *  
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.objects.inventory;

import org.dockbox.selene.core.objects.Exceptional;

/**
 * Represents a single {@link org.dockbox.selene.core.objects.player.Player player's} inventory. This is built up of:
 * - 4 rows, of which one is the hotbar
 * - 4 equipment slots
 * - 2 hand slots (off- and main-hand)
 */
public abstract class PlayerInventory implements Inventory {

    /**
     * Gets a specific row within the inventory. If the index is out of bounds, an empty {@link Exceptional} is returned.
     *
     * @param index
     *     The index of the row.
     *
     * @return The row, or empty.
     */
    public abstract Exceptional<InventoryRow> getRow(int index);

    /**
     * Gets the hotbar within the inventory.
     *
     * @return The hotbar.
     */
    public InventoryRow getHotbar() {
        return this.getRow(3).get();
    }

}
