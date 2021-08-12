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

package org.dockbox.hartshorn.server.minecraft.dimension;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.server.minecraft.Interactable;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;

import java.util.Map;

public interface Block extends PersistentDataHolder, Interactable {

    static Block from(final Location location) {
        return Hartshorn.context().get(Block.class, location);
    }

    static Block from(final ItemTypes type) {
        return from(Item.of(type));
    }

    static Block from(final Item item) {
        return Hartshorn.context().get(Block.class, item);
    }

    static Block of(final String id) {
        return from(Item.of(id));
    }

    static Block empty() {
        return Hartshorn.context().get(Block.class, Item.of(ItemTypes.AIR));
    }

    Exceptional<Item> item();

    String id();

    Map<String, Object> states();

    <T> Exceptional<T> state(String state);

    void state(String state, Object value);

    boolean isEmpty();

    boolean place(Location location);

}
