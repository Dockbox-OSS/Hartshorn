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

package org.dockbox.hartshorn.i18n.text.pagination;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.jetbrains.annotations.NotNull;

@Binds(PaginationBuilder.class)
public class PaginationBuilderImpl extends PaginationBuilder {

    @NotNull
    @Override
    public Pagination build() {
        return new PaginationImpl(
                super.padding(),
                super.linesPerPage(),
                super.header(),
                super.footer(),
                super.title(),
                super.content());
    }
}
