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

package org.dockbox.hartshorn.persistence.table;

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

import lombok.Getter;

public class TableRow {

    @Getter private final Map<ColumnIdentifier<?>, Object> data = HartshornUtils.emptyMap();

    public TableRow() {}

    /**
     * @param column
     *         Indicates which columns to assign the value to
     * @param value
     *         Indicates the value of the column
     *
     * @return The instance of this TableRow
     */
    @NotNull
    public TableRow add(@NotNull final ColumnIdentifier<?> column, @Nullable final Object value) {
        // Make sure both the Identifier and the Value are both the same type
        if (null == value || TypeContext.of(value).childOf(column.type()))
            this.data.put(column, value);
        else
            throw new IllegalArgumentException(
                    String.format(
                            "The value: %s, is not of the correct type. (Expected: %s, but got %s)",
                            value, column.type().name(), value.getClass().getSimpleName()));
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
    @NotNull
    public <T> Exceptional<T> value(@NotNull final ColumnIdentifier<T> column) {
        if (null == this.data.get(column)) return Exceptional.empty();

        return Exceptional.of((T) this.data.get(column));
    }

    /** @return Return a set of the values of the columns of the row */
    @NotNull
    public Set<Object> values() {
        return HartshornUtils.asUnmodifiableSet(this.data.values());
    }

    /** @return Return a set of keys of the row */
    @NotNull
    public Set<ColumnIdentifier<?>> columns() {
        return HartshornUtils.asUnmodifiableSet(this.data.keySet());
    }

    @Override
    public String toString() {
        return "TableRow{" + "data=" + this.data + '}';
    }
}
