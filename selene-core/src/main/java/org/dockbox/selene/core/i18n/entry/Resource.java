/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core.i18n.entry;

import org.dockbox.selene.core.i18n.common.Language;
import org.dockbox.selene.core.i18n.common.ResourceEntry;
import org.dockbox.selene.core.i18n.common.ResourceService;
import org.dockbox.selene.core.server.Selene;

import java.util.Map;

public class Resource implements ResourceEntry {

    private final String key;
    private final Map<Language, String> resourceMap = Selene.getInstance(ResourceService.class).getTranslations(this);
    private String value;

    public Resource(String value, String key) {
        this.value = value;
        this.key = key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getValue(Language lang) {
        if (this.resourceMap.containsKey(lang)) return this.resourceMap.get(lang);
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
