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

package org.dockbox.selene.api.entities;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.item.storage.MinecraftItems;
import org.dockbox.selene.api.objects.location.position.BlockFace;

/**
 * Represents an Item Frame. See <a href="https://minecraft.gamepedia.com/Item_Frame">Item Frame on
 * the Minecraft Wiki</a>.
 */
public interface ItemFrame extends Entity<ItemFrame> {

    /**
     * Returns the current {@link Item} displayed in the item frame, wrapped in a {@link Exceptional}.
     * If no item is currently being represented, {@link Exceptional#empty()} is returned.
     *
     * @return The {@link Item} displayed in the item frame, or {@link Exceptional#empty()}.
     */
    Exceptional<Item> getDisplayedItem();

    /**
     * Sets the {@link Item} to be displayed in the item frame. If the item is equal to {@link
     * MinecraftItems#getAir()} the displayed item is reset to display none.
     *
     * @param stack
     *         The {@link Item} to display.
     */
    void setDisplayedItem(Item stack);

    /**
     * Gets the {@link Rotation} of the currently displayed {@link Item}. If no item is displayed,
     * {@link Rotation#TOP} is returned.
     *
     * @return The {@link Rotation} of the displayed {@link Item}, or {@link Rotation#TOP}.
     */
    Rotation getRotation();

    /**
     * Sets the {@link Rotation} of the currently displayed {@link Item}. If no item is displayed, the
     * rotation is stored for the next item which will be represented.
     *
     * @param rotation
     *         The {@link Rotation} of the displayed {@link Item}.
     */
    void setRotation(Rotation rotation);

    /**
     * Gets the {@link BlockFace} the item frame is placed against. If no blockface is configured,
     * {@link BlockFace#NONE} is returned.
     *
     * @return The {@link BlockFace} the item frame is placed against, or {@link BlockFace#NONE}.
     */
    BlockFace getBlockFace();

    /**
     * Sets the {@link BlockFace} the item frame should be placed against.
     *
     * @param blockFace
     *         The {@link BlockFace} the item frame should be placed against.
     */
    void setBlockFace(BlockFace blockFace);

    enum Rotation {
        BOTTOM,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        LEFT,
        RIGHT,
        TOP,
        TOP_LEFT,
        TOP_RIGHT
    }
}
