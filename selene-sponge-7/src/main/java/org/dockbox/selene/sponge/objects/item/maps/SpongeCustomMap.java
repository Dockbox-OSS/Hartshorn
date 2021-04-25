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

package org.dockbox.selene.sponge.objects.item.maps;

import org.dockbox.selene.api.domain.Identifiable;
import org.dockbox.selene.server.minecraft.item.SimplePersistentCustomMapModel;
import org.dockbox.selene.server.minecraft.item.maps.CustomMap;
import org.dockbox.selene.server.minecraft.item.persistence.PersistentItemModel;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeCustomMap extends SpongeItem implements CustomMap {

    private final Identifiable owner;
    private final int mapId;

    public SpongeCustomMap(@NotNull ItemStack initialValue, Identifiable owner, int mapId) {
        super(initialValue);
        this.owner = owner;
        this.mapId = mapId;
    }

    @Override
    public Identifiable getOwner() {
        return this.owner;
    }

    @Override
    public int getMapId() {
        return this.mapId;
    }

    @Override
    public Class<? extends PersistentItemModel> getModelClass() {
        return SimplePersistentCustomMapModel.class;
    }

    @Override
    public PersistentItemModel toPersistentModel() {
        return new SimplePersistentCustomMapModel(this);
    }
}
