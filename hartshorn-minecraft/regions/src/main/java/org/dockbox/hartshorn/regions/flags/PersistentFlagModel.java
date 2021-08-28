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

import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.i18n.common.Language;
import org.dockbox.hartshorn.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.i18n.common.ResourceEntryModel;
import org.dockbox.hartshorn.persistence.PersistentModel;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PersistentFlagModel {

    @Id
    @Getter private String id;
    private String descriptionKey;
    private String descriptionFallback;
    private String descriptionLanguage;
    private String type;

    public PersistentFlagModel(final String id, final ResourceEntry description, final String type) {
        this.id = id;
        this.descriptionKey = description.key();
        this.descriptionFallback = description.asString();
        this.descriptionLanguage = description.language().name();
        this.type = type;
    }

    public RegionFlag<?> restore(final ApplicationContext context) {
        final TypeContext<?> flagType = TypeContext.lookup(this.type);

        if (flagType.childOf(RegionFlag.class)) {
            final PersistentModel<ResourceEntry> description = new ResourceEntryModel(this.descriptionKey, this.descriptionFallback, Language.valueOf(this.descriptionLanguage));
            return (RegionFlag<?>) context.get(flagType, this.id, description.restore(context));
        }
        return null;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PersistentFlagModel that)) return false;
        return this.id.equals(that.id);
    }
}
