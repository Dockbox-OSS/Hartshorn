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
import org.dockbox.hartshorn.server.minecraft.dimension.position.Location;
import org.dockbox.hartshorn.server.minecraft.entities.CloneableEntity;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.world.server.ServerLocation;

public abstract class SpongeCloneableEntityReference
        <E extends CloneableEntity<E>,
        M extends net.minecraft.world.entity.Entity,
        S extends org.spongepowered.api.entity.Entity>
    extends SpongeEntityReference<S>
    implements SpongeEntity<M, S>, SpongeCloneableEntity<E, S>
{
    public SpongeCloneableEntityReference(S entity) {
        super(entity);
    }

    public SpongeCloneableEntityReference(Location location) {
        super(null);
        final Exceptional<ServerLocation> exceptionalLocation = SpongeConvert.toSponge(location);
        if (exceptionalLocation.absent()) throw new IllegalArgumentException("Location cannot be converted to server location");

        final ServerLocation serverLocation = exceptionalLocation.get();
        final S entity = serverLocation.world().createEntity(this.type(), serverLocation.position());

        this.modify(entity);
    }
}
