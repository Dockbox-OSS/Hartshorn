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

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.table.annotations.Identifier;
import org.dockbox.selene.integrated.data.table.annotations.Ignore;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Table {

    private final List<TableRow> rows;
    private final ColumnIdentifier<?>[] identifiers;

    public Table(ColumnIdentifier<?>... columns) {
        this.identifiers = columns;
        this.rows = new CopyOnWriteArrayList<>();
    }

    /**
     * @param row Row object to add to the table
     */
    public void addRow(TableRow row) {
        // Check if the row has the same amount of column as this table
        if (row.getColumns().size() != this.identifiers.length)
            throw new IllegalArgumentException("The row does not have the same amount of columns as the table");

        // Check if the row has the same columns with the same order
        int index = 0;
        for (ColumnIdentifier<?> column : row.getColumns()) {
            if (!this.hasColumn(column)) {
                throw new IllegalArgumentException("Column '" + column.getColumnName() + "' is not contained in table");
            }
        }

        this.rows.add(row);
    }

    /**
     * @param object Object to "try to" add as a row to the table
     */
    public void addRow(Object object) {
        TableRow row = new TableRow();

        for (Field field : object.getClass().getFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
                try {
                    ColumnIdentifier columnIdentifier = null;

                    // Try to grab the column identifier from the Identifier annotation of the field (if present)
                    Identifier identifier = field.getAnnotation(Identifier.class);
                    if (null != identifier)
                        columnIdentifier = this.getIdentifier(identifier.value());

                    // If no Identifier annotation was present, try to grab it using the field name
                    if (null == columnIdentifier)
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
    }

    /**
     * @param values Objects to "try to" add as rows to the table
     */
    public void addRow(Object... values) {
        if (values.length != this.identifiers.length)
            throw new IllegalArgumentException("Amount of given values does not meet amount of column headers");

        TableRow row = new TableRow();
        for (int i = 0; i < this.identifiers.length; i++) {
            ColumnIdentifier identifier = this.identifiers[i];
            Object value = values[i];
            row.addValue(identifier, value);
        }
        this.rows.add(row);
    }

    /**
     * @param column Indicates which column is used to search through the table
     * @param filter Indicates what value to search for
     * @param <T> Indicates the class that both the Identifier and the Filter must have
     * @return Returns the new table with the filter applied
     */
    public <T> Table where(ColumnIdentifier<T> column, T filter) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException("Cannot look up a column that does not exist");

        Collection<TableRow> filteredRows = new ArrayList<>();
        for (TableRow row : this.rows) {
            Exceptional<T> value = row.getValue(column);
            if (!value.isPresent()) continue;
            if (value.get() == filter || value.get().equals(filter)) {
                filteredRows.add(row);
            }
        }
        Table lookupTable = new Table(this.identifiers);
        filteredRows.forEach(lookupTable::addRow);
        return lookupTable;
    }

    /**
     * @param otherTable Indicates the second table to merge with
     * @param column Indicates the column of the second table to merge to the first table
     * @param <T> Indicates the data type of the column
     * @return Return the merged table
     */
    // TODO, several merge methods (e.g. keepAll, keepColumns(A, B), [ preferOriginal, preferOther (when reaching column identifiers which are the same but may have different values) ]
    public <T> Collection<TableRow> merge(@NotNull Table otherTable, ColumnIdentifier<T> column) {
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

    /**
     * @param columns Indicates the columns to select
     * @return Return the new table with only the selected columns
     */
    public Table select(ColumnIdentifier<?>... columns) {
        Table table = new Table(columns);

        this.rows.forEach(row -> {
            TableRow tmpRow = new TableRow();
            for (ColumnIdentifier<?> column : columns) {
                row.getColumns().stream().filter(column::equals).forEach(tCol -> {
                    tmpRow.addValue(column, row.getValue(column).get());
                });
            }
        });

        return table;
    }

    /**
     * @return Return the table's identifiers
     */
    public ColumnIdentifier<?>[] getIdentifiers() {
        return this.identifiers;
    }

    /**
     * @return Return the table's rows
     */
    public List<TableRow> getRows() {
        return SeleneUtils.asUnmodifiableList(this.rows);
    }

    /**
     * @return Return the table's row count
     */
    public int count() {
        return this.rows.size();
    }

    /**
     @return Return the first row of the table
     */
    public Exceptional<TableRow> first() {
        return Exceptional.of(() -> this.rows.get(0));
    }

    /**
     @return Return the last row of the table
     */
    public Exceptional<TableRow> last() {
        return Exceptional.of(() -> this.rows.get(this.count() - 1));
    }

    /**
     @param column
     Indicates the column to order by
     @param order
     Indicates what way to order the table by
     */
    public void orderBy(ColumnIdentifier<?> column, Orders order) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException("Table does not contains column named : " + column.getColumnName());

        if (!Comparable.class.isAssignableFrom(column.getType()))
            throw new IllegalArgumentException("Column does not contain a comparable data type : " + column.getColumnName());

        this.rows.sort((r1, r2) -> {
            Comparable c1 = (Comparable) r1.getValue(column).get();
            Comparable c2 = (Comparable) r2.getValue(column).get();
            return Orders.ASC == order ? c1.compareTo(c2) : c2.compareTo(c1);
        });
    }

    public boolean hasColumn(ColumnIdentifier<?> column) {
        for (ColumnIdentifier<?> identifier : this.identifiers) {
            if (identifier == column)
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

    public boolean hasRow(TableRow row) {
        for (TableRow tableRow : this.getRows()) {
            if (tableRow == row) {
                return true;
            }
        }
        return false;
    }

}
