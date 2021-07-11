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

package org.dockbox.hartshorn.sponge.game.entity;

import net.minecraft.world.entity.Entity;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.spongepowered.api.entity.EntityType;

import java.lang.ref.WeakReference;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SpongeGenericEntity implements SpongeEntity<Entity, org.spongepowered.api.entity.Entity> {

    private final WeakReference<org.spongepowered.api.entity.Entity> entity;

    @Override
    public EntityType<org.spongepowered.api.entity.Entity> type() {
        //noinspection unchecked
        return (EntityType<org.spongepowered.api.entity.Entity>) this.spongeEntity()
                .map(org.spongepowered.api.entity.Entity::type)
                .orNull();
    }

    @Override
    public Exceptional<org.spongepowered.api.entity.Entity> spongeEntity() {
        return Exceptional.of(this.entity.get());
    }
}
