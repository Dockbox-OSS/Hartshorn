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

package org.dockbox.hartshorn.i18n.common;

import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.PersistentModel;

import lombok.Getter;
import lombok.Setter;

public class PersistentLanguageModel implements PersistentModel<Language> {

    @Property(getter = "getCode", setter = "getCode")
    @Getter @Setter private String code;

    public PersistentLanguageModel(final String code) {
        this.code = code;
    }

    @Override
    public Class<Language> type() {
        return Language.class;
    }

    @Override
    public Language restore(final ApplicationContext context) {
        return Language.of(this.code());
    }
}
