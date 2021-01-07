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

package org.dockbox.selene.core.inventory;

import org.dockbox.selene.core.inventory.factory.ElementFactory;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.function.Consumer;

/**
 * Represents a inventory element which can be displayed by a {@link org.dockbox.selene.core.inventory.pane.Pane}
 * instance. A element can either be a static representation of a {@link Item}, or carry a action listener in the form
 * of a {@link Consumer<Player>}.
 */
public interface Element {

    /**
     * Gets the represented {@link Item}.
     *
     * @return The represented item.
     */
    Item getItem();

    /**
     * Sets the action to perform when a player interacts with the element.
     *
     * @param onClick
     *         The action to perform.
     */
    void onClick(Consumer<Player> onClick);

    /**
     * Creates an element from the given {@link Item}.
     *
     * @param item
     *         The item to represent.
     *
     * @return The element.
     */
    static Element of(Item item) {
        return of(item, p -> {});
    }

    /**
     * Creates an element from the given {@link Item} and adds a action listener to perform the given {@code onClick}
     * action.
     *
     * @param item
     *         The item to represent.
     * @param onClick
     *         The action to perform when a player interacts with the item.
     *
     * @return The element.
     */
    static Element of(Item item, Consumer<Player> onClick) {
        return SeleneUtils.INJECT.getInstance(ElementFactory.class).create(item, onClick);
    }

}
