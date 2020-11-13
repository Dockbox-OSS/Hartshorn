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

import org.dockbox.selene.integrated.data.table.annotations.Identifier;
import org.dockbox.selene.integrated.data.table.annotations.Ignore;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("unchecked")
public class Table {

    private final List<TableRow> rows;
    private ColumnIdentifier<?>[] identifiers;

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

    public Table addRow(Object object) {
        TableRow row = new TableRow();

        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    ColumnIdentifier columnIdentifier = null;

                    // Try to grab the column identifier from the Identifier annotation of the field (if present)
                    Identifier identifier = field.getAnnotation(Identifier.class);
                    if (null != identifier)
                        columnIdentifier = this.getIdentifier(identifier.columnIdentifier());

                    // If no Identifier annotation was present, try to grab it using the field name
                    if (null != columnIdentifier)
                        columnIdentifier = this.getIdentifier(field.getName());

                    // No column identifier was found
                    if (null == columnIdentifier)
                        throw new IllegalArgumentException("Unknown column identifier for field named : " + field.getName());

                    row.addValue(columnIdentifier, field.get(object));
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
            row.addValue(identifier, value);
        }
        this.rows.add(row);
        return this;
    }

    public <T> Table where(ColumnIdentifier<? extends ColumnIdentifier<?>> column, T filter) {
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

    public <T> Collection<TableRow> merge(Table otherTable, ColumnIdentifier<T> column) {
        if (this.hasColumn(column) && otherTable.hasColumn(column)) {
            Collection<TableRow> mergedRows = new ArrayList<>();
            this.rows.forEach(row -> {
                Object value = row.getValue(column);
                TableRow mergedRow = new TableRow();

                row.getColumns().forEach((ColumnIdentifier c) -> row.addValue(c, row.getValue(c)));

                otherTable.rows.stream().filter(other_row -> {
                    Object otherValue = other_row.getValue(column);
                    return otherValue == value || otherValue.equals(value);
                }).forEach(matchingRow -> matchingRow.getColumns()
                    // This will override any other columns matching the same identifier
                    .forEach((ColumnIdentifier c) -> mergedRow.addValue(c, matchingRow.getValue(c)))
                );

                mergedRows.add(mergedRow);
            });

            return mergedRows;
        }
        throw new IllegalArgumentException("Column '" + column + "' does not exist in both tables");
    }

    public Table select(ColumnIdentifier<?>... columns) {
        Table table = this;
        List<ColumnIdentifier<?>> tmp = Arrays.asList(this.identifiers);

        this.rows.forEach(row -> {
            for (ColumnIdentifier<?> columnIdentifier : columns) {
                if(!row.getColumns().contains(columnIdentifier)) {
                    row.getColumns().remove(columnIdentifier);
                }
            }
        });

        for (ColumnIdentifier<?> column : columns) {
            tmp.remove(column);
        }
        table.identifiers = (ColumnIdentifier<?>[]) tmp.toArray();

        return table;
    }

    public ColumnIdentifier<?>[] getIdentifiers() {
        return this.identifiers;
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
    private ColumnIdentifier getIdentifier(@NonNls String fieldName) throws ClassCastException {
        for (ColumnIdentifier columnIdentifier : this.identifiers) {
            if (columnIdentifier.getColumnName().equalsIgnoreCase(fieldName)) {
                return columnIdentifier;
            }
        }
        return null;
    }

    // TODO : Contains
    // TODO : Matches
    // TODO : Select
    // TODO : OrderBy
    // TODO : Find
    // TODO : ToList
    // TODO : ToArray
    // TODO : Count
    // TODO : First
    // TODO : Last
    // TODO, several merge methods (e.g. keepAll, keepColumns(A, B), [ preferOriginal, preferOther (when reaching column identifiers which are the same but may have different values) ]

}
