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

package org.dockbox.selene.api.objects.location;

import org.dockbox.selene.api.objects.tuple.Vector3N;

public class Location {

    private final Vector3N vectorLoc;
    private final World world;

    public Location(double x, double y, double z, World world) {
        this(new Vector3N(x, y, z), world);
    }

    public Location(Vector3N vectorLoc, World world) {
        this.vectorLoc = vectorLoc;
        this.world = world;
    }

    public static Location empty() {
        return new Location(0, 0, 0, World.empty());
    }

    public double getX() {
        return this.vectorLoc.getXd();
    }

    public double getY() {
        return this.vectorLoc.getYd();
    }

    public double getZ() {
        return this.vectorLoc.getZd();
    }

    public Location expandX(double x) {
        return this.expand(new Vector3N(x, 0, 0));
    }

    public Location expand(Vector3N vector) {
        return new Location(this.vectorLoc.expand(vector), this.getWorld());
    }

    public World getWorld() {
        return this.world;
    }

    public Location expandY(double y) {
        return this.expand(new Vector3N(0, y, 0));
    }

    public Location expandZ(double z) {
        return this.expand(new Vector3N(0, 0, z));
    }

    public Vector3N getVectorLoc() {
        return this.vectorLoc;
    }
}
