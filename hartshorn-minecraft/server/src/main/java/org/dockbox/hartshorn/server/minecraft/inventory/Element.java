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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PartialApi;
import org.dockbox.hartshorn.server.minecraft.item.Item;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a inventory element which can be displayed by a {@link
 * org.dockbox.hartshorn.server.minecraft.inventory.pane.Pane} instance. A element can either be a static
 * representation of a {@link Item}, or carry a action listener in the form of a {@link Consumer
 * Player consumer}.
 */
public interface Element {

    /**
     * Creates an element from the given {@link Item}.
     *
     * @param item
     *         The item to represent.
     *
     * @return The element.
     */
    static Element of(final Item item) {
        return of(item, true);
    }

    static Element of(final Item item, final boolean modifiable) {
        return of(item, p -> modifiable);
    }

    /**
     * Creates an element from the given {@link Item} and adds a action listener to perform the given
     * {@code onClick} action.
     *
     * @param item
     *         The item to represent.
     * @param onClick
     *         The action to perform when a player interacts with the item.
     *
     * @return The element.
     */
    static Element of(final Item item, final Function<ClickContext, Boolean> onClick) {
        return Hartshorn.context().get(Element.class, item, onClick);
    }

    /**
     * Gets the represented {@link Item}.
     *
     * @return The represented item.
     */
    Item item();

    /**
     * Sets the action to perform when a player interacts with the element.
     *
     * @param onClick
     *         The action to perform.
     */
    void onClick(Function<ClickContext, Boolean> onClick);

    /**
     * Executes the consumer of the element with the given player.
     * @param player The player that caused the click action.
     * @return Whether the slot can be modified
     */
    @PartialApi
    boolean perform(final ClickContext player);

    @PartialApi
    boolean listening();
}
