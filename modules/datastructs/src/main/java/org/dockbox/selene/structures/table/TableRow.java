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

package org.dockbox.selene.structures.table;

import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.util.Reflect;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.structures.table.column.ColumnIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class TableRow {

    private final Map<ColumnIdentifier<?>, Object> data = SeleneUtils.emptyMap();

    public TableRow() {}

    /**
     * @param column
     *         Indicates which columns to assign the value to
     * @param value
     *         Indicates the valiue of the column
     *
     * @return The instance of this TableRow
     */
    @NotNull
    public TableRow addValue(@NotNull ColumnIdentifier<?> column, @Nullable Object value) {
        // Make sure both the Identifier and the Value are both the same type
        if (null == value || Reflect.isAssignableFrom(column.getType(), value.getClass()))
            this.data.put(column, value);
        else
            throw new IllegalArgumentException(
                    String.format(
                            "The value: %s, is not of the correct type. (Expected: %s, but got %s)",
                            value, column.getType().getSimpleName(), value.getClass().getSimpleName()));
        return this;
    }

    /**
     * @param column
     *         Indicates which columns to get the value from
     * @param <T>
     *         Indicates what class type of object is used and returned
     *
     * @return Return a Nullable value of the asked column
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public <T> Exceptional<T> getValue(@NotNull ColumnIdentifier<T> column) {
        if (null == this.data.get(column)) return Exceptional.none();

        return Exceptional.of((T) this.data.get(column));
    }

    /** @return Return a set of the values of the columns of the row */
    @NotNull
    public Set<Object> getValues() {
        return SeleneUtils.asUnmodifiableSet(this.data.values());
    }

    /** @return Return a set of keys of the row */
    @NotNull
    public Set<ColumnIdentifier<?>> getColumns() {
        return SeleneUtils.asUnmodifiableSet(this.data.keySet());
    }

    @Override
    public String toString() {
        return "TableRow{" + "data=" + this.data + '}';
    }
}
