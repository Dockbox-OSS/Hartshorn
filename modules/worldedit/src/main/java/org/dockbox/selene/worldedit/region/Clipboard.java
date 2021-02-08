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

package org.dockbox.selene.worldedit.region;

import org.dockbox.selene.api.objects.tuple.Vector3N;

public class Clipboard
{

    private final Region region;
    private final Vector3N origin;

    public Clipboard(Region region, Vector3N origin)
    {
        this.region = region;
        this.origin = origin;
    }

    public Region getRegion()
    {
        return this.region;
    }

    public Vector3N getOrigin()
    {
        return this.origin;
    }
}
