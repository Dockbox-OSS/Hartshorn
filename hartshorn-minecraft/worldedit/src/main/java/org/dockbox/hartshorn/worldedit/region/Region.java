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

package org.dockbox.hartshorn.worldedit.region;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.ContextCarrier;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.worldedit.WorldEditService;

import java.util.Collection;

public interface Region extends ContextCarrier {

    Vector3N minimum();

    Vector3N maximum();

    Vector3N center();

    int area();

    int width();

    int height();

    int length();

    World world();

    default void replace(final Mask mask, final Pattern pattern, final Player cause) {
        this.applicationContext().get(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(final Pattern pattern, final Player cause) {
        this.applicationContext().get(WorldEditService.class).set(this, pattern, cause);
    }

    default void replace(final Item mask, final Item pattern, final Player cause) {
        this.replace(HartshornUtils.singletonList(mask), HartshornUtils.singletonList(pattern), cause);
    }

    default void replace(final Collection<Item> mask, final Collection<Item> pattern, final Player cause) {
        this.applicationContext().get(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(final Item pattern, final Player cause) {
        this.set(HartshornUtils.singletonList(pattern), cause);
    }

    default void set(final Collection<Item> pattern, final Player cause) {
        this.applicationContext().get(WorldEditService.class).set(this, pattern, cause);
    }
}
