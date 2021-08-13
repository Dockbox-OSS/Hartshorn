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

package org.dockbox.hartshorn.server.minecraft.inventory.builder;

import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.item.Item;

public abstract class LayoutBuilder implements AttributeHolder {

    public LayoutBuilder set(final Item item, final int index) {
        return this.set(Element.of(item), index);
    }

    public abstract LayoutBuilder set(Element element, int index);

    public LayoutBuilder set(final Item item, final int... indices) {
        return this.set(Element.of(item), indices);
    }

    public LayoutBuilder set(final Element element, final int... indices) {
        for (final int index : indices) {
            this.set(element, index);
        }
        return this;
    }

    public LayoutBuilder row(final Item item, final int index) {
        return this.row(Element.of(item), index);
    }

    public abstract LayoutBuilder row(Element element, int index);

    public LayoutBuilder column(final Item item, final int index) {
        return this.column(Element.of(item), index);
    }

    public abstract LayoutBuilder column(Element element, int index);

    public LayoutBuilder border(final Item item) {
        return this.border(Element.of(item));
    }

    public abstract LayoutBuilder border(Element element);

    public LayoutBuilder fill(final Item item) {
        return this.fill(Element.of(item));
    }

    public abstract LayoutBuilder fill(Element element);

    public PaginatedPaneBuilder toPaginatedPaneBuilder() {
        return PaginatedPane.builder(this.build());
    }

    public abstract InventoryLayout build();

    public StaticPaneBuilder toStaticPaneBuilder() {
        return StaticPane.builder(this.build());
    }
}
