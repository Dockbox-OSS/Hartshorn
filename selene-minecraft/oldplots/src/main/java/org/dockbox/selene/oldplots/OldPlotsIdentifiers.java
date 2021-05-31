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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.persistence.table.column.ColumnIdentifier;
import org.dockbox.selene.persistence.table.column.SimpleColumnIdentifier;

public final class OldPlotsIdentifiers {

    public static final ColumnIdentifier<String> UUID = new SimpleColumnIdentifier<>("owner", String.class);
    public static final ColumnIdentifier<String> WORLD = new SimpleColumnIdentifier<>("world", String.class);
    public static final ColumnIdentifier<Integer> PLOT_X = new SimpleColumnIdentifier<>("plotIdX", Integer.class);
    public static final ColumnIdentifier<Integer> PLOT_Z = new SimpleColumnIdentifier<>("plotIdZ", Integer.class);
    public static final ColumnIdentifier<Integer> PLOT_ID = new SimpleColumnIdentifier<>("plotIdInternal", Integer.class);

    private OldPlotsIdentifiers() {}
}
