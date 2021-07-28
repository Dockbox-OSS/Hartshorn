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

package org.dockbox.hartshorn.regions;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.regions.flags.RegionFlag;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.Map;

public interface Region extends KeyHolder<Region> {

    Text name();

    Exceptional<Player> owner();

    Map<RegionFlag<?>, ?> flags();

    <T> void add(RegionFlag<T> flag, T value);

    void remove(RegionFlag<?> flag);

    <T> Exceptional<T> get(RegionFlag<T> flag);

    Location center();

    Vector3N size();

    Vector3N cornerA();
    Vector3N cornerB();

    World world();

}
