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

import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.nms.entities.NMSEntity;
import org.dockbox.selene.sponge.objects.composite.SpongeComposite;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataTransactionResult.Type;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.entity.EntityType;

import java.util.UUID;

public abstract class SpongeEntity
        <T extends Entity, E extends org.dockbox.selene.core.entities.Entity<E>>
        extends NMSEntity<T>
        implements org.dockbox.selene.core.entities.Entity<E>, SpongeComposite {

    @SuppressWarnings("unchecked")
    protected <C extends org.spongepowered.api.entity.Entity> C create(Location location) {
        return SpongeConversionUtil.toSponge(location)
                .map(spongeLocation -> (C) spongeLocation.createEntity(this.getEntityType()))
                .orNull();
    }

    @Override
    public E copy() {
        return SpongeConversionUtil.toSponge(this.getLocation())
                .map(spongeLocation -> {
                    org.spongepowered.api.entity.Entity clone = spongeLocation.createEntity(this.getEntityType());
                    DataTransactionResult result = clone.copyFrom(this.getRepresentation());
                    if (Type.FAILURE == result.getType()) {
                        // Typically only time-related keys are rejected (e.g. invulnerability time)
                        for (DataManipulator<?, ?> container : this.getRepresentation().getContainers())
                            if (Type.FAILURE == clone.offer(container, MergeFunction.IGNORE_ALL).getType()) {
                                String key = container.getKeys().stream().findFirst().map(CatalogType::getId).orElse("unknown_key");
                                Selene.log().warn("Could not offer container key [" + key + "] to clone of " + this.getClass().getSimpleName());
                            }
                    }
                    return this.from(clone);
                }).orNull();
    }

    @Override
    public boolean isInvulnerable() {
        return this.getRepresentation().getOrElse(Keys.INVULNERABLE, false);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        this.getRepresentation().offer(Keys.INVULNERABLE, invulnerable);
    }

    @Override
    public UUID getUniqueId() {
        return this.getRepresentation().getUniqueId();
    }

    @Override
    public String getName() {
        return this.getDisplayName().toPlain();
    }

    @Override
    public void setName(String name) {
        this.setDisplayName(Text.of(name));
    }

    @Override
    public Location getLocation() {
        return SpongeConversionUtil.fromSponge(this.getRepresentation().getLocation());
    }

    @Override
    public void setLocation(Location location) {
        this.getRepresentation().setLocation(SpongeConversionUtil.toSponge(location).orNull());
    }

    @Override
    public World getWorld() {
        return SpongeConversionUtil.fromSponge(this.getRepresentation().getWorld());
    }

    @Override
    public Exceptional<? extends DataHolder> getDataHolder() {
        return Exceptional.ofNullable(this.getRepresentation());
    }

    @Override
    public Text getDisplayName() {
        return SpongeConversionUtil.fromSponge(this.getRepresentation()
                .getOrElse(Keys.DISPLAY_NAME, org.spongepowered.api.text.Text.EMPTY)
        );
    }

    @Override
    public void setDisplayName(Text displayName) {
        if (null == displayName) {
            this.getRepresentation().offer(Keys.CUSTOM_NAME_VISIBLE, false);
            this.getRepresentation().remove(Keys.DISPLAY_NAME);
        } else {
            this.getRepresentation().offer(Keys.DISPLAY_NAME, SpongeConversionUtil.toSponge(displayName));
            this.getRepresentation().offer(Keys.CUSTOM_NAME_VISIBLE, true);
        }
    }

    @Override
    public double getHealth() {
        return this.getRepresentation().getOrElse(Keys.HEALTH, -1D);
    }

    @Override
    public void setHealth(double health) {
        this.getRepresentation().offer(Keys.HEALTH, health);
    }

    @Override
    public boolean isInvisible() {
        return this.getRepresentation().getOrElse(Keys.INVISIBLE, false);
    }

    @Override
    public void setInvisible(boolean visible) {
        this.getRepresentation().offer(Keys.INVISIBLE, visible);
    }

    @Override
    public boolean hasGravity() {
        return this.getRepresentation().getOrElse(Keys.HAS_GRAVITY, false);
    }

    @Override
    public void setGravity(boolean gravity) {
        this.getRepresentation().offer(Keys.HAS_GRAVITY, gravity);
    }

    @Override
    public boolean summon(Location location) {
        return SpongeConversionUtil.toSponge(location)
                .map(spongeLocation -> spongeLocation.spawnEntity(this.getRepresentation()))
                .orElse(false);
    }

    @Override
    public boolean destroy() {
        this.getRepresentation().remove();
        return true;
    }

    @Override
    public boolean isAlive() {
        return this.getRepresentation().isLoaded();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getEntity() {
        return (T) this.getRepresentation();
    }

    protected abstract org.spongepowered.api.entity.Entity getRepresentation();

    protected abstract EntityType getEntityType();

    protected abstract E from(org.spongepowered.api.entity.Entity clone);
}
