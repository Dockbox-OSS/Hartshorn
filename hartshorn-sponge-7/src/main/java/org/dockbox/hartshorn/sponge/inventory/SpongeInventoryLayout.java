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

package org.dockbox.hartshorn.sponge.inventory;

import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Map;

import dev.flashlabs.flashlibs.inventory.Layout;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SpongeInventoryLayout implements InventoryLayout {

    private final Layout layout;
    private final InventoryType inventoryType;

    @Override
    public Map<Integer, Element> getElements() {
        Map<Integer, Element> elements = HartshornUtils.emptyMap();
        this.layout
                .getElements()
                .forEach((index, element) -> elements.put(index, SpongeConversionUtil.fromSponge(element)));
        return HartshornUtils.asUnmodifiableMap(elements);
    }
}
