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

package org.dockbox.hartshorn.sponge.inventory.pane;

import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;

import java.util.Collection;
import java.util.stream.Collectors;

import dev.flashlabs.flashlibs.inventory.Page;

public class SpongePaginatedPane implements PaginatedPane {

    private final Page page;

    public SpongePaginatedPane(Page initializedPage) {
        this.page = initializedPage;
    }

    @Override
    public void open(Player player, int page) {
        SpongeConversionUtil.toSponge(player).present(p -> this.page.open(p, page));
    }

    @Override
    public void elements(Collection<Element> elements) {
        this.page.define(
                elements.stream().map(SpongeConversionUtil::toSponge).collect(Collectors.toList()));
    }

    @Override
    public void open(Player player) {
        SpongeConversionUtil.toSponge(player).present(this.page::open);
    }
}
