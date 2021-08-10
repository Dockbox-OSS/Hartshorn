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
import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.sponge.game.SpongeComposite;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.dockbox.hartshorn.sponge.util.SpongeUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.data.DataHolder.Mutable;
import org.spongepowered.api.data.Key;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.server.ServerLocation;

import java.util.UUID;

public interface SpongeEntity
        <T extends Entity, S extends org.spongepowered.api.entity.Entity>
        extends org.dockbox.hartshorn.server.minecraft.entities.Entity, SpongeComposite {

    abstract EntityType<S> type();

    default UUID uniqueId() {
        return this.spongeEntity().map(org.spongepowered.api.entity.Entity::uniqueId)
                .orElse(() -> HartshornUtils.EMPTY_UUID)
                .get();
    }

    default String name() {
        return this.displayName().toPlain();
    }

    default Text displayName() {
        return SpongeUtil.get(this.spongeEntity(), Keys.DISPLAY_NAME, SpongeConvert::fromSponge, Text::of);
    }

    default SpongeEntity<T, S> displayName(Text displayName) {
        this.spongeEntity().present(entity -> entity.offer(Keys.DISPLAY_NAME, SpongeConvert.toSponge(displayName)));
        return this;
    }

    default double health() {
        return this.spongeEntity().map(entity -> entity.get(Keys.HEALTH).orElse(0D)).or(0D);
    }

    default SpongeEntity<T, S> health(double health) {
        this.spongeEntity().present(entity -> entity.offer(Keys.HEALTH, health));
        return this;
    }

    default boolean alive() {
        final boolean alive = this.spongeEntity().map(entity -> entity.isLoaded() && !entity.isRemoved()).or(false);
        return alive && this.health() > 0;
    }

    default boolean invisible() {
        return this.bool(Keys.IS_INVISIBLE);
    }

    default SpongeEntity<T, S> invisible(boolean visible) {
        this.bool(Keys.IS_INVISIBLE, visible);
        return this;
    }

    default boolean invulnerable() {
        return this.bool(Keys.INVULNERABLE);
    }

    default SpongeEntity<T, S> invulnerable(boolean invulnerable) {
        this.bool(Keys.INVULNERABLE, invulnerable);
        return this;
    }

    default boolean gravity() {
        return this.bool(Keys.IS_GRAVITY_AFFECTED);
    }

    default SpongeEntity<T, S> gravity(boolean gravity) {
        this.bool(Keys.IS_GRAVITY_AFFECTED, gravity);
        return this;
    }

    default boolean summon(Location location) {
        if (!this.alive()) {
            return this.spongeEntity().map(entity -> {
                final Exceptional<ServerLocation> serverLocation = SpongeConvert.toSponge(location);
                if (serverLocation.absent()) return false;
                final ServerLocation loc = serverLocation.get();
                return loc.spawnEntity(entity);
            }).or(false);
        }
        else {
            return this.location(location);
        }
    }

    default boolean destroy() {
        this.spongeEntity().present(S::remove);
        return true;
    }

    default void bool(Key<Value<Boolean>> key, boolean value) {
        this.spongeEntity().present(entity -> entity.offer(key, value));
    }

    default boolean bool(Key<Value<Boolean>> key) {
        return SpongeUtil.get(this.spongeEntity(), key, t -> t, () -> false);
    }

    public abstract Exceptional<S> spongeEntity();    default Location location() {
        return this.spongeEntity()
                .map(S::serverLocation)
                .map(SpongeConvert::fromSponge)
                .orElse(Location::empty)
                .get();
    }

    default Exceptional<? extends Mutable> dataHolder() {
        return this.spongeEntity();
    }    default boolean location(Location location) {
        return this.spongeEntity().map(entity -> SpongeConvert.toSponge(location)
                .map(entity::setLocation).or(false)).or(false);
    }

    /**
     * Native entity reference, this is used for packet data.
     *
     * @return The native Minecraft entity, if it exists.
     */
    default Exceptional<T> minecraftEntity() {
        //noinspection unchecked
        return this.spongeEntity().map(e -> (T) e);
    }    default World world() {
        return this.location().world();
    }






}
