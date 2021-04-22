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

package org.dockbox.selene.sponge.inventory.pane;

import org.dockbox.selene.api.inventory.Element;
import org.dockbox.selene.api.inventory.InventoryLayout;
import org.dockbox.selene.minecraft.inventory.pane.StaticPane;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.minecraft.players.Player;
import org.dockbox.selene.sponge.inventory.SpongeInventoryLayout;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;

import dev.flashlabs.flashlibs.inventory.View;

public class SpongeStaticPane implements StaticPane {

    private final View view;

    public SpongeStaticPane(View initializedView) {
        this.view = initializedView;
    }

    @Override
    public void open(Player player) {
        SpongeConversionUtil.toSponge(player).present(this.view::open);
    }

    @Override
    public void set(Element element, int index) {
        this.view.set(SpongeConversionUtil.toSponge(element), index);
    }

    @Override
    public void set(Item item, int index) {
        this.view.set(SpongeConversionUtil.toSponge(item), index);
    }

    @Override
    public void update(InventoryLayout layout) {
        if (layout instanceof SpongeInventoryLayout) {
            this.view.update(((SpongeInventoryLayout) layout).getLayout());
        }
    }
}
