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

package org.dockbox.selene.core.entities;

import org.dockbox.selene.core.objects.keys.KeyHolder;
import org.dockbox.selene.core.objects.keys.PersistentDataHolder;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.targets.Locatable;
import org.dockbox.selene.core.text.Text;

public interface Entity<T extends Entity<T>> extends Identifiable, Locatable, PersistentDataHolder, KeyHolder<T> {

    Text getDisplayName();

    void setDisplayName(Text displayName);

    double getHealth();

    void setHealth(double health);

    boolean isAlive();

    boolean isInvisible();

    void setInvisible(boolean visible);

    boolean isInvulnerable();

    void setInvulnerable(boolean invulnerable);

    boolean hasGravity();

    void setGravity(boolean gravity);

    boolean summon(Location location);

    default boolean summon() {
        return this.summon(this.getLocation());
    }

    boolean destroy();

    T copy();

}
