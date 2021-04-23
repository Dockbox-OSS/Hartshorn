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

package org.dockbox.selene.test.objects.inventory;

import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.item.storage.MinecraftItems;
import org.dockbox.selene.server.minecraft.players.inventory.AbstractInventoryRow;
import org.dockbox.selene.server.minecraft.players.inventory.PlayerInventory;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Collection;
import java.util.Map;

public class JUnitInventoryRow extends AbstractInventoryRow {

    private final Map<Integer, Item> slots = SeleneUtils.emptyMap();

    public JUnitInventoryRow(int rowIndex, PlayerInventory inventory) {
        super(rowIndex, inventory);
    }

    @Override
    public void setSlot(Item item, int index) {
        if (index < this.capacity()) this.slots.put(index, item);
    }

    @Override
    public Collection<Item> getAllItems() {
        return SeleneUtils.asUnmodifiableList(this.slots.values());
    }

    @Override
    public boolean give(Item item) {
        for (int i = 0; i < this.capacity(); i++) {
            if (this.getSlot(i).isAir()) {
                this.setSlot(item, i);
                return true;
            }
        }
        return false;
    }

    @Override
    public Item getSlot(int index) {
        return this.slots.getOrDefault(index, MinecraftItems.getInstance().getAir());
    }
}
