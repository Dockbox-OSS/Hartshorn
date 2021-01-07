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

package org.dockbox.selene.core.objects.location;

import org.dockbox.selene.core.objects.Exceptional;

/**
 * Represents a warp, a virtual location inside a world, with descriptive properties.
 */
public class Warp {

    public final Exceptional<String> description;
    public final Exceptional<String> category;
    public final Location location;
    public final String name;

    public Warp(Exceptional<String> description, Exceptional<String> category, Location location, String name) {
        this.description = description;
        this.category = category;
        this.location = location;
        this.name = name;
    }

    public Exceptional<String> getDescription() {
        return this.description;
    }

    public Exceptional<String> getCategory() {
        return this.category;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }
}
