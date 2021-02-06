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

import org.dockbox.selene.core.objects.targets.InventoryHolder;
import org.dockbox.selene.core.objects.tuple.Vector3N;

/**
 * Represents an Armor Stand. See <a href="https://minecraft.gamepedia.com/Armor_Stand">Armor Stand on the Minecraft Wiki</a>.
 */
public interface ArmorStand extends Entity<ArmorStand>, InventoryHolder
{

    /**
     * Gets the rotation of the given {@link Limbs limb}, represented in a {@link Vector3N},
     * indicating the roll (x), pitch (y), and yaw (z) of the limb.
     *
     * <p>See <a href="http://planning.cs.uiuc.edu/node102.html">This document on Yaw, pitch,
     * and roll rotations</a> by Steven M. LaValle (2006, Cambridge University Press).</p>
     *
     * @param limb
     *         The limb to get the rotation of.
     *
     * @return The rotation of the limb, represented by a {@link Vector3N}.
     */
    Vector3N getRotation(Limbs limb);

    /**
     * Sets the rotation of the given {@link Limbs limb}, represented in a {@link Vector3N},
     * indicating the roll (x), pitch (y), and yaw (z) of the limb.
     *
     * <p>See <a href="http://planning.cs.uiuc.edu/node102.html">This document on Yaw, pitch,
     * and roll rotations</a> by Steven M. LaValle (2006, Cambridge University Press).</p>
     *
     * @param limb
     *         The limb to set the rotation of.
     * @param rotation
     *         The rotation of the limb, represented by a {@link Vector3N}.
     */
    void setRotation(Limbs limb, Vector3N rotation);

    /**
     * Indicates whether the armor stand has a baseplate present.
     *
     * @return {@code true} if the armor stand has a baseplate, else {@code false}
     */
    boolean hasBaseplate();

    /**
     * Sets whether the armor stand should have a baseplate.
     *
     * @param baseplate
     *         Whether a baseplate should be present.
     */
    void setBaseplate(boolean baseplate);

    /**
     * Indicates whether the armor stand is small or full-size.
     *
     * @return {@code true} if the armor stand is small, else {@code false}.
     */
    boolean isSmall();

    /**
     * Sets whether the armor stand should be small or full-size.
     *
     * @param small
     *         Whether the armor stand should be small.
     */
    void setSmall(boolean small);

    /**
     * Indicates whether the armor stand has arms visible.
     *
     * @return {@code true} if arms are visible, else {@code false}.
     */
    boolean hasArms();

    /**
     * Sets whether the armor stand should have arms visible.
     *
     * @param arms
     *         Whether armors should be visible
     */
    void setArms(boolean arms);

    enum Limbs
    {
        HEAD,
        BODY,
        LEFT_LEG,
        RIGHT_LEG,
        LEFT_ARM,
        RIGHT_ARM
    }

}
