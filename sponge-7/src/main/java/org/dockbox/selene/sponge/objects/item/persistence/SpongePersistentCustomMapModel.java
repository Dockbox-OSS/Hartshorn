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

package org.dockbox.selene.sponge.objects.item.persistence;

import com.google.inject.Singleton;

import org.dockbox.selene.api.annotations.entity.Extract;
import org.dockbox.selene.api.annotations.entity.Extract.Behavior;
import org.dockbox.selene.api.annotations.entity.Metadata;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.item.maps.CustomMapService;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.sponge.objects.item.maps.SpongeCustomMap;

@Singleton
@Extract(Behavior.KEEP)
@Metadata(alias = "map")
public class SpongePersistentCustomMapModel extends SpongePersistentItemModel {

    public SpongePersistentCustomMapModel(SpongeCustomMap map) {
        super(map);
    }

    @Override
    public Class<? extends Item> getCapableType() {
        return SpongeCustomMap.class;
    }

    @Override
    public Item toPersistentCapable() {
        return repopulate(Selene.provide(CustomMapService.class).getById(getMeta()));
    }
}
