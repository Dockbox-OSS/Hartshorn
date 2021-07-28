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

package org.dockbox.hartshorn.api.domain.tuple;

import java.util.Objects;

/**
 * Represents a 3D point on a x-, y-, and z-axis. All axis points can be represented as any numeral
 * value.
 */
public class Vector3N extends Vector2N {

    private final Number y;

    private Vector3N(Number x, Number y, Number z) {
        super(x, z);
        this.y = y;
    }

    /**
     * Creates a new {@link Vector3N} from the given values.
     * @param x The x position of the {@link Vector3N}
     * @param y The y position of the {@link Vector3N}
     * @param z The z position of the {@link Vector3N}
     * @return The new {@link Vector3N}
     */
    public static Vector3N of(Number x, Number y, Number z) {
        return new Vector3N(x, y, z);
    }

    /**
     * Creates a new empty {@link Vector3N}. An empty {@link Vector3N} contains only
     * zeroes.
     * @return A new empty {@link Vector3N}
     */
    public static Vector3N empty() {
        return Vector3N.of(0, 0, 0);
    }

    /**
     * Gets the position on the y-axis as a double.
     * @return The y-position as double
     */
    public double yD() {
        return this.y.doubleValue();
    }

    /**
     * Gets the position on the y-axis as a float.
     * @return The y-position as float
     */
    public float yF() {
        return this.y.floatValue();
    }

    /**
     * Gets the position on the y-axis as a integer.
     * @return The y-position as integer
     */
    public int yI() {
        return this.y.intValue();
    }

    /**
     * Gets the position on the y-axis as a long.
     * @return The y-position as long
     */
    public long yL() {
        return this.y.longValue();
    }

    /**
     * Expands the current {@link Vector3N} with the given {@link Vector3N} position. If the
     * current {@link Vector3N} is equal to [11,12,13] and the given {@link Vector3N} is equal
     * to [1,2,3] the output {@link Vector3N} is [12,14,16]. Both {@link Vector3N vectors} can
     * contain negative values.
     * @param vector The {@link Vector3N} containing the expansion values
     * @return The expanded {@link Vector3N}
     */
    public Vector3N expand(Vector3N vector) {
        return Vector3N.of(
                this.xD() + vector.xD(),
                this.yD() + vector.yD(),
                this.zD() + vector.zD());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3N vector3N)) return false;
        return this.yF() == vector3N.yF() && this.xF() == vector3N.xF() && this.zF() == vector3N.zF();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.xD(), this.yD(), this.zD());
    }
}
