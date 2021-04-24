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

package org.dockbox.selene.api.i18n.common;

import org.dockbox.selene.api.entity.annotations.Accessor;
import org.dockbox.selene.api.entity.annotations.Metadata;
import org.dockbox.selene.persistence.PersistentModel;

@Metadata(alias = "language")
public class PersistentLanguageModel implements PersistentModel<Language> {

    @Accessor(getter = "getCode", setter = "getCode")
    private String code;

    public PersistentLanguageModel(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public Class<Language> getCapableType() {
        return Language.class;
    }

    @Override
    public Language toPersistentCapable() {
        return Language.of(this.getCode());
    }
}
