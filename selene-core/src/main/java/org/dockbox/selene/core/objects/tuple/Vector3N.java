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

package org.dockbox.selene.core.objects.tuple;

public class Vector3N extends Vector2N {

    private final Number y;

    public Vector3N(Number x, Number y, Number z) {
        super(x, z);
        this.y = y;
    }

    public double getYd() { return this.y.doubleValue(); }

    public float getYf() { return this.y.floatValue(); }

    public int getYi() { return this.y.intValue(); }

    public long getYl() { return this.y.longValue(); }

    public Vector3N expand(Vector3N vector) {
        return new Vector3N(
                this.getXd() + vector.getXd(),
                this.getYd() + vector.getYd(),
                this.getZd() + vector.getZd()
        );
    }

    public static Vector3N empty() {
        return new Vector3N(0, 0, 0);
    }
}
