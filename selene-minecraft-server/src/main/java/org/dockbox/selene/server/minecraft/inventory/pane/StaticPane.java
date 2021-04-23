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

package org.dockbox.selene.server.minecraft.inventory.pane;

import org.dockbox.selene.di.Provider;
import org.dockbox.selene.server.minecraft.inventory.Element;
import org.dockbox.selene.server.minecraft.inventory.InventoryLayout;
import org.dockbox.selene.server.minecraft.inventory.builder.StaticPaneBuilder;
import org.dockbox.selene.server.minecraft.inventory.properties.LayoutProperty;
import org.dockbox.selene.server.minecraft.item.Item;

/** Represents a static (non-changing) pane, which can be updated without closing the pane. */
public interface StaticPane extends Pane {

    /**
     * Create a new {@link StaticPaneBuilder} instance.
     *
     * @param layout
     *         The layout to use while building the pane.
     *
     * @return The builder
     */
    static StaticPaneBuilder builder(InventoryLayout layout) {
        return Provider.provide(StaticPaneBuilder.class, new LayoutProperty(layout));
    }

    /**
     * Places a specific {@link Element} on the given {@code index}.
     *
     * @param element
     *         The element to place.
     * @param index
     *         The position index.
     */
    void set(Element element, int index);

    /**
     * Places a specific {@link Item} on the given {@code index}.
     *
     * @param item
     *         The item to place.
     * @param index
     *         The position index.
     */
    void set(Item item, int index);

    /**
     * Updates the pane. This will update the pane even if it is open to a player. This will not close
     * the pane and open the updated version, and instead cleans and replaces the items currently
     * displayed by the pane.
     *
     * @param layout
     *         The new layout to display.
     */
    void update(InventoryLayout layout);
}
