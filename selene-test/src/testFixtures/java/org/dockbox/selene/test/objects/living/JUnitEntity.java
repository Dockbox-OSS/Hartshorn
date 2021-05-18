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

package org.dockbox.selene.test.objects.living;

import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.keys.PersistentDataHolder;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.dimension.world.World;
import org.dockbox.selene.server.minecraft.entities.Entity;
import org.dockbox.selene.test.objects.JUnitWorld;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public abstract class JUnitEntity<T extends Entity> implements Entity, PersistentDataHolder {

    @Getter
    private Text displayName;
    @Getter @Setter
    private double health = 20;
    @Getter
    private Location location;
    @Getter @Setter
    private boolean invisible = false;
    @Getter @Setter
    private boolean invulnerable = false;
    @Setter
    private boolean gravity = true;
    @Getter
    private final UUID uuid;

    public JUnitEntity(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean summon(Location location) {
        ((JUnitWorld) location.getWorld()).addEntity(this);
        this.setLocation(location);
        return true;
    }

    @Override
    public boolean destroy() {
        ((JUnitWorld) this.getLocation().getWorld()).destroyEntity(this.getUniqueId());
        return true;
    }

    @Override
    public String getName() {
        return this.getDisplayName().toPlain();
    }

    @Override
    public boolean isAlive() {
        return this.getHealth() > 0;
    }

    @Override
    public boolean hasGravity() {
        return this.gravity;
    }

    @Override
    public void setLocation(Location location) {
        ((JUnitWorld) this.getWorld()).destroyEntity(this.getUniqueId());
        this.location = location;
        ((JUnitWorld) this.getWorld()).addEntity(this);
    }

    @Override
    public World getWorld() {
        return this.getLocation().getWorld();
    }
}
