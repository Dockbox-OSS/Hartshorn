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

package org.dockbox.hartshorn.server.minecraft.item;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMap;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMapService;

import javax.inject.Singleton;

@Singleton
@Entity(value = "map")
public class SimplePersistentCustomMapModel extends SimplePersistentItemModel {

    public SimplePersistentCustomMapModel(CustomMap map) {
        super(map);
    }

    @Override
    public Class<? extends Item> getCapableType() {
        return CustomMap.class;
    }

    @Override
    public Item toPersistentCapable() {
        return this.repopulate(Hartshorn.context().get(CustomMapService.class).getById(this.getMeta()));
    }
}