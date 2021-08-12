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

package org.dockbox.hartshorn.server.minecraft.dimension.position;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.keys.KeyHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.di.annotations.inject.Required;
import org.dockbox.hartshorn.server.minecraft.dimension.Block;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;

import java.util.Objects;

@Required
public abstract class Location implements KeyHolder<Location>, PersistentDataHolder {

    public static Location empty() {
        return new EmptyLocation();
    }

    public static Location of(World world) {
        return Hartshorn.context().get(Location.class, world);
    }

    public static Location of(int x, int y, int z, World world) {
        return of(Vector3N.of(x, y, z), world);
    }

    public static Location of(Vector3N position, World world) {
        return Hartshorn.context().get(Location.class, position, world);
    }

    public double getX() {
        return this.vector().xD();
    }

    public abstract Vector3N vector();

    public double getY() {
        return this.vector().yD();
    }

    public double getZ() {
        return this.vector().zD();
    }

    public Location expandX(double x) {
        return this.expand(Vector3N.of(x, 0, 0));
    }

    public abstract Location expand(Vector3N vector);

    public Location expandY(double y) {
        return this.expand(Vector3N.of(0, y, 0));
    }

    public Location expandZ(double z) {
        return this.expand(Vector3N.of(0, 0, z));
    }

    public boolean place(Block block) {
        return block.place(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.vector(), this.world());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location location)) return false;
        return this.vector().equals(location.vector()) && Objects.equals(this.world(), location.world());
    }

    public abstract World world();

}
