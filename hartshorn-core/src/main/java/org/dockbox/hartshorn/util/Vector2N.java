/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.util;

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
