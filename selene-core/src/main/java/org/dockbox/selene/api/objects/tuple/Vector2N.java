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

package org.dockbox.selene.api.objects.tuple;

import java.util.Objects;

/**
 * Represents a 2D point on a x-, and z-axis. All axis points can be represented as any numeral
 * value.
 */
public class Vector2N {

    private final Number x;
    private final Number z;

    public Vector2N(Number x, Number z) {
        this.x = x;
        this.z = z;
    }

    public double getXd() {
        return this.x.doubleValue();
    }

    public float getXf() {
        return this.x.floatValue();
    }

    public int getXi() {
        return this.x.intValue();
    }

    public long getXl() {
        return this.x.longValue();
    }

    public double getZd() {
        return this.z.doubleValue();
    }

    public float getZf() {
        return this.z.floatValue();
    }

    public int getZi() {
        return this.z.intValue();
    }

    public long getZl() {
        return this.z.longValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector2N)) return false;
        Vector2N vector2N = (Vector2N) o;
        return x.equals(vector2N.x) && z.equals(vector2N.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
