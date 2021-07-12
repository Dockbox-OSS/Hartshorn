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

package org.dockbox.hartshorn.server.minecraft.entities;

import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.server.minecraft.dimension.Locatable;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;

/**
 * An entity is a Minecraft entity.
 *
 * <p>Examples of entities include:
 *
 * <ul>
 *   <li>Zombies
 *   <li>Sheep
 *   <li>Players
 *   <li>Dropped items
 *   <li>Dropped experience points
 *   <li>etc.
 * </ul>
 *
 * <p>Blocks and items (when they are in inventories) are not entities.
 */
public interface Entity extends Identifiable, Locatable, PersistentDataHolder, KeyHolder<Entity> {

    @SuppressWarnings("ConstantDeclaredInInterface")
    double DEFAULT_MAX_HEALTH = 20D;

    /**
     * Gets the display name of the entity. If no display name is present, an empty {@link Text}
     * instance is returned.
     *
     * @return The display name of the entity, or an empty {@link Text} instance.
     */
    Text displayName();

    /**
     * Sets the display name of the entity.
     *
     * @param displayName
     *         The display name of the entity.
     */
    Entity displayName(Text displayName);

    /**
     * Gets the current health of the entity in the form of the total HP.
     *
     * @return The current health of the entity.
     */
    double health();

    /**
     * Sets the health of the entity in the form of the total HP. If the given value is higher than
     * the maximum health of the entity, the maximum health is applied instead.
     *
     * @param health
     *         The new health of the entity.
     */
    Entity health(double health);

    /**
     * Indicates whether the entity is alive inside a {@link
     * World}.
     *
     * @return {@code true} if the entity is alive and loaded in a world, else {@code false}.
     */
    boolean alive();

    /**
     * Indicates whether the entity is invisible.
     *
     * @return {@code true} if the entity is invisible, else {@code false}
     */
    boolean invisible();

    /**
     * Sets whether the entity should be invisible. Depending on the type of entity, inventory items
     * may still be visible (e.g. armor, held items, etc).
     *
     * @param visible
     *         Whether the entity should be visible.
     */
    Entity invisible(boolean visible);

    /**
     * Indicates whether the entity is invulnerable. This protects the entity from being damaged by
     * other entities, explosions, etc. However it does not protect it from being affected by {@link
     * #health(double)} and {@link #destroy()}.
     *
     * @return Whether the entity is invulnerable.
     */
    boolean invulnerable();

    /**
     * Sets whether the entity should be invulnerable.
     *
     * @param invulnerable
     *         Whether the entity should be invulnerable.
     */
    Entity invulnerable(boolean invulnerable);

    /**
     * Indicates whether the entity is affected by gravity.
     *
     * @return {@code true} if the entity is affected by gravity, or {@code false}.
     */
    boolean gravity();

    /**
     * Sets whether the entity should be affected by gravity. If the entity does not support gravity
     * physics, as seen in e.g. Item Frames, nothing is done.
     *
     * @param gravity
     *         Whether the entity should be affected by gravity.
     */
    Entity gravity(boolean gravity);

    /**
     * Summons the entity into a world. The location at which the entity is summoned is provided by
     * its base {@link Location}, set during the creation of the entity.
     *
     * @return {@code true} if the entity was summoned successfully, else {@code false}.
     */
    default boolean summon() {
        return this.summon(this.location());
    }

    /**
     * Summons the entity at a given {@link Location}.
     *
     * @param location
     *         The location to summon the entity at.
     *
     * @return {@code true} if the entity was summoned successfully, else {@code false}.
     */
    boolean summon(Location location);

    /**
     * Destroys (kills) the entity, removing it from the world. This prevents the entity from being
     * summoned again through {@link #summon()}.
     *
     * @return {@code true} if the entity was destroyed successfully, else {@code false}.
     */
    boolean destroy();

}
