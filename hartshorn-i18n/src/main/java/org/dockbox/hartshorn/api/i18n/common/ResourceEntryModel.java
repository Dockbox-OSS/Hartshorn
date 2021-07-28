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

package org.dockbox.hartshorn.api.i18n.common;

import org.dockbox.hartshorn.api.i18n.entry.Resource;
import org.dockbox.hartshorn.persistence.PersistentModel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ResourceEntryModel implements PersistentModel<ResourceEntry> {

    private String key;
    private String fallback;
    private Language language;

    @Override
    public Class<? extends ResourceEntry> capableType() {
        return ResourceEntry.class;
    }

    @Override
    public ResourceEntry toPersistentCapable() {
        return new Resource(this.fallback, this.key, this.language);
    }
}
