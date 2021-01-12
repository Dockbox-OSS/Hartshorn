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

package org.dockbox.selene.core.objects.inventory;

import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.item.storage.MinecraftItems;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents an inventory UI
 */
public interface Inventory {

    /**
     * Gets the {@link Item} in the requested position within the inventory. If the position is out of bounds, or if there is no item present,
     * {@link MinecraftItems#getAir()} is returned instead. Indices start at zero.
     *
     * @param row
     *     The row index
     * @param column
     *     The column index
     *
     * @return The {@link Item}, or {@link MinecraftItems#getAir() air}.
     */
    Item getSlot(int row, int column);

    /**
     * Gets the {@link Item} in the requested position within the inventory. If the position is out of bounds, or if there is no item present,
     * {@link MinecraftItems#getAir()} is returned instead. Indices start at zero.
     *
     * @param index
     *     The inventory index
     *
     * @return The {@link Item}, or {@link MinecraftItems#getAir() air}.
     */
    Item getSlot(int index);

    /**
     * Gets the {@link Item} in the requested slot type within the inventory. If the slot type is not supported by the inventory, or if there is no
     * item present, {@link MinecraftItems#getAir()} is returned instead.
     *
     * @param slot
     *     The slot type
     *
     * @return The {@link Item}, or {@link MinecraftItems#getAir() air}.
     */
    Item getSlot(Slot slot);

    /**
     * Sets the item at the given position within the inventory to the given {@link Item}. If the position is out of bounds, nothing happens. Indices
     * start at zero.
     *
     * @param item
     *     The item to place
     * @param row
     *     The row index
     * @param column
     *     The column index
     */
    void setSlot(Item item, int row, int column);

    /**
     * Sets the item at the given position within the inventory to the given {@link Item}. If the position is out of bounds, nothing happens. Indices
     * start at zero.
     *
     * @param item
     *     The item to place
     * @param index
     *     The inventory index
     */
    void setSlot(Item item, int index);

    /**
     * Sets the item at the given slot type to the given {@link Item}. If the slot type is not supported by the inventory, nothing happens. Indices
     * start at zero.
     *
     * @param item
     *     The item to place
     * @param slot
     *     The slot type
     */
    void setSlot(Item item, Slot slot);

    /**
     * Returns {@code true} if the inventory contains the given {@link Item}, otherwise {@code false}.
     *
     * @param item
     *     The item
     *
     * @return {@code true} if the inventory contains the item, otherwise {@code false}
     */
    default boolean contains(Item item) {
        return 0 < this.count(item);
    }

    /**
     * Returns all {@link Item items} which match a given filter. If no items are present, or none match the filter, an empty list is returned.
     *
     * @param filter
     *     The filter
     *
     * @return All items which match the given filter.
     */
    default Collection<Item> findMatching(Predicate<Item> filter) {
        return this.getAllItems().stream()
            .filter(filter)
            .collect(Collectors.toList());
    }

    /**
     * Returns all {@link Item items}, including {@link MinecraftItems#getAir()}.
     *
     * @return All items inside the inventory.
     */
    Collection<Item> getAllItems();

    /**
     * Returns the total amount of occurrences of the {@link Item} as the total quantity.
     *
     * <p>E.g. if two stacks (64) of {@code minecraft:stone} are inside the inventory, 128 will be returned.
     *
     * @param item
     *     The item
     *
     * @return The total quantity of the item
     */
    default int count(Item item) {
        return this.getAllItems().stream()
            .filter(inventoryItem -> inventoryItem.equals(item))
            .mapToInt(Item::getAmount)
            .sum();
    }

    /**
     * Attempts to give the {@link Item} to the inventory. If the item cannot be added, false is returned.
     *
     * @param item
     *     The item to add
     *
     * @return {@code true} if the item was added, otherwise {@code false}
     */
    boolean give(Item item);

    /**
     * Returns the first occurring index of the given {@link Item}. If the item is not present, -1 is returned.
     *
     * @param item
     *     The item
     *
     * @return The first index of the item, or -1
     */
    default int indexOf(Item item) {
        int capacity = this.capacity() -1; //-1 to correct for index offset
        while (0 <= capacity) {
            Item slot = this.getSlot(capacity);
            if (slot.equals(item)) return capacity;
            capacity--;
        }
        return -1; // No matches
    }

    /**
     * Returns the total capacity of the inventory. This is equal to the maximum index, plus one (to correct for starting at 0).
     *
     * @return The capacity
     */
    int capacity();
}
