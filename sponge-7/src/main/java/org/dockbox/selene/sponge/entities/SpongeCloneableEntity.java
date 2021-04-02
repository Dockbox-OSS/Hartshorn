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

package org.dockbox.selene.sponge.entities;

import net.minecraft.nbt.NBTTagCompound;

import org.dockbox.selene.api.entities.CloneableEntity;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityArchetype;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.CauseStackManager.StackFrame;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.common.bridge.world.WorldInfoBridge;
import org.spongepowered.common.data.AbstractArchetype;
import org.spongepowered.common.entity.SpongeEntitySnapshotBuilder;
import org.spongepowered.common.util.Constants;

public abstract class SpongeCloneableEntity<T extends net.minecraft.entity.Entity, E extends CloneableEntity<E>> extends SpongeEntity<T, E> implements CloneableEntity<E> {

    @Override
    public E copy() {
        // TODO: S192, entities cannot currently copy correctly
        return SpongeConversionUtil.toSponge(this.getLocation()).map(spongeLocation -> {
            try (StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
                frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                EntityArchetype archetype = this.getRepresentation().createArchetype();

                @SuppressWarnings("unchecked")
                org.spongepowered.api.entity.Entity clone = this.createSnapshot(
                        (AbstractArchetype<EntityType, EntitySnapshot, Entity>) archetype,
                        spongeLocation
                ).restore().orElse(null);

                return this.from(clone);
            }
        }).rethrow().orNull();
    }


    /**
     * Custom implementation of {@link
     * org.spongepowered.common.entity.SpongeEntityArchetype#toSnapshot(org.spongepowered.api.world.Location)},
     * fixing the incompatibility with non-rotated entities. The original method did not include the
     * rotation, scale, and type in the snapshot.
     *
     * @param archetype
     *         The entity archetype to use when creating the snapshot.
     * @param location
     *         The base location of the entity snapshot
     *
     * @return The new {@link EntitySnapshot}
     */
    private EntitySnapshot createSnapshot(
            AbstractArchetype<EntityType, EntitySnapshot, org.spongepowered.api.entity.Entity> archetype,
            org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location
    ) {
        final SpongeEntitySnapshotBuilder builder = new SpongeEntitySnapshotBuilder();
        builder.type(this.getEntityType());
        NBTTagCompound newCompound = archetype.getCompound().copy();
        newCompound.setTag("Pos", Constants.NBT.newDoubleNBTList(
                location.getPosition().getX(),
                location.getPosition().getY(),
                location.getPosition().getZ()));
        //noinspection ConstantConditions
        newCompound.setInteger("Dimension", ((WorldInfoBridge) location.getExtent().getProperties()).bridge$getDimensionId());
        builder.unsafeCompound(newCompound);
        builder.worldId(location.getExtent().getUniqueId());
        builder.position(location.getPosition());
        builder.rotation(this.getRepresentation().getRotation());
        builder.scale(this.getRepresentation().getScale());
        builder.type(this.getEntityType());
        return builder.build();
    }

}
