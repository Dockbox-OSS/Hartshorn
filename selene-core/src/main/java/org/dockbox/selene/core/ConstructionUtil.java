/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.core;

import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.text.pagination.PaginationBuilder;

public interface ConstructionUtil {
    PaginationBuilder paginationBuilder();

    /**
     Creates a item based on a given fully qualified identifier. Uses unsafe damage (meta) to select blocks from a
     internal platform registry.

     <b>Note that the use of unsafe damage (meta) is deprecated, and should be avoided. As of 1.13 this will no longer
     be available!</b>
     @param id The fully qualified identifier of a block, e.g. {@code minecraft:stone}
     @param meta The unsafe damage, or meta. Constraints to range 0-15
     @return The item instance, or {@link Item#AIR}
     */
    @Deprecated
    Item<?> item(String id, int meta);
    Item<?> item(String id);
}
