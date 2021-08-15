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

package org.dockbox.hartshorn.sponge.inventory.panes;

import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

@Binds(PaginatedPaneBuilder.class)
public class SpongePaginatedPaneBuilder extends PaginatedPaneBuilder {

    @Override
    public PaginatedPane build() {
        final InventoryMenu menu = ViewableInventory.builder()
                .type(SpongeConvert.toSponge(this.layout().inventoryType()))
                .completeStructure()
                .build().asMenu();

        if (this.title() != null) menu.setTitle(SpongeConvert.toSponge(this.title()));

        final PaginatedPane pane = new SpongePaginatedPane(menu, this.layout().inventoryType(), this.onClose(), this.actions());

        if (this.lock()) {
            for (int i = 0; i < this.layout().inventoryType().size(); i++) {
                pane.onClick(i, context -> false);
            }
        }

        this.listeners().forEach(pane::onClick);

        pane.elements(this.elements());

        return pane;
    }
}
