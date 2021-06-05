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

package org.dockbox.hartshorn.sponge.inventory.builder;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryType;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.LayoutBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.InventoryTypeProperty;
import org.dockbox.hartshorn.sponge.inventory.SpongeInventoryLayout;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;

import dev.flashlabs.flashlibs.inventory.Layout;

public class SpongeLayoutBuilder extends LayoutBuilder {

    private Layout.Builder builder;
    private InventoryType inventoryType;

    @Override
    public LayoutBuilder set(Element element, int index) {
        this.builder.set(SpongeConversionUtil.toSponge(element), index);
        return this;
    }

    @Override
    public LayoutBuilder row(Element element, int index) {
        this.builder.row(SpongeConversionUtil.toSponge(element), index);
        return this;
    }

    @Override
    public LayoutBuilder column(Element element, int index) {
        this.builder.column(SpongeConversionUtil.toSponge(element), index);
        return this;
    }

    @Override
    public LayoutBuilder border(Element element) {
        this.builder.border(SpongeConversionUtil.toSponge(element));
        return this;
    }

    @Override
    public LayoutBuilder fill(Element element) {
        this.builder.fill(SpongeConversionUtil.toSponge(element));
        return this;
    }

    @Override
    public InventoryLayout build() {
        return new SpongeInventoryLayout(this.builder.build(), this.inventoryType);
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Bindings.value(InventoryTypeProperty.KEY, InventoryType.class, properties)
                .present(inventoryType -> this.inventoryType = inventoryType)
                .absent(() -> {
                    Hartshorn.log().warn("Missing inventory type argument, using default setting 'CHEST'");
                    this.inventoryType = InventoryType.CHEST;
                });
        this.builder = Layout.builder(this.inventoryType.getRows(), this.inventoryType.getColumns());
    }
}
