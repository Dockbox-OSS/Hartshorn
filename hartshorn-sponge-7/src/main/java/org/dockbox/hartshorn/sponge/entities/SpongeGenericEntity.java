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

package org.dockbox.hartshorn.sponge.entities;

import net.minecraft.entity.Entity;

import org.spongepowered.api.entity.EntityType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpongeGenericEntity extends SpongeEntity<Entity, SpongeGenericEntity> implements org.dockbox.hartshorn.server.minecraft.entities.Entity {

    private final org.spongepowered.api.entity.Entity representation;

    @Override
    protected EntityType getEntityType() {
        return this.representation.getType();
    }

    @Override
    protected SpongeGenericEntity from(org.spongepowered.api.entity.Entity clone) {
        return new SpongeGenericEntity(clone);
    }
}
