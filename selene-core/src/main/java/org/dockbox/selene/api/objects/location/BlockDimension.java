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

package org.dockbox.selene.api.objects.location;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.profile.Profile;
import org.dockbox.selene.api.objects.tuple.Vector3N;

public interface BlockDimension {

    Vector3N minimumPosition();

    Vector3N maximumPosition();

    Vector3N floor(Vector3N position);

    boolean hasBlock(Vector3N position);

    Exceptional<Item> getBlock(Vector3N position);

    boolean setBlock(Vector3N position, Item item, BlockFace direction, Profile placer);

}
