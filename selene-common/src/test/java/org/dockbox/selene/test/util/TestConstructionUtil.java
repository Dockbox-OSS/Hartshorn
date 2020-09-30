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

package org.dockbox.selene.test.util;

import org.dockbox.selene.core.text.navigation.PaginationBuilder;
import org.dockbox.selene.core.util.construct.ConstructionUtil;
import org.jetbrains.annotations.NotNull;

public class TestConstructionUtil implements ConstructionUtil {
    @NotNull
    @Override
    public PaginationBuilder paginationBuilder() {
        throw new UnsupportedOperationException("Pagination is not testable in common implementations, and should only be tested in the platform implementation");
    }
}
