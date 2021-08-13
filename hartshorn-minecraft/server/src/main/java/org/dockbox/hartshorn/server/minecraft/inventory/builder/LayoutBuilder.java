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

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.annotations.PartialApi;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.Inventory;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.InventoryTypeAttribute;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.Collection;
import java.util.Map;

public class LayoutBuilder implements AttributeHolder {

    private final Map<Integer, Element> elements = HartshornUtils.emptyConcurrentMap();
    private InventoryType type;

    @Override
    public void apply(final Attribute<?> property) {
        if (property instanceof InventoryTypeAttribute inventoryTypeAttribute) {
            this.type = inventoryTypeAttribute.value();
        }
    }

    @Override
    public void enable() throws ApplicationException {
        if (this.type == null) throw new ApplicationException("Missing attribute for InventoryType");
    }

    @PartialApi
    public LayoutBuilder set(final Item item, final int index) {
        return this.set(Element.of(item), index);
    }

    @PartialApi
    public LayoutBuilder set(final Element element, final int index) {
        this.elements.put(index, element);
        return this;
    }

    @PartialApi
    public LayoutBuilder set(final Item item, final int... indices) {
        return this.set(Element.of(item), indices);
    }

    @PartialApi
    public LayoutBuilder set(final Element element, final int... indices) {
        for (final int index : indices) {
            this.set(element, index);
        }
        return this;
    }

    @PartialApi
    public LayoutBuilder row(final Item item, final int index) {
        return this.row(Element.of(item), index);
    }

    @PartialApi
    public LayoutBuilder row(final Element element, final int index) {
        if (index >= 0 && index < this.type.rows()) {
            final int start = index * this.type.columns();
            final int end = start + this.type.columns();
            for (int i = start; i < end; i++) {
                this.set(element, i);
            }
        }
        return this;
    }

    @PartialApi
    public LayoutBuilder column(final Item item, final int index) {
        return this.column(Element.of(item), index);
    }

    @PartialApi
    public LayoutBuilder column(final Element element, final int index) {
        for (int i = 0; i < this.type.rows(); i++) {
            final int next = i * this.type.columns();
            this.set(element, next + index);
        }
        return this;
    }

    @PartialApi
    public LayoutBuilder border(final Item item) {
        return this.border(Element.of(item));
    }

    @PartialApi
    public LayoutBuilder border(final Element element) {
        if (this.type.rows() > 0 && this.type.columns() > 0) {
            this.row(element, 0);
            this.row(element, this.type.rows()-1);
            this.column(element, 0);
            this.column(element, this.type.columns()-1);
        }
        return this;
    }

    @PartialApi
    public LayoutBuilder fill(final Item item) {
        return this.fill(Element.of(item));
    }

    @PartialApi
    public LayoutBuilder fill(final Element element) {
        for (int i = 0; i < this.type.size(); i++) {
            this.set(element, i);
        }
        return this;
    }

    @PartialApi
    public PaginatedPaneBuilder toPaginatedPaneBuilder() {
        return PaginatedPane.builder(this.build());
    }

    @PartialApi
    public StaticPaneBuilder toStaticPaneBuilder() {
        return StaticPane.builder(this.build());
    }

    public InventoryLayout build() {
        return Hartshorn.context().get(InventoryLayout.class, this.type, this.elements);
    }

    public LayoutBuilder add(final Inventory inventory) {
        return this.addItems(inventory.items());
    }

    public LayoutBuilder addItems(final Collection<Item> items) {
        for (final Item item : items) this.add(item);
        return this;
    }

    public LayoutBuilder addElements(final Collection<Element> elements) {
        for (final Element element : elements) this.add(element);
        return this;
    }

    private LayoutBuilder add(final Element element) {
        for (int i = 0; i < this.type.size(); i++) {
            if (this.elements.containsKey(i)) continue;

            this.elements.put(i, element);
            break;
        }
        return this;
    }

    public LayoutBuilder add(final Item item) {
        return this.add(Element.of(item));
    }
}
