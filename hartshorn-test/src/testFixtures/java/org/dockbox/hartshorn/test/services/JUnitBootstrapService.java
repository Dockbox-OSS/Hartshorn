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

package org.dockbox.hartshorn.test.services;

import org.dockbox.hartshorn.api.annotations.PostBootstrap;
import org.dockbox.hartshorn.api.annotations.UseBootstrap;
import org.dockbox.hartshorn.di.annotations.service.Service;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.item.ItemContext;
import org.dockbox.hartshorn.server.minecraft.item.ItemTypes;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;

@Service(activators = UseBootstrap.class)
public class JUnitBootstrapService {

    @PostBootstrap
    public void bootstrap(final ApplicationContext context) {
        final List<String> items = HartshornUtils.emptyList();
        for (final ItemTypes value : ItemTypes.values()) items.add(value.id());
        final ItemContext itemContext = new ItemContext(items, HartshornUtils.asList(ItemTypes.STONE.id()));
        context.add(itemContext);
    }

}
