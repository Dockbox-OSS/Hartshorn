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

import org.dockbox.selene.core.annotations.Placeholder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

@Placeholder(description = "Advanced relational table type, see https://github.com/GuusLieben/Selene/issues/124",
             by = "GuusLieben", assignee = "Coulis")
@SuppressWarnings("unchecked")
public class Table {

    private final Collection<Row> rows;
    private final ColumnIdentifier<?>[] identifiers;

    public Table(ColumnIdentifier<?>... columns) {
        this.identifiers = columns;
        this.rows = new CopyOnWriteArrayList<>();
    }

    public void addRow(Row row) {
        // Check if the row has the same amount of column as this table
        if (row.keySet().size() != this.identifiers.length)
            throw new IllegalArgumentException("Illegal magic!");

        // Check if the row has the same columns with the same order
        int index = 0;
        for (ColumnIdentifier<?> rowColumn : row.keySet()) {
            ColumnIdentifier<?> tableColumn = this.identifiers[index];
            if (rowColumn != tableColumn)
                throw new IllegalArgumentException("Column '" + rowColumn + "' did not match expected type '" + tableColumn + "'");
            index++;
        }

        this.rows.add(row);
    }

    public <T> Table addRow(Object object) {
        Row row = new Row();

        for (Field field : object.getClass().getFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    ColumnIdentifier<T> identifier = this.getIdentifier(field.getName());
                    if (null != identifier) {
                        row.addValue(identifier, (T) field.get(object));
                    }
                } catch (IllegalAccessError | ClassCastException | IllegalAccessException ignored) {
                }
            }
        }

        if (row.size() != this.identifiers.length) {
            throw new IllegalArgumentException("Missing Columns!");
        }
        this.rows.add(row);
        return this;
    }

    public Table addRow(Object... values) {
        if (values.length != this.identifiers.length) throw new IllegalArgumentException("Not allowed");

        Row row = new Row();
        for (int i = 0; i < this.identifiers.length; i++) {
            try {
                ColumnIdentifier identifier = this.identifiers[i];
                row.addValue(identifier, values[i]);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException(String.format("The value: %s, is not of the correct type.", values[i]), e);
            }
        }
        this.rows.add(row);
        return this;
    }

    public <T> Table lookup(ColumnIdentifier<? extends ColumnIdentifier<?>> column, T filter) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException("Cannot lookup a column which does not exist");

        Collection<Row> filteredRows = new ArrayList<>();
        for (Row row : this.rows) {
            Object value = row.getValue(column);
            if (value == filter || value.equals(filter)) {
                filteredRows.add(row);
            }
        }
        Table lookupTable = new Table(this.identifiers);
        filteredRows.forEach(lookupTable::addRow);
        return lookupTable;
    }

    public <T> Collection<Row> merge(Table otherTable, ColumnIdentifier<?> column) {
        if (this.hasColumn(column) && otherTable.hasColumn(column)) {
            Collection<Row> mergedRows = new ArrayList<>();
            this.rows.forEach(row -> {
                Object value = row.getValue(column);
                Row mergedRow = new Row();

                for (ColumnIdentifier<?> columnIdentifier : row.keySet()) {
                    ColumnIdentifier<T> identifier = (ColumnIdentifier<T>) columnIdentifier;
                    T val = row.getValue(identifier);
                    mergedRow.addValue(identifier, val);
                }

                otherTable.rows.stream().filter(other_row -> {
                    Object otherValue = other_row.getValue(column);
                    return otherValue == value || otherValue.equals(value);
                }).forEach(matchingRow -> {
                    // Yes this will override any other columns matching the same identifier, we don't have to care
                    for (ColumnIdentifier<?> columnIdentifier : matchingRow.keySet()) {
                        ColumnIdentifier<T> identifier = (ColumnIdentifier<T>) columnIdentifier;
                        T val = row.getValue(identifier);
                        mergedRow.addValue(identifier, val);
                    }
                });

                mergedRows.add(mergedRow);
            });

            return mergedRows;
        }
        throw new IllegalArgumentException("Column '" + column + "' does not exist in both tables");
    }

    public Collection<Row> getRows() {
        return this.rows;
    }

    private boolean hasColumn(ColumnIdentifier<?> column) {
        for (ColumnIdentifier<?> identifier : this.identifiers) {
            if(identifier == column)
                return true;
        }
        return false;
    }

    @Nullable
    private <T> ColumnIdentifier<T> getIdentifier(@NonNls String fieldName) throws ClassCastException {
        for (ColumnIdentifier<?> columnIdentifier : this.identifiers) {
            if (columnIdentifier.getColumnName().equalsIgnoreCase(fieldName)) {
                return (ColumnIdentifier<T>) columnIdentifier;
            }
        }
        return null;
    }
}
