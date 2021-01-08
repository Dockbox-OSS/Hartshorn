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

import org.dockbox.selene.core.objects.item.Item;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface Inventory {

    Item getSlot(int row, int column);

    Item getSlot(int index);

    Item getSlot(Slot slot);

    void setSlot(Item item, int row, int column);

    void setSlot(Item item, int index);

    void setSlot(Item item, Slot slot);

    default boolean contains(Item item) {
        return 0 < this.count(item);
    }

    default Collection<Item> findMatching(Predicate<Item> filter) {
        return this.getAllItems().stream()
            .filter(filter)
            .collect(Collectors.toList());
    }

    Collection<Item> getAllItems();

    default int count(Item item) {
        return this.getAllItems().stream()
            .filter(inventoryItem -> inventoryItem.equals(item))
            .mapToInt(Item::getAmount)
            .sum();
    }
    
}
