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

package org.dockbox.selene.integrated.data.table;

import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;

import java.util.HashMap;
import java.util.Set;

public class TableRow {

    private final HashMap<ColumnIdentifier<?>, Object> data = new HashMap<>();

    public TableRow() { }

    public <T> TableRow addValue(ColumnIdentifier<T> column, T value) {
        if (column.getType().isAssignableFrom(value.getClass()))
            this.data.put(column, value);
        else
            throw new IllegalArgumentException(
                String.format("The value: %s, is not of the correct type. (Expected: %s, but got %s)",
                    value,
                    column.getType().getSimpleName(),
                    value.getClass().getSimpleName()
                ));

        return this;
    }

    public <T> T getValue(ColumnIdentifier<T> column) {
        return (T) this.data.get(column);
    }

    public Set<Object> getValues() {
        return SeleneUtils.asUnmodifiableSet(this.data.values());
    }

    public Set<ColumnIdentifier<?>> getColumns() {
        return this.data.keySet();
    }
}
