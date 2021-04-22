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

package org.dockbox.selene.sponge.inventory;

import org.dockbox.selene.api.inventory.Element;
import org.dockbox.selene.api.inventory.InventoryLayout;
import org.dockbox.selene.api.inventory.InventoryType;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;

import java.util.Map;

import dev.flashlabs.flashlibs.inventory.Layout;

public class SpongeInventoryLayout implements InventoryLayout {

    private final Layout layout;
    private final InventoryType inventoryType;

    public SpongeInventoryLayout(Layout initialLayout, InventoryType inventoryType) {
        this.layout = initialLayout;
        this.inventoryType = inventoryType;
    }

    @Override
    public Map<Integer, Element> getElements() {
        Map<Integer, Element> elements = SeleneUtils.emptyMap();
        this.layout
                .getElements()
                .forEach((index, element) -> elements.put(index, SpongeConversionUtil.fromSponge(element)));
        return SeleneUtils.asUnmodifiableMap(elements);
    }

    @Override
    public InventoryType getIventoryType() {
        return this.inventoryType;
    }

    public Layout getLayout() {
        return this.layout;
    }
}
