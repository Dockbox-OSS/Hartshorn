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

import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.api.entity.annotations.Property;
import org.dockbox.hartshorn.persistence.PersistentModel;

import lombok.Getter;
import lombok.Setter;

@Entity(value = "language")
public class PersistentLanguageModel implements PersistentModel<Language> {

    @Property(getter = "getCode", setter = "getCode")
    @Getter @Setter
    private String code;

    public PersistentLanguageModel(String code) {
        this.code = code;
    }

    @Override
    public Class<Language> capableType() {
        return Language.class;
    }

    @Override
    public Language toPersistentCapable() {
        return Language.of(this.code());
    }
}
