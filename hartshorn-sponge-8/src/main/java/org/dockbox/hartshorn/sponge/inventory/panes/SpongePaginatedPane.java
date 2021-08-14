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

import org.dockbox.hartshorn.server.minecraft.inventory.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;
import org.dockbox.hartshorn.server.minecraft.players.Player;

import java.util.Collection;
import java.util.function.Function;

// TODO: #369 Implement this
public class SpongePaginatedPane implements PaginatedPane {

    @Override
    public void open(final Player player, final int page) {

    }

    @Override
    public void elements(final Collection<Element> elements) {

    }

    @Override
    public void open(final Player player) {

    }

    @Override
    public void onClick(final int index, final Function<ClickContext, Boolean> onClick) {

    }

    @Override
    public void close(final Player player) {

    }
}
