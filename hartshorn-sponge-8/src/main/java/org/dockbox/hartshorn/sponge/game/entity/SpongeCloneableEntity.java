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

package org.dockbox.hartshorn.sponge.game.entity;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.server.minecraft.entities.CloneableEntity;
import org.spongepowered.api.entity.Entity;

public interface SpongeCloneableEntity<T extends CloneableEntity<T>, S extends Entity> extends CloneableEntity<T> {

    @Override
    default Exceptional<T> copy() {
        return this.spongeEntity().map(entity -> {
            try {
                final Entity copy = entity.copy();
                if (copy == null) return null;
                //noinspection unchecked
                return this.from((S) copy);
            } catch (ClassCastException e) {
                throw new ApplicationException(e);
            }
        });
    }

    T from(S entity);
    Exceptional<S> spongeEntity();

}
