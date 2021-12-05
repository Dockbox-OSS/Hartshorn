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

package org.dockbox.hartshorn.core.domain.tuple;

import java.util.Objects;

/**
 * Represents a 2D point on a x-, and z-axis. All axis points can be represented as any numeral
 * value.
 */
public class Vector2N {

    private Number x;
    private Number z;

    protected Vector2N() {
    }

    protected Vector2N(final Number x, final Number z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Creates a new empty {@link Vector2N}. An empty {@link Vector2N} contains only
     * zeroes.
     *
     * @return A new empty {@link Vector2N}
     */
    public static Vector2N empty() {
        return Vector2N.of(0, 0);
    }

    /**
     * Creates a new {@link Vector2N} from the given values.
     *
     * @param x The x position of the {@link Vector2N}
     * @param z The z position of the {@link Vector2N}
     *
     * @return The new {@link Vector3N}
     */
    public static Vector2N of(final Number x, final Number z) {
        return new Vector2N(x, z);
    }

    /**
     * Gets the position on the x-axis as a double.
     *
     * @return The x-position as double
     */
    public double xD() {
        return this.x.doubleValue();
    }

    /**
     * Gets the position on the x-axis as a float.
     *
     * @return The x-position as float
     */
    public float xF() {
        return this.x.floatValue();
    }

    /**
     * Gets the position on the x-axis as an integer.
     *
     * @return The x-position as integer
     */
    public int xI() {
        return this.x.intValue();
    }

    /**
     * Gets the position on the x-axis as a long.
     *
     * @return The x-position as long
     */
    public long xL() {
        return this.x.longValue();
    }

    /**
     * Gets the position on the z-axis as a double.
     *
     * @return The z-position as double
     */
    public double zD() {
        return this.z.doubleValue();
    }

    /**
     * Gets the position on the z-axis as a float.
     *
     * @return The z-position as float
     */
    public float zF() {
        return this.z.floatValue();
    }

    /**
     * Gets the position on the z-axis as an integer.
     *
     * @return The z-position as integer
     */
    public int zI() {
        return this.z.intValue();
    }

    /**
     * Gets the position on the z-axis as a long.
     *
     * @return The z-position as long
     */
    public long zL() {
        return this.z.longValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.z);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2N vector2N)) return false;
        return this.x.equals(vector2N.x) && this.z.equals(vector2N.z);
    }
}
