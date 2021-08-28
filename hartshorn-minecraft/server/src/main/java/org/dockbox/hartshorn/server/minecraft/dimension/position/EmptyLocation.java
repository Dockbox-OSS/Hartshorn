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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Vector3N;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.TransactionResult;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.i18n.entry.FakeResource;
import org.dockbox.hartshorn.server.minecraft.dimension.world.World;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EmptyLocation extends Location {

    @Getter
    private ApplicationContext applicationContext;

    @Override
    public <T> Exceptional<T> get(final PersistentDataKey<T> dataKey) {
        return Exceptional.empty();
    }

    @Override
    public <T> TransactionResult set(final PersistentDataKey<T> dataKey, final T value) {
        return TransactionResult.fail(new FakeResource("Empty resource"));
    }

    @Override
    public <T> void remove(final PersistentDataKey<T> dataKey) {
        // Nothing happens
    }

    @Override
    public Map<PersistentDataKey<?>, Object> data() {
        return HartshornUtils.emptyMap();
    }

    @Override
    public Vector3N vector() {
        return Vector3N.empty();
    }

    @Override
    public Location expand(final Vector3N vector) {
        return this;
    }

    @Override
    public World world() {
        return World.empty(this.applicationContext());
    }
}
