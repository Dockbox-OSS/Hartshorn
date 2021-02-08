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

package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.objects.keys.PersistentDataHolder;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.targets.Locatable;
import org.dockbox.selene.core.text.Text;

/**
 * An entity is a Minecraft entity.
 *
 * <p>Examples of entities include:</p>
 *
 * <ul>
 *     <li>Zombies</li>
 *     <li>Sheep</li>
 *     <li>Players</li>
 *     <li>Dropped items</li>
 *     <li>Dropped experience points</li>
 *     <li>etc.</li>
 * </ul>
 *
 * <p>Blocks and items (when they are in inventories) are not entities.</p>
 *
 * @param <T>
 *         The type of {@link Entity}
 */
public interface Entity<T extends Entity<T>> extends Identifiable, Locatable, PersistentDataHolder, KeyHolder<T>
{

    @SuppressWarnings("ConstantDeclaredInInterface")
    double DEFAULT_MAX_HEALTH = 20D;

    /**
     * Gets the display name of the entity. If no display name is present, an empty {@link Text} instance is
     * returned.
     *
     * @return The display name of the entity, or an empty {@link Text} instance.
     */
    Text getDisplayName();

    /**
     * Sets the display name of the entity.
     *
     * @param displayName
     *         The display name of the entity.
     */
    void setDisplayName(Text displayName);

    /**
     * Gets the current health of the entity in the form of the total HP.
     *
     * @return The current health of the entity.
     */
    double getHealth();

    /**
     * Sets the health of the entity in the form of the total HP. If the given value is higher than
     * the maximum health of the entity, the maximum health is applied instead.
     *
     * @param health
     *         The new health of the entity.
     */
    void setHealth(double health);

    /**
     * Indicates whether the entity is alive inside a {@link org.dockbox.selene.core.objects.location.World}.
     *
     * @return {@code true} if the entity is alive and loaded in a world, else {@code false}.
     */
    boolean isAlive();

    /**
     * Indicates whether the entity is invisible.
     *
     * @return {@code true} if the entity is invisible, else {@code false}
     */
    boolean isInvisible();

    /**
     * Sets whether the entity should be invisible. Depending on the type of entity, inventory items may still be
     * visible (e.g. armor, held items, etc).
     *
     * @param visible
     *         Whether the entity should be visible.
     */
    void setInvisible(boolean visible);

    /**
     * Indicates whether the entity is invulnerable. This protects the entity from being damaged by other
     * entities, explosions, etc. However it does not protect it from being affected by {@link #setHealth(double)}
     * and {@link #destroy()}.
     *
     * @return Whether the entity is invulnerable.
     */
    boolean isInvulnerable();

    /**
     * Sets whether the entity should be invulnerable.
     *
     * @param invulnerable
     *         Whether the entity should be invulnerable.
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * Indicates whether the entity is affected by gravity.
     *
     * @return {@code true} if the entity is affected by gravity, or {@code false}.
     */
    boolean hasGravity();

    /**
     * Sets whether the entity should be affected by gravity. If the entity does not support gravity physics,
     * as seen in e.g. Item Frames, nothing is done.
     *
     * @param gravity
     *         Whether the entity should be affected by gravity.
     */
    void setGravity(boolean gravity);

    /**
     * Summons the entity into a world. The location at which the entity is summoned is provided by its
     * base {@link Location}, set during the creation of the entity.
     *
     * @return {@code true} if the entity was summoned successfully, else {@code false}.
     */
    default boolean summon()
    {
        return this.summon(this.getLocation());
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
     * Destroys (kills) the entity, removing it from the world. This prevents the entity from being summoned again through
     * {@link #summon()}.
     *
     * @return {@code true} if the entity was destroyed successfully, else {@code false}.
     */
    boolean destroy();

    /**
     * Creates a copy of the entity, copying its default and custom data provided by {@link org.dockbox.selene.core.objects.keys.Key}s
     * and {@link org.dockbox.selene.core.objects.keys.PersistentDataKey}s.
     *
     * @return The copy of the current entity.
     */
    T copy();

}
