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

package org.dockbox.selene.server.minecraft.dimension;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.server.minecraft.dimension.position.Location;

public class SimpleWarp implements Warp {

    private final Exceptional<String> description;
    private final Exceptional<String> category;
    private final Location location;
    private final String name;

    public SimpleWarp(
            Exceptional<String> description,
            Exceptional<String> category,
            Location location,
            String name
    ) {
        this.description = description;
        this.category = category;
        this.location = location;
        this.name = name;
    }

    @Override
    public Exceptional<String> getDescription() {
        return this.description;
    }

    @Override
    public Exceptional<String> getCategory() {
        return this.category;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
