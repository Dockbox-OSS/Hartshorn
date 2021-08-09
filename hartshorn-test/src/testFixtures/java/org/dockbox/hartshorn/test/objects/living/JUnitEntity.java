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

package org.dockbox.hartshorn.test.objects.living;

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.server.minecraft.entities.Entity;
import org.dockbox.hartshorn.test.objects.JUnitWorld;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public abstract class JUnitEntity<T extends Entity> implements Entity, PersistentDataHolder {

    @Getter @Setter private Text displayName;
    @Getter @Setter private double health = 20;
    @Getter private Location location;
    @Getter @Setter private boolean invisible = false;
    @Getter @Setter private boolean invulnerable = false;
    @Setter private boolean gravity = true;

    private final UUID uniqueId;

    public JUnitEntity(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Override
    public UUID uniqueId() {
        return this.uniqueId;
    }

    @Override
    public boolean summon(Location location) {
        ((JUnitWorld) location.world()).addEntity(this);
        this.location(location);
        return true;
    }

    @Override
    public boolean destroy() {
        ((JUnitWorld) this.location().world()).destroyEntity(this.uniqueId());
        return true;
    }

    @Override
    public String name() {
        return this.displayName().toPlain();
    }

    @Override
    public boolean alive() {
        return this.health() > 0;
    }

    @Override
    public boolean gravity() {
        return this.gravity;
    }

    @Override
    public boolean location(Location location) {
        ((JUnitWorld) this.world()).destroyEntity(this.uniqueId());
        this.location = location;
        ((JUnitWorld) this.world()).addEntity(this);
        return true;
    }

    @Override
    public World world() {
        return this.location().world();
    }
}
