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

package org.dockbox.selene.server.minecraft.inventory;

import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.minecraft.inventory.builder.LayoutBuilder;
import org.dockbox.selene.server.minecraft.inventory.properties.InventoryTypeProperty;

import java.util.Map;

/**
 * Represents the layout of a inventory. Typically this is responsible for placing the default
 * elements in a {@link org.dockbox.selene.server.minecraft.inventory.pane.Pane}.
 */
public interface InventoryLayout {

    /**
     * Create a new {@link LayoutBuilder} instance.
     *
     * @param inventoryType
     *         The inventory type to use while building the pane.
     *
     * @return The builder
     */
    static LayoutBuilder builder(InventoryType inventoryType) {
        return Provider.provide(LayoutBuilder.class, new InventoryTypeProperty(inventoryType));
    }

    /**
     * Get all the {@link Element elements} in the inventory, identified by their position index.
     *
     * @return The elements in the inventory.
     */
    Map<Integer, Element> getElements();

    /**
     * Get the {@link InventoryType inventory type} used by the layout.
     *
     * @return The iventory type.
     */
    InventoryType getIventoryType();
}
