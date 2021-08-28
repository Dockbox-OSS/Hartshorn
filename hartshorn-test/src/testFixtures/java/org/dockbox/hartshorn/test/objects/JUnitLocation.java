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

package org.dockbox.hartshorn.test.objects;

import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.annotations.inject.Bound;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;

import java.util.UUID;

import javax.inject.Inject;

import lombok.Getter;

@Binds(Location.class)
public class JUnitLocation extends Location implements JUnitPersistentDataHolder {

    private final Vector3N position;
    private final World world;
    private final UUID uniqueId;

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Bound
    public JUnitLocation(final World world) {
        this(world.spawnPosition(), world);
    }

    @Bound
    public JUnitLocation(final Vector3N position, final World world) {
        if (!(world instanceof JUnitWorld spongeWorld)) {
            throw new IllegalArgumentException("Provided world cannot be used as a JUnit reference");
        }
        this.world = spongeWorld;
        this.position = position;
        this.uniqueId = UUID.randomUUID();
    }

    @Override
    public Vector3N vector() {
        return this.position;
    }

    @Override
    public Location expand(final Vector3N vector) {
        return new JUnitLocation(this.position.expand(vector), this.world);
    }

    @Override
    public World world() {
        return this.world;
    }

    @Override
    public UUID uniqueId() {
        return this.uniqueId;
    }

    @Override
    public String name() {
        return "JUnitLocation";
    }
}
