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

package org.dockbox.selene.api.objects.item.maps;

import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.images.MultiSizedImage;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface CustomMap extends Item {

    static CustomMap of(BufferedImage image, Identifiable source) {
        return Selene.provide(CustomMapService.class).create(image, source);
    }

    static CustomMap of(byte[] image, Identifiable source) {
        return Selene.provide(CustomMapService.class).create(image, source);
    }

    static Map<Integer[], CustomMap> of(MultiSizedImage image, Identifiable source) {
        return Selene.provide(CustomMapService.class).create(image, source);
    }

    Identifiable getOwner();

    int getMapId();
}
