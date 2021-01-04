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

package org.dockbox.selene.core.inventory.builder;

import org.dockbox.selene.core.inventory.Element;
import org.dockbox.selene.core.inventory.InventoryLayout;
import org.dockbox.selene.core.inventory.pane.PaginatedPane;
import org.dockbox.selene.core.inventory.pane.StaticPane;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.server.properties.InjectableType;

public abstract class LayoutBuilder implements InjectableType {

    public abstract LayoutBuilder set(Element element, int index);

    public LayoutBuilder set(Element element, int... indices) {
        for (int index : indices) {
            this.set(element, index);
        }
        return this;
    }

    public abstract LayoutBuilder row(Element element, int index);

    public abstract LayoutBuilder column(Element element, int index);

    public abstract LayoutBuilder border(Element element);

    public abstract LayoutBuilder fill(Element element);

    public LayoutBuilder set(Item item, int index) {
        return this.set(Element.of(item), index);
    }

    public LayoutBuilder set(Item item, int... indices) {
        return this.set(Element.of(item), indices);
    }

    public LayoutBuilder row(Item item, int index) {
        return this.row(Element.of(item), index);
    }

    public LayoutBuilder column(Item item, int index) {
        return this.column(Element.of(item), index);
    }

    public LayoutBuilder border(Item item) {
        return this.border(Element.of(item));
    }

    public LayoutBuilder fill(Item item) {
        return this.fill(Element.of(item));
    }

    public PaginatedPaneBuilder toPaginatedPaneBuilder() {
        return PaginatedPane.builder(this.build());
    }

    public StaticPaneBuilder toStaticPaneBuilder() {
        return StaticPane.builder(this.build());
    }

    public abstract InventoryLayout build();

}
