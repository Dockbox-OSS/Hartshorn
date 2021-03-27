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

import org.dockbox.selene.api.entities.Entity;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.test.objects.JUnitPersistentDataHolder;
import org.dockbox.selene.test.objects.JUnitWorld;

import java.util.UUID;

public abstract class JUnitLivingEntity<T extends Entity<T>> extends JUnitGenericEntity<T> implements JUnitPersistentDataHolder {

    private final UUID uuid;

    public JUnitLivingEntity(UUID uuid) {
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
    public UUID getUniqueId() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.getDisplayName().toPlain();
    }
}
