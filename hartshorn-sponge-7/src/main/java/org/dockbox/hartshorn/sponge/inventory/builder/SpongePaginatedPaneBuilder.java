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
import org.dockbox.hartshorn.api.i18n.text.Text;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.InventoryLayout;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.inventory.properties.InventoryTypeProperty;
import org.dockbox.hartshorn.sponge.Sponge7Application;
import org.dockbox.hartshorn.sponge.inventory.SpongeInventoryLayout;
import org.dockbox.hartshorn.sponge.inventory.pane.SpongePaginatedPane;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.item.inventory.InventoryArchetypes;

import java.util.Collection;
import java.util.stream.Collectors;

import dev.flashlabs.flashlibs.inventory.Page;

public class SpongePaginatedPaneBuilder extends PaginatedPaneBuilder {

    private Page.Builder builder;
    private Collection<Element> elements = HartshornUtils.emptyList();

    @Override
    public PaginatedPaneBuilder elements(Collection<Element> elements) {
        this.elements = elements;
        return this;
    }

    @Override
    public PaginatedPaneBuilder title(Text text) {
        this.builder.title(ctx -> SpongeConversionUtil.toSponge(text));
        return this;
    }

    @Override
    public PaginatedPane build() {
        Page page = this.builder.build(Sponge7Application.container());
        page.define(this.elements.stream()
                .map(SpongeConversionUtil::toSponge)
                .collect(Collectors.toList())
        );
        return new SpongePaginatedPane(page);
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Bindings.value(InventoryTypeProperty.KEY, InventoryLayout.class, properties)
                .present(layout -> {
                    this.builder = Page.builder(SpongeConversionUtil.toSponge(layout.getInventoryType()));
                    this.layout(layout);
                })
                .absent(() -> {
                    Hartshorn.log().warn("Missing inventory type argument, using default setting 'CHEST'");
                    this.builder = Page.builder(InventoryArchetypes.CHEST);
                });
    }

    public void layout(InventoryLayout layout) {
        if (layout instanceof SpongeInventoryLayout)
            this.builder.layout(((SpongeInventoryLayout) layout).getLayout());
    }
}
