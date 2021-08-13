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

import org.dockbox.hartshorn.i18n.text.Text;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.builder.PaginatedPaneBuilder;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;

import java.util.Collection;

// TODO: #369 Implement this
public class SpongePaginatedPaneBuilder extends PaginatedPaneBuilder {
    @Override
    public PaginatedPaneBuilder elements(Collection<Element> elements) {
        return null;
    }

    @Override
    public PaginatedPaneBuilder title(Text text) {
        return null;
    }

    @Override
    public PaginatedPane build() {
        return null;
    }
}
