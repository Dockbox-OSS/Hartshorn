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

import org.dockbox.hartshorn.api.annotations.PartialApi;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.inject.Required;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryHolder;

/**
 * Represents an Armor Stand. See <a href="https://minecraft.gamepedia.com/Armor_Stand">Armor Stand
 * on the Minecraft Wiki</a>.
 */
@Required
public interface ArmorStand extends CloneableEntity<ArmorStand>, InventoryHolder {

    /**
     * Gets the rotation of the given {@link Limbs limb}, represented in a {@link Vector3N},
     * indicating the roll (x), pitch (y), and yaw (z) of the limb.
     *
     * <p>See <a href="http://planning.cs.uiuc.edu/node102.html">This document on Yaw, pitch, and roll
     * rotations</a> by Steven M. LaValle (2006, Cambridge University Press).
     *
     * @param limb
     *         The limb to get the rotation of.
     *
     * @return The rotation of the limb, represented by a {@link Vector3N}.
     */
    @PartialApi
    Vector3N rotation(Limbs limb);

    /**
     * Sets the rotation of the given {@link Limbs limb}, represented in a {@link Vector3N},
     * indicating the roll (x), pitch (y), and yaw (z) of the limb.
     *
     * <p>See <a href="http://planning.cs.uiuc.edu/node102.html">This document on Yaw, pitch, and roll
     * rotations</a> by Steven M. LaValle (2006, Cambridge University Press).
     *
     * @param limb
     *         The limb to set the rotation of.
     * @param rotation
     *         The rotation of the limb, represented by a {@link Vector3N}.
     */
    @PartialApi
    void rotation(Limbs limb, Vector3N rotation);

    /**
     * Indicates whether the armor stand has a baseplate present.
     *
     * @return {@code true} if the armor stand has a baseplate, else {@code false}
     */
    @PartialApi
    boolean baseplate();

    /**
     * Sets whether the armor stand should have a baseplate.
     *
     * @param baseplate
     *         Whether a baseplate should be present.
     */
    @PartialApi
    ArmorStand baseplate(boolean baseplate);

    /**
     * Indicates whether the armor stand is small or full-size.
     *
     * @return {@code true} if the armor stand is small, else {@code false}.
     */
    @PartialApi
    boolean small();

    /**
     * Sets whether the armor stand should be small or full-size.
     *
     * @param small
     *         Whether the armor stand should be small.
     */
    @PartialApi
    ArmorStand small(boolean small);

    /**
     * Indicates whether the armor stand has arms visible.
     *
     * @return {@code true} if arms are visible, else {@code false}.
     */
    @PartialApi
    boolean arms();

    /**
     * Sets whether the armor stand should have arms visible.
     *
     * @param arms
     *         Whether armors should be visible
     */
    @PartialApi
    ArmorStand arms(boolean arms);

    @Override
    ArmorStandInventory inventory();

    enum Limbs {
        HEAD,
        BODY,
        LEFT_LEG,
        RIGHT_LEG,
        LEFT_ARM,
        RIGHT_ARM
    }
}
