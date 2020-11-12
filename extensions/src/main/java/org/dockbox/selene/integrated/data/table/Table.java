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

import org.dockbox.selene.integrated.data.table.annotations.Ignore;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unchecked")
public class Table {

    private final List<TableRow> rows;
    private final ColumnIdentifier<?>[] identifiers;

    public Table(ColumnIdentifier<?>... columns) {
        this.identifiers = columns;
        this.rows = new CopyOnWriteArrayList<>();
    }

    public void addRow(TableRow row) {
        // Check if the row has the same amount of column as this table
        if (row.getColumns().size() != this.identifiers.length)
            throw new IllegalArgumentException("Illegal magic!");

        // Check if the row has the same columns with the same order
        int index = 0;
        for (ColumnIdentifier<?> rowColumn : row.getColumns()) {
            ColumnIdentifier<?> tableColumn = this.identifiers[index];
            if (rowColumn != tableColumn)
                throw new IllegalArgumentException("Column '" + rowColumn + "' did not match expected type '" + tableColumn + "'");
            index++;
        }

        this.rows.add(row);
    }

    public <T> Table addRow(Object object) {
        TableRow row = new TableRow();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    // TODO, add a @ColumnIdentifier annotation holding a constant ColumnIdentifier. If that is present
                    //  on a type, use that ColumnIdentifier here, otherwise fall back to the current behavior.
                    ColumnIdentifier<T> identifier = this.getIdentifier(field.getName());
                    if (null != identifier) {
                        row.addValue(identifier, (T) field.get(object));
                    }
                } catch (IllegalAccessError | ClassCastException | IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if (row.getColumns().size() != this.identifiers.length) {
            throw new IllegalArgumentException("Missing Columns!");
        }
        this.rows.add(row);
        return this;
    }

    public Table addRow(Object... values) {
        if (values.length != this.identifiers.length)
            throw new IllegalArgumentException("Amount of given values does not meet amount of column headers");

        TableRow row = new TableRow();
        for (int i = 0; i < this.identifiers.length; i++) {
            ColumnIdentifier identifier = this.identifiers[i];
            Object value = values[i];
            if (identifier.getType().isAssignableFrom(value.getClass()))
                row.addValue(identifier, values[i]);
            else // TODO, can we move this to TableRow? So we can ensure other classes can't insert mismatched types either
                throw new IllegalArgumentException(
                        String.format("The value: %s, is not of the correct type. (Expected: %s, but got %s)",
                                values[i],
                                identifier.getType().getSimpleName(),
                                value.getClass().getSimpleName()
                        ));
        }
        this.rows.add(row);
        return this;
    }

    // TODO, several lookup methods (e.g. match, contains, etc)
    public <T> Table lookup(ColumnIdentifier<? extends ColumnIdentifier<?>> column, T filter) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException("Cannot lookup a column which does not exist");

        Collection<TableRow> filteredRows = new ArrayList<>();
        for (TableRow row : this.rows) {
            Object value = row.getValue(column);
            if (value == filter || value.equals(filter)) {
                filteredRows.add(row);
            }
        }
        Table lookupTable = new Table(this.identifiers);
        filteredRows.forEach(lookupTable::addRow);
        return lookupTable;
    }

    // TODO, several merge methods (e.g. keepAll, keepColumns(A, B), [ preferOriginal, preferOther (when reaching column identifiers which are the same but may have different values) ]
    public <T> Collection<TableRow> merge(Table otherTable, ColumnIdentifier<T> column) {
        if (this.hasColumn(column) && otherTable.hasColumn(column)) {
            Collection<TableRow> mergedRows = new ArrayList<>();
            this.rows.forEach(row -> {
                Object value = row.getValue(column);
                TableRow mergedRow = new TableRow();

                for (ColumnIdentifier<?> columnIdentifier : row.getColumns()) {
                    ColumnIdentifier<T> identifier = (ColumnIdentifier<T>) columnIdentifier;
                    T val = row.getValue(identifier);
                    mergedRow.addValue(identifier, val);
                }

                otherTable.rows.stream().filter(other_row -> {
                    Object otherValue = other_row.getValue(column);
                    return otherValue == value || otherValue.equals(value);
                }).forEach(matchingRow -> {
                    // This will override any other columns matching the same identifier
                    for (ColumnIdentifier<?> columnIdentifier : matchingRow.getColumns()) {
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

    public List<TableRow> getRows() {
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
