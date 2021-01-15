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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
class PlotWorldModelList {
    private final List<PlotWorldModel> worlds = SeleneUtils.emptyList();

    public List<PlotWorldModel> getWorlds() {
        return this.worlds;
    }

    public void addWorld(PlotWorldModel model) {
        this.worlds.add(model);
    }

    public Exceptional<PlotWorldModel> getWorld(@NonNls String worldName) {
        return Exceptional.of(() -> {
            for (PlotWorldModel world : this.worlds) {
                if (world.getName().equalsIgnoreCase(worldName)) {
                    return world;
                }
            }
            //noinspection ReturnOfNull
            return null;
        });
    }
}
