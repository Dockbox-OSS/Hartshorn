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

package org.dockbox.selene.worldedit.region;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.tuple.Vector3N;
import org.dockbox.selene.server.minecraft.dimension.world.World;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.util.SeleneUtils;
import org.dockbox.selene.worldedit.WorldEditService;

import java.util.Collection;

public interface Region {

    Vector3N getMinimumPoint();

    Vector3N getMaximumPoint();

    Vector3N getCenter();

    int getArea();

    int getWidth();

    int getHeight();

    int getLength();

    World getWorld();

    default void replace(Mask mask, Pattern pattern, Player cause) {
        Selene.context().get(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(Pattern pattern, Player cause) {
        Selene.context().get(WorldEditService.class).set(this, pattern, cause);
    }

    default void replace(Item mask, Item pattern, Player cause) {
        this.replace(SeleneUtils.singletonList(mask), SeleneUtils.singletonList(pattern), cause);
    }

    default void replace(Collection<Item> mask, Collection<Item> pattern, Player cause) {
        Selene.context().get(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(Item pattern, Player cause) {
        this.set(SeleneUtils.singletonList(pattern), cause);
    }

    default void set(Collection<Item> pattern, Player cause) {
        Selene.context().get(WorldEditService.class).set(this, pattern, cause);
    }
}