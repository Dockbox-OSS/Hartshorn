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

package org.dockbox.hartshorn.core.domain.tuple;

import java.util.Objects;

/**
 * Represents a 3D point on an x-, y-, and z-axis. All axis points can be represented as any numeral
 * value.
 */
public class Vector3N extends Vector2N {

    private Number y;

    protected Vector3N() {
        super();
    }

    private Vector3N(final Number x, final Number y, final Number z) {
        super(x, z);
        this.y = y;
    }

    /**
     * Creates a new empty {@link Vector3N}. An empty {@link Vector3N} contains only
     * zeroes.
     *
     * @return A new empty {@link Vector3N}
     */
    public static Vector3N empty() {
        return Vector3N.of(0, 0, 0);
    }

    /**
     * Creates a new {@link Vector3N} from the given values.
     *
     * @param x The x position of the {@link Vector3N}
     * @param y The y position of the {@link Vector3N}
     * @param z The z position of the {@link Vector3N}
     *
     * @return The new {@link Vector3N}
     */
    public static Vector3N of(final Number x, final Number y, final Number z) {
        return new Vector3N(x, y, z);
    }

    /**
     * Gets the position on the y-axis as an integer.
     *
     * @return The y-position as integer
     */
    public int yI() {
        return this.y.intValue();
    }

    /**
     * Gets the position on the y-axis as a long.
     *
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
     *
     * @param vector The {@link Vector3N} containing the expansion values
     *
     * @return The expanded {@link Vector3N}
     */
    public Vector3N expand(final Vector3N vector) {
        return Vector3N.of(
                this.xD() + vector.xD(),
                this.yD() + vector.yD(),
                this.zD() + vector.zD());
    }

    /**
     * Gets the position on the y-axis as a double.
     *
     * @return The y-position as double
     */
    public double yD() {
        return this.y.doubleValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.xD(), this.yD(), this.zD());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3N vector3N)) return false;
        return this.yF() == vector3N.yF() && this.xF() == vector3N.xF() && this.zF() == vector3N.zF();
    }

    /**
     * Gets the position on the y-axis as a float.
     *
     * @return The y-position as float
     */
    public float yF() {
        return this.y.floatValue();
    }

    @Override
    public String toString() {
        return "[x:%s,y:%s,z:%s]".formatted(this.xD(), this.yD(), this.zD());
    }
}
