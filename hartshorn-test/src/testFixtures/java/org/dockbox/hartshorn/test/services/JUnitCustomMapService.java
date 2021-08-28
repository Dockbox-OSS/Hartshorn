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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.commands.SystemSubject;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.server.minecraft.item.DefaultCustomMapService;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMap;
import org.dockbox.hartshorn.test.objects.JUnitCustomMap;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

public class JUnitCustomMapService extends DefaultCustomMapService {

    private static final List<CustomMap> maps = HartshornUtils.emptyConcurrentList();

    @Inject
    private ApplicationContext context;

    @Override
    public CustomMap create(final BufferedImage image, final Identifiable source) {
        final CustomMap map = this.context.get(JUnitCustomMap.class, source, maps.size());
        maps.add(map);
        return map;
    }

    @Override
    public CustomMap create(final byte[] image, final Identifiable source) {
        final CustomMap map = this.context.get(JUnitCustomMap.class, source, maps.size());
        maps.add(map);
        return map;
    }

    @Override
    public CustomMap from(final int id) {
        if (maps.size() > id) {
            return maps.get(id);
        }
        return this.context.get(JUnitCustomMap.class, SystemSubject.instance(this.context), id);
    }

    @Override
    public Collection<CustomMap> all(final Identifiable source) {
        return maps.stream()
                .filter(map -> map.owner().equals(source))
                .toList();
    }

    @Override
    public Exceptional<CustomMap> from(final Item item) {
        return Exceptional.empty();
    }
}
