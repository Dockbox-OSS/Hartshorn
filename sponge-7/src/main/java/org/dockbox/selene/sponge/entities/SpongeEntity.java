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

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.location.World;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.nms.entities.NMSEntity;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
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

import java.util.UUID;

/**
 * Represents common functionality for all entities implemented in Sponge.
 *
 * @param <T>
 *         The native Minecraft {@link Entity} type to represent
 * @param <E>
 *         The internal Selene {@link org.dockbox.selene.api.entities.Entity} type to represent
 */
public abstract class SpongeEntity
        <T extends Entity, E extends org.dockbox.selene.api.entities.Entity<E>>
        extends NMSEntity<T>
        implements org.dockbox.selene.api.entities.Entity<E>, SpongeComposite
{

    @SuppressWarnings("unchecked")
    protected <C extends org.spongepowered.api.entity.Entity> C create(Location location)
    {
        return SpongeConversionUtil.toSponge(location)
                .map(spongeLocation -> (C) spongeLocation.createEntity(this.getEntityType()))
                .orNull();
    }

    protected abstract EntityType getEntityType();

    @Override
    public UUID getUniqueId()
    {
        return this.getRepresentation().getUniqueId();
    }

    @Override
    public String getName()
    {
        return this.getDisplayName().toPlain();
    }

    @Override
    public Text getDisplayName()
    {
        return SpongeConversionUtil.fromSponge(this.getRepresentation()
                .getOrElse(Keys.DISPLAY_NAME, org.spongepowered.api.text.Text.EMPTY)
        );
    }

    @Override
    public void setDisplayName(Text displayName)
    {
        if (null == displayName)
        {
            this.getRepresentation().offer(Keys.CUSTOM_NAME_VISIBLE, false);
            this.getRepresentation().remove(Keys.DISPLAY_NAME);
        }
        else
        {
            this.getRepresentation().offer(Keys.DISPLAY_NAME, SpongeConversionUtil.toSponge(displayName));
            this.getRepresentation().offer(Keys.CUSTOM_NAME_VISIBLE, true);
        }
    }

    @Override
    public double getHealth()
    {
        return this.getRepresentation().getOrElse(Keys.HEALTH, -1D);
    }

    @Override
    public void setHealth(double health)
    {
        double maxHealth = this.getRepresentation().getOrElse(Keys.MAX_HEALTH, DEFAULT_MAX_HEALTH);
        if (maxHealth < health) health = maxHealth;
        this.getRepresentation().offer(Keys.HEALTH, health);
    }

    @Override
    public boolean isAlive()
    {
        return this.getRepresentation().isLoaded();
    }

    @Override
    public boolean isInvisible()
    {
        return this.getRepresentation().getOrElse(Keys.INVISIBLE, false);
    }

    @Override
    public void setInvisible(boolean visible)
    {
        this.getRepresentation().offer(Keys.INVISIBLE, visible);
    }

    @Override
    public boolean isInvulnerable()
    {
        return this.getRepresentation().getOrElse(Keys.INVULNERABLE, false);
    }

    @Override
    public void setInvulnerable(boolean invulnerable)
    {
        this.getRepresentation().offer(Keys.INVULNERABLE, invulnerable);
    }

    @Override
    public boolean hasGravity()
    {
        return this.getRepresentation().getOrElse(Keys.HAS_GRAVITY, false);
    }

    @Override
    public void setGravity(boolean gravity)
    {
        this.getRepresentation().offer(Keys.HAS_GRAVITY, gravity);
    }

    @Override
    public boolean summon(Location location)
    {
        return SpongeConversionUtil.toSponge(location)
                .map(spongeLocation -> spongeLocation.spawnEntity(this.getRepresentation()))
                .orElse(false);
    }

    @Override
    public boolean destroy()
    {
        this.getRepresentation().remove();
        return true;
    }

    @Override
    public E copy()
    {
        return SpongeConversionUtil.toSponge(this.getLocation())
                .map(spongeLocation -> {
                    try (StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame())
                    {
                        frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                        EntityArchetype archetype = this.getRepresentation()
                                .createArchetype();

                        @SuppressWarnings("unchecked") org.spongepowered.api.entity.Entity clone =
                                this.createSnapshot(
                                        (AbstractArchetype<EntityType, EntitySnapshot, org.spongepowered.api.entity.Entity>) archetype,
                                        spongeLocation
                                )
                                        .restore()
                                        .orElse(null);

                        return this.from(clone);
                    }
                }).rethrowUnchecked().orNull();
    }

    @Override
    public Location getLocation()
    {
        return SpongeConversionUtil.fromSponge(this.getRepresentation().getLocation());
    }

    @Override
    public void setLocation(Location location)
    {
        this.getRepresentation().setLocation(SpongeConversionUtil.toSponge(location).orNull());
    }

    @Override
    public World getWorld()
    {
        return SpongeConversionUtil.fromSponge(this.getRepresentation().getWorld());
    }

    /**
     * Gets the represented {@link org.spongepowered.api.entity.Entity}, typically this is a mixed-in instance of
     * {@link Entity}.
     *
     * @return The represented {@link org.spongepowered.api.entity.Entity}.
     */
    protected abstract org.spongepowered.api.entity.Entity getRepresentation();

    /**
     * Custom implementation of {@link org.spongepowered.common.entity.SpongeEntityArchetype#toSnapshot(org.spongepowered.api.world.Location)},
     * fixing the incompatibility with non-rotated entities. The original method did not include the rotation, scale, and type in the snapshot.
     *
     * @param archetype
     *         The entity archetype to use when creating the snapshot.
     * @param location
     *         The base location of the entity snapshot
     *
     * @return The new {@link EntitySnapshot}
     */
    private EntitySnapshot createSnapshot(AbstractArchetype<EntityType, EntitySnapshot, org.spongepowered.api.entity.Entity> archetype,
                                          org.spongepowered.api.world.Location<org.spongepowered.api.world.World> location)
    {
        final SpongeEntitySnapshotBuilder builder = new SpongeEntitySnapshotBuilder();
        builder.type(this.getEntityType());
        NBTTagCompound newCompound = archetype.getCompound().copy();
        newCompound.setTag("Pos", Constants.NBT
                .newDoubleNBTList(location.getPosition().getX(), location.getPosition().getY(), location.getPosition().getZ()));
        newCompound.setInteger("Dimension", ((WorldInfoBridge) location.getExtent().getProperties()).bridge$getDimensionId());
        builder.unsafeCompound(newCompound);
        builder.worldId(location.getExtent().getUniqueId());
        builder.position(location.getPosition());
        builder.rotation(this.getRepresentation().getRotation());
        builder.scale(this.getRepresentation().getScale());
        builder.type(this.getEntityType());
        return builder.build();
    }

    /**
     * Creates a new instance of {@link org.dockbox.selene.api.entities.Entity}, representing a native {@link org.spongepowered.api.entity.Entity}.
     *
     * @param clone
     *         The native entity to create the instance from
     *
     * @return The new {@link org.spongepowered.common.util.Constants.Entity} instance
     */
    protected abstract E from(org.spongepowered.api.entity.Entity clone);

    @Override
    public Exceptional<? extends DataHolder> getDataHolder()
    {
        return Exceptional.ofNullable(this.getRepresentation());
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getEntity()
    {
        return (T) this.getRepresentation();
    }
}
