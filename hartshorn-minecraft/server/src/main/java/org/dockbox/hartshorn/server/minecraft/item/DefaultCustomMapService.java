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

package org.dockbox.hartshorn.server.minecraft.item;

import org.dockbox.hartshorn.api.domain.Identifiable;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMap;
import org.dockbox.hartshorn.server.minecraft.item.maps.CustomMapService;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.dockbox.hartshorn.util.images.ImageUtil;
import org.dockbox.hartshorn.util.images.MultiSizedImage;

import java.awt.image.BufferedImage;
import java.util.Map;

public abstract class DefaultCustomMapService implements CustomMapService {

    @Override
    public Map<Integer[], CustomMap> create(BufferedImage image, int width, int height, Identifiable source) {
        return this.create(ImageUtil.split(image, width, height), source);
    }

    @Override
    public Map<Integer[], CustomMap> create(MultiSizedImage image, Identifiable source) {
        Map<Integer[], CustomMap> chunks = HartshornUtils.emptyMap();
        image.imageMap().forEach((index, chunk) -> chunks.put(index, this.create(chunk, source)));
        return chunks;
    }
}
