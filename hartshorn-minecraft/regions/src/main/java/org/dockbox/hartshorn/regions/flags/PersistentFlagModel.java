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

package org.dockbox.hartshorn.regions.flags;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntryModel;
import org.dockbox.hartshorn.util.Reflect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PersistentFlagModel {

    @Getter
    private String id;
    private ResourceEntryModel description;
    private String type;

    public RegionFlag<?> restore() {
        final Class<?> flagType = Reflect.lookup(this.type);
        if (Reflect.assigns(RegionFlag.class, flagType)) {
            return (RegionFlag<?>) Hartshorn.context().get(flagType, this.id, this.description.toPersistentCapable());
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentFlagModel that)) return false;
        return this.id.equals(that.id);
    }
}
