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

package org.dockbox.selene.domain.table.column;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimpleColumnIdentifier<T> implements ColumnIdentifier<T> {

    private final String columnName;
    private final Class<T> type;

    @Override
    public int hashCode() {
        return Objects.hash(this.columnName, this.getType());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleColumnIdentifier)) return false;
        SimpleColumnIdentifier<?> that = (SimpleColumnIdentifier<?>) o;
        return Objects.equals(this.columnName, that.columnName)
                && Objects.equals(this.getType(), that.getType());
    }

    @Override
    public String toString() {
        return "SimpleColumnIdentifier{" + "fieldName='" + this.columnName + '\'' + ", type=" + this.type + '}';
    }
}
