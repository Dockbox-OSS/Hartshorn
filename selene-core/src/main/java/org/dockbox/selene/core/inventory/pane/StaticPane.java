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

package org.dockbox.selene.core.inventory.pane;

import org.dockbox.selene.core.inventory.Element;
import org.dockbox.selene.core.inventory.InventoryType;
import org.dockbox.selene.core.inventory.InventoryLayout;
import org.dockbox.selene.core.inventory.properties.InventoryTypeProperty;
import org.dockbox.selene.core.inventory.builder.StaticPaneBuilder;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.util.SeleneUtils;

public interface StaticPane extends Pane {

    void set(Element element, int index);
    void set(Item item, int index);
    void update(InventoryLayout layout);

    static StaticPaneBuilder builder(InventoryType inventoryType) {
        return SeleneUtils.INJECT.getInstance(StaticPaneBuilder.class, new InventoryTypeProperty(inventoryType));
    }

}
