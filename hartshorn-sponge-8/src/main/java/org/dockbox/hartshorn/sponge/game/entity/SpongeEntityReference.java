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
import org.spongepowered.api.entity.Entity;

import java.lang.ref.WeakReference;

public abstract class SpongeEntityReference<E extends Entity> {

    private WeakReference<E> entity;

    public SpongeEntityReference(E entity) {
        this.modify(entity);
    }

    protected final void modify(E entity) {
        this.entity = new WeakReference<>(entity);
    }

    protected Exceptional<E> entity() {
        return Exceptional.of(this.entity.get());
    }

}
