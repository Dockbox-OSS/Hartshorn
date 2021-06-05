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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.api.keys.PersistentDataHolder;
import org.dockbox.hartshorn.api.keys.PersistentDataKey;
import org.dockbox.hartshorn.api.keys.TransactionResult;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unchecked")
public interface JUnitPersistentDataHolder extends PersistentDataHolder, Identifiable {

    Map<UUID, Map<PersistentDataKey<?>, Object>> DATA = HartshornUtils.emptyMap();

    @Override
    default <T> Exceptional<T> get(PersistentDataKey<T> dataKey) {
        return Exceptional.of((T) this.getPersistentData().getOrDefault(dataKey, null));
    }

    @Override
    default <T> TransactionResult set(PersistentDataKey<T> dataKey, T value) {
        Map<PersistentDataKey<?>, Object> persistentData = this.getPersistentData();
        persistentData.put(dataKey, value);
        DATA.put(this.getUniqueId(), persistentData);
        return TransactionResult.success();
    }

    @Override
    default <T> void remove(PersistentDataKey<T> dataKey) {
        Map<PersistentDataKey<?>, Object> persistentData = this.getPersistentData();
        persistentData.remove(dataKey);
        DATA.put(this.getUniqueId(), persistentData);
    }

    @Override
    default Map<PersistentDataKey<?>, Object> getPersistentData() {
        DATA.putIfAbsent(this.getUniqueId(), HartshornUtils.emptyMap());
        return DATA.get(this.getUniqueId());
    }
}
