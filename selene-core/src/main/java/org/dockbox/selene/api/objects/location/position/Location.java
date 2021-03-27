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

package org.dockbox.selene.api.objects.location.position;

import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.dimensions.World;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.tuple.Vector3N;

import java.util.Objects;

public class Location {

    private final Vector3N vectorLoc;
    private final World world;

    public Location(double x, double y, double z, World world) {
        this(Vector3N.of(x, y, z), world);
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
        return this.expand(Vector3N.of(x, 0, 0));
    }

    public Location expand(Vector3N vector) {
        return new Location(this.vectorLoc.expand(vector), this.getWorld());
    }

    public World getWorld() {
        return this.world;
    }

    public Location expandY(double y) {
        return this.expand(Vector3N.of(0, y, 0));
    }

    public Location expandZ(double z) {
        return this.expand(Vector3N.of(0, 0, z));
    }

    public Vector3N getVectorLoc() {
        return this.vectorLoc;
    }

    public boolean place(Item item, Profile placer) {
        return this.place(item, BlockFace.NONE, placer);
    }

    public boolean place(Item item, BlockFace direction) {
        return this.place(item, direction, null);
    }

    public boolean place(Item item, BlockFace direction, Profile placer) {
        if (!item.isBlock() && !item.isAir()) return false;
        return this.getWorld().setBlock(this.getVectorLoc(), item, direction, placer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return this.getVectorLoc().equals(location.getVectorLoc()) && Objects.equals(this.getWorld(), location.getWorld());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getVectorLoc(), this.getWorld());
    }

}
