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

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMap;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMapService;

import javax.inject.Singleton;

@Singleton
public class PersistentCustomMapModelImpl extends PersistentItemModelImpl {

    private final int mapId;

    public PersistentCustomMapModelImpl(final CustomMap map) {
        super(map);
        this.mapId = map.mapId();
    }

    @Override
    public Class<? extends Item> type() {
        return CustomMap.class;
    }

    @Override
    public Item restore(final ApplicationContext context) {
        return this.repopulate(context.get(CustomMapService.class).from(this.mapId));
    }
}
