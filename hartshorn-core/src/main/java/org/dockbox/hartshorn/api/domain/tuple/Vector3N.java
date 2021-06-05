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

public class Vector3N extends Vector2N {

    private final Number y;

    public Vector3N(Number x, Number y, Number z) {
        super(x, z);
        this.y = y;
    }

    public static Vector3N of(Number x, Number y, Number z) {
        return new Vector3N(x, y, z);
    }

    public static Vector3N empty() {
        return Vector3N.of(0, 0, 0);
    }

    public float getYf() {
        return this.y.floatValue();
    }

    public int getYi() {
        return this.y.intValue();
    }

    public long getYl() {
        return this.y.longValue();
    }

    public Vector3N expand(Vector3N vector) {
        return Vector3N.of(
                this.getXd() + vector.getXd(),
                this.getYd() + vector.getYd(),
                this.getZd() + vector.getZd());
    }

    public double getYd() {
        return this.y.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vector3N)) return false;
        Vector3N vector3N = (Vector3N) o;
        return this.getYf() == vector3N.getYf() && this.getXf() == vector3N.getXf() && this.getZf() == vector3N.getZf();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getXd(), this.getYd(), this.getZd());
    }
}
