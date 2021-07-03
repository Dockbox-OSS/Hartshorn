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
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.game.SpongeComposite;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.spongepowered.api.data.DataHolder.Mutable;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.UUID;

public interface SpongeEntity
        <T extends Entity, S extends org.spongepowered.api.entity.Entity>
        extends org.dockbox.hartshorn.server.minecraft.entities.Entity, SpongeComposite
{

    abstract EntityType<S> type();

    default UUID getUniqueId() {
        return null;
    }

    default String getName() {
        return null;
    }

    default Text getDisplayName() {
        return SpongeUtil.get(this.spongeEntity(), Keys.DISPLAY_NAME, SpongeConvert::fromSponge, Text::of);
    }

    default void setDisplayName(Text displayName) {
        this.spongeEntity().present(entity -> entity.offer(Keys.DISPLAY_NAME, SpongeConvert.toSponge(displayName)));
    }

    default double getHealth() {
        return this.spongeEntity().map(entity -> entity.get(Keys.HEALTH).orElse(0D)).or(0D);
    }

    default void setHealth(double health) {
        this.spongeEntity().present(entity -> entity.offer(Keys.HEALTH, health));
    }

    default boolean isAlive() {
        final boolean alive = this.spongeEntity().map(entity -> entity.isLoaded() && !entity.isRemoved()).or(false);
        return alive && this.getHealth() > 0;
    }

    default boolean isInvisible() {
        return this.getBoolean(Keys.IS_INVISIBLE);
    }

    default void setInvisible(boolean visible) {
        this.setBoolean(Keys.IS_INVISIBLE, visible);
    }

    default boolean isInvulnerable() {
        return this.getBoolean(Keys.INVULNERABLE);
    }

    default void setInvulnerable(boolean invulnerable) {
        this.setBoolean(Keys.INVULNERABLE, invulnerable);
    }

    default boolean hasGravity() {
        return this.getBoolean(Keys.IS_GRAVITY_AFFECTED);
    }

    default void setGravity(boolean gravity) {
        this.setBoolean(Keys.IS_GRAVITY_AFFECTED, gravity);
    }

    default boolean getBoolean(Key<Value<Boolean>> key) {
        return SpongeUtil.get(this.spongeEntity(), key, t -> t, () -> false);
    }

    default void setBoolean(Key<Value<Boolean>> key, boolean value) {
        this.spongeEntity().present(entity -> entity.offer(key, value));
    }

    default boolean summon(Location location) {
        if (!this.isAlive()) {
            return this.spongeEntity().map(entity -> {
                final Exceptional<ServerLocation> serverLocation = SpongeConvert.toSponge(location);
                if (serverLocation.absent()) return false;
                final ServerLocation loc = serverLocation.get();
                return loc.spawnEntity(entity);
            }).or(false);
        }
        return false;
    }

    default boolean destroy() {
        this.spongeEntity().present(S::remove);
        return true;
    }

    default Location getLocation() {
        return this.spongeEntity()
                .map(S::serverLocation)
                .map(SpongeConvert::fromSponge)
                .orElse(Location::empty)
                .get();
    }

    default void setLocation(Location location) {
        this.spongeEntity().present(entity -> SpongeConvert.toSponge(location).present(entity::setLocation));
    }

    default World getWorld() {
        return this.getLocation().getWorld();
    }

    default Exceptional<? extends Mutable> getDataHolder() {
        return this.spongeEntity();
    }

    /**
     * Native entity reference, this is used for packet data.
     *
     * @return The native Minecraft entity, if it exists.
     */
    default Exceptional<T> minecraftEntity() {
        //noinspection unchecked
        return this.spongeEntity().map(e -> (T) e);
    }

    public abstract Exceptional<S> spongeEntity();
}
