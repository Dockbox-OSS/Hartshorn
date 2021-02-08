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

package org.dockbox.selene.core.inventory.pane;

import org.dockbox.selene.core.inventory.Element;
import org.dockbox.selene.core.inventory.InventoryLayout;
import org.dockbox.selene.core.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.selene.core.inventory.properties.LayoutProperty;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;

import java.util.Collection;

/**
 * Represents a pane containing pagination controls to display a large quantity of {@link Element elements}.
 */
public interface PaginatedPane extends Pane
{

    /**
     * Create a new {@link PaginatedPaneBuilder} instance.
     *
     * @param layout
     *         The layout to use while building the pane.
     *
     * @return The builder
     */
    static PaginatedPaneBuilder builder(InventoryLayout layout)
    {
        return Selene.provide(PaginatedPaneBuilder.class, new LayoutProperty(layout));
    }

    /**
     * Opens the pane on a specific page for the given {@link Player}.
     *
     * @param player
     *         The player to show the pane to.
     * @param page
     *         The number of the page to show.
     */
    void open(Player player, int page);

    /**
     * Set the {@link Element elements} to be displayed by the pane.
     *
     * @param elements
     *         The elements
     */
    void elements(Collection<Element> elements);
}
