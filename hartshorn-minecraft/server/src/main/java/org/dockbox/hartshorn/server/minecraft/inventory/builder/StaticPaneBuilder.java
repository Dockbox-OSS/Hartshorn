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

import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.server.minecraft.inventory.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.StaticPane;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.LayoutAttribute;

import java.util.function.Function;

import lombok.AccessLevel;
import lombok.Getter;

@SuppressWarnings("EmptyClass")
public abstract class StaticPaneBuilder extends DefaultPaneBuilder<StaticPane, StaticPaneBuilder> {

    @Getter(AccessLevel.PROTECTED)
    private InventoryLayout layout;

    @Override
    public void apply(final Attribute<?> property) {
        if (property instanceof LayoutAttribute layoutAttribute) {
            this.layout = layoutAttribute.value();
        }
    }

    @Override
    public void enable() throws ApplicationException {
        if (this.layout == null) throw new ApplicationException("Missing attribute for InventoryLayout");
    }

    @Override
    public StaticPaneBuilder onClickOutput(final Function<ClickContext, Boolean> onClick) {
        this.onClick(this.layout().inventoryType().size()-1, onClick);
        return this;
    }

    @Override
    protected StaticPaneBuilder self() {
        return this;
    }
}
