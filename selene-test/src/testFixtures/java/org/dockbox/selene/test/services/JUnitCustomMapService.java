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

package org.dockbox.selene.test.services;

import org.dockbox.selene.api.objects.Console;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.minecraft.item.maps.CustomMap;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.server.minecraft.item.DefaultCustomMapService;
import org.dockbox.selene.test.objects.JUnitCustomMap;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JUnitCustomMapService extends DefaultCustomMapService {

    private static final List<CustomMap> maps = SeleneUtils.emptyConcurrentList();

    @Override
    public CustomMap create(BufferedImage image, Identifiable source) {
        CustomMap map = new JUnitCustomMap(source, maps.size());
        maps.add(map);
        return map;
    }

    @Override
    public CustomMap create(byte[] image, Identifiable source) {
        CustomMap map = new JUnitCustomMap(source, maps.size());
        maps.add(map);
        return map;
    }

    @Override
    public CustomMap getById(int id) {
        if (maps.size() > id) {
            return maps.get(id);
        }
        return new JUnitCustomMap(Console.getInstance(), id);
    }

    @Override
    public Collection<CustomMap> getFrom(Identifiable source) {
        return maps.stream()
                .filter(map -> map.getOwner().equals(source))
                .collect(Collectors.toList());
    }

    @Override
    public Exceptional<CustomMap> derive(Item item) {
        return Exceptional.of(this.getById(item.getMeta()));
    }
}
