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

package org.dockbox.selene.palswap.fileparsers;

import org.dockbox.selene.api.entity.annotations.Entity;
import org.dockbox.selene.api.entity.annotations.Property;
import org.dockbox.selene.util.SeleneUtils;

import java.util.Map;

@Entity(value = "itemdata")
public class ItemData {

    @Property(getter = "getItemRegistry")
    private Map<String, String> itemRegistry = SeleneUtils.emptyConcurrentMap();

    @Property(getter = "getBlockIdentifierIDs")
    private Map<String, String> blockIdentifierIds = SeleneUtils.emptyConcurrentMap();

    public static ItemData of(Map<String, String> itemRegistry, Map<String, String> blockIdentifierIDs) {
        ItemData instance = new ItemData();
        instance.itemRegistry = itemRegistry;
        instance.blockIdentifierIds = blockIdentifierIDs;
        return instance;
    }

    public Map<String, String> getItemRegistry() {
        return this.itemRegistry;
    }

    public Map<String, String> getBlockIdentifierIDs() {
        return this.blockIdentifierIds;
    }
}
