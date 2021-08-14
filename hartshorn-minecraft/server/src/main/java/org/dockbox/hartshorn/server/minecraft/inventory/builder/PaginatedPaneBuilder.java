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

import org.dockbox.hartshorn.server.minecraft.inventory.context.ClickContext;
import org.dockbox.hartshorn.server.minecraft.inventory.Element;
import org.dockbox.hartshorn.server.minecraft.inventory.pane.PaginatedPane;

import java.util.Collection;
import java.util.function.Function;

public abstract class PaginatedPaneBuilder extends DefaultPaneBuilder<PaginatedPane, PaginatedPaneBuilder> {

    public abstract PaginatedPaneBuilder elements(Collection<Element> elements);


    @Override
    public PaginatedPaneBuilder onClickOutput(final Function<ClickContext, Boolean> onClick) {
        // Pagination has no output types
        return this;
    }

    @Override
    protected PaginatedPaneBuilder self() {
        return this;
    }
}
