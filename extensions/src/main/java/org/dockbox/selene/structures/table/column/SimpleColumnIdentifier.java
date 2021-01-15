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

package org.dockbox.selene.structures.table.column;

public class SimpleColumnIdentifier<T> implements ColumnIdentifier<T> {

    private final String fieldName;
    private final Class<T> type;

    public SimpleColumnIdentifier(String fieldName, Class<T> type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    @Override
    public String getColumnName() {
        return this.fieldName;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "SimpleColumnIdentifier{" +
                "fieldName='" + fieldName + '\'' +
                ", type=" + type +
                '}';
    }
}
