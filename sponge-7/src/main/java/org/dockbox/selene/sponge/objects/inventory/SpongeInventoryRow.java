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

package org.dockbox.selene.sponge.objects.inventory;

import org.dockbox.selene.core.objects.inventory.InventoryRow;
import org.dockbox.selene.core.objects.inventory.PlayerInventory;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;

import java.util.Collection;
import java.util.function.Function;

public class SpongeInventoryRow implements InventoryRow {

    private final SpongePlayerInventory inventory;
    private final int rowIndex;
    private final SpongePlayer player;

    public SpongeInventoryRow(SpongePlayerInventory inventory, int rowIndex, SpongePlayer player) {
        this.inventory = inventory;
        this.rowIndex = rowIndex;
        this.player = player;
    }

    // TODO, implementation
    
    @Override
    public Item getSlot(int row, int column) {
        return null;
    }

    @Override
    public Item getSlot(int index) {
        return null;
    }

    @Override
    public Item getSlot(Slot slot) {
        return null;
    }

    @Override
    public void setSlot(Item item, int row, int column) {

    }

    @Override
    public void setSlot(Item item, int index) {

    }

    @Override
    public void setSlot(Item item, Slot slot) {

    }

    @Override
    public boolean contains(Item item) {
        return false;
    }

    @Override
    public Collection<Item> findMatching(Function<Item, Boolean> filter) {
        return null;
    }

    @Override
    public int count(Item item) {
        return 0;
    }

    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }
}
