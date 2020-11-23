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
import org.dockbox.selene.integrated.data.table.behavior.Merge;
import org.dockbox.selene.integrated.data.table.behavior.Order;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.dockbox.selene.integrated.data.table.exceptions.EmptyEntryException;
import org.dockbox.selene.integrated.data.table.exceptions.IdentifierMismatchException;
import org.dockbox.selene.integrated.data.table.exceptions.UnknownIdentifierException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 A relational table type which can easily create weak relations to other tables. Relations are non-strict references so
 either table can be disposed of without affecting the origin.

 Each table has a final set of column identifiers indicating their structure. Tables contain {@link TableRow}s which can
 only be added if the row has the same identifiers as the table. If a row has more, less, or mismatching column
 idenfitiers it cannot be added to the table.

 Column identifiers are unique and should be implementations of {@link ColumnIdentifier} with a generic type indicating
 the data type.

 @since feature/S124
 @author Simbolduc, GuusLieben
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class Table {

    private final List<TableRow> rows;
    private final ColumnIdentifier<?>[] identifiers;

    /**
     Instantiates a new Table with a given set of column identifiers. These identifiers cannot be modified later unless
     a select action is performed, creating a split copy of this table instance.

     @param columns
     The column identifiers
     */
    public Table(ColumnIdentifier<?>... columns) {
        this.identifiers = columns;
        this.rows = new CopyOnWriteArrayList<>();
    }

    /**
     Adds a row to the table if the column identifiers are equal in length and type. If the row has more or less column
     identifiers it is not accepted into the table. If a row has a column which is not contained in this table it is not
     accepted into the table.

     @param row
     The row object to add to the table

     @throws IdentifierMismatchException
     When there is a column mismatch between the row and the table
     */
    public void addRow(TableRow row) throws IdentifierMismatchException {
        // Check if the row has the same amount of column as this table
        if (row.getColumns().size() != this.identifiers.length)
            throw new IdentifierMismatchException("The row does not have the same amount of columns as the table");

        // Check if the row has the same columns with the same order
        int index = 0;
        for (ColumnIdentifier<?> column : row.getColumns()) {
            if (!this.hasColumn(column)) {
                throw new IdentifierMismatchException("Column '" + column.getColumnName() + "' is not contained in table");
            }
        }

        this.rows.add(row);
    }

    /**
     Generates a {@link TableRow} from a given object based on the objects {@link Field}s. By default the field name is
     used to look up a matching column identifier which is present inside the table. If the field is annotated with
     {@link Identifier} the contained {@link ColumnIdentifier} is used instead.

     If the field is annotated with {@link Ignore} the field will not be converted to a column entry in the row.
     One attempt will be made to make the field accessible if it is not already.

     @param object
     Object to "try to" add as a row to the table
     @throws IllegalArgumentException
     When a field cannot be accessed or cast correctly. Contains the causing {@link Exception} as the cause.
     @throws UnknownIdentifierException
     When no column identifier could be found or generated, or when there are not enough fields present to satiate the
     column identifiers present in this table.
     */
    public void addRow(Object object) {
        TableRow row = new TableRow();

        for (Field field : object.getClass().getFields()) {
            if (!field.isAccessible()) field.setAccessible(true);
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
                        throw new UnknownIdentifierException("Unknown column identifier for field named : " + field.getName());

                    row.addValue(columnIdentifier, field.get(object));
                } catch (IllegalAccessError | ClassCastException | IllegalAccessException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if (row.getColumns().size() != this.identifiers.length) {
            throw new UnknownIdentifierException("Missing Columns!");
        }
        this.rows.add(row);
    }

    /**
     Generates a {@link TableRow} from a given set of objects. The objects should be in the same order as the table's
     {@link Table#getIdentifiers()}. If the data type of a object does not match up with its expected
     {@link ColumnIdentifier} a exception is thrown and the row is not inserted into the table.

     @param values
     Objects to "try to" add as rows to the table
     @throws IllegalArgumentException
     When the amount of values does not meet the amount of column headers, or when the data type of the object does not
     match the expected type.

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
     Filters the table's rows based on their value for a given {@link ColumnIdentifier}. If the value of the row at the
     given column matches the expected filter value the row is kept, otherwise it is ignored.

     Returns a new table with only the filtered rows. The origin table is not modified. Either table can be disposed of
     without affecting the other. Row references are shared between both tables.

     @param <T>
     Indicates the class that both the Identifier and the Filter must have
     @param column
     Indicates which column is used to search through the table
     @param filter
     Indicates what value to search for

     @return Returns the new table with the filter applied

     @throws IllegalArgumentException
     When a row causes a {@link IdentifierMismatchException}. Typically this is never thrown unless changes were made
     from another thread.
     */
    public <T> Table where(ColumnIdentifier<T> column, T filter) {
        if (!this.hasColumn(column))
            throw new UnknownIdentifierException("Cannot look up a column that does not exist");

        Collection<TableRow> filteredRows = new ArrayList<>();
        for (TableRow row : this.rows) {
            Exceptional<T> value = row.getValue(column);
            if (!value.isPresent()) continue;
            if (value.get() == filter || value.get().equals(filter)) {
                filteredRows.add(row);
            }
        }
        Table lookupTable = new Table(this.identifiers);
        for (TableRow filteredRow : filteredRows) {
            try {
                lookupTable.addRow(filteredRow);
            } catch (IdentifierMismatchException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return lookupTable;
    }

    /**
     Overloaded method for {@link #join(Table, ColumnIdentifier, Merge, boolean)}. Empty entries will not be populated
     using this method.

     @param <T>
     The data type of the column
     @param otherTable
     The other/foreign table
     @param column
     The column to join on
     @param merge
     The merge behavior

     @return A new table with the joined rows

     @throws EmptyEntryException
     Thrown if a entry is empty and cannot be populated
     @throws IdentifierMismatchException
     When a identifier does not exist across both tables
     */
    public <T> Table join(@NotNull Table otherTable, ColumnIdentifier<T> column, Merge merge) throws EmptyEntryException, IdentifierMismatchException {
        return this.join(otherTable, column, merge, false);
    }

    /**
     Joins two tables based on a given {@link ColumnIdentifier}. Rows are joined together based on their value on the
     given column, if the values match the rows are joined together.

     Rows from the origin and the foreign table are merged into single rows. If a column exists in both tables, the
     {@link Merge} behavior indicates which to keep.

     If a row does not have a matching row in the other table while new columns are created, {@code populateEmptyEntries}
     indicates whether to treat this is a illegal state, or populate the entry with null.

     @param <T>
     The data type of the column
     @param otherTable
     The other/foreign table
     @param column
     The column to join on
     @param merge
     The merge behavior
     @param populateEmptyEntries
     Whether or not empty entries should be populated (with null)

     @return A new table with the joined rows

     @throws EmptyEntryException
     Thrown if a entry is empty and cannot be populated
     @throws IdentifierMismatchException
     When a identifier does not exist across both tables
     */
    public <T> Table join(@NotNull Table otherTable, ColumnIdentifier<T> column, Merge merge, boolean populateEmptyEntries) throws EmptyEntryException, IdentifierMismatchException {
        if (this.hasColumn(column) && otherTable.hasColumn(column)) {
            List<ColumnIdentifier<?>> mergedIdentifiers = new ArrayList<>();
            for (ColumnIdentifier<?> identifier : SeleneUtils.addAll(this.getIdentifiers(), otherTable.getIdentifiers())) {
                if (mergedIdentifiers.contains(identifier)) continue;
                mergedIdentifiers.add(identifier);
            }

            Table joinedTable = new Table(mergedIdentifiers.toArray(new ColumnIdentifier<?>[0]));
            for (TableRow row : this.getRows()) {
                try {
                    List<TableRow> matchingRows = this.getMatchingRows(row, otherTable, column);

                    TableRow joinedRow = new TableRow();
                    for (ColumnIdentifier<?> identifier : this.getIdentifiers()) {
                        joinedRow.addValue(identifier, row.getValue(identifier).get());
                    }
                    for (ColumnIdentifier<?> identifier : otherTable.getIdentifiers()) {
                        for (TableRow matchingRow : matchingRows) {
                            /*
                             If there is already a value present on this row, look up if we want to keep the existing,
                             or use the new value.
                             */
                            if (!joinedRow.getValue(identifier).isPresent() || Merge.PREFER_FOREIGN == merge) {
                                joinedRow.addValue(identifier, matchingRow.getValue(identifier).get());

                            }
                        }

                        /*
                         If there was no value filled by either this table instance, or the foreign table, try to
                          populate it with null. If that is not allowed throw a exception.
                        */
                        if (!joinedRow.getValue(identifier).isPresent()) {
                            if (populateEmptyEntries) {
                                joinedRow.addValue(identifier, null);
                            } else {
                                throw new EmptyEntryException("Could not populate empty entry for column " + identifier.getColumnName());
                            }
                        }
                    }

                    joinedTable.addRow(joinedRow);
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }

            /*
             It is possible not all foreign rows had a matching value, if that is the case we will add them here if
             possible (if the foreign table has no additional identifiers which we cannot populate here.
             */
            for (TableRow row : otherTable.getRows()) {
                try {
                    List<TableRow> matchingRows = this.getMatchingRows(row, joinedTable, column);
                    if (matchingRows.isEmpty()) {
                        this.tryPopulateMissingEntry(otherTable, populateEmptyEntries, mergedIdentifiers, joinedTable, row);
                    }
                } catch (IdentifierMismatchException e) {
                    continue;
                }
            }

            return joinedTable;
        }
        throw new IdentifierMismatchException("Column '" + column + "' does not exist in both tables");

    }

    private void tryPopulateMissingEntry(@NotNull Table otherTable, boolean populateEmptyEntries, List<
            ColumnIdentifier<?>> mergedIdentifiers, Table joinedTable, TableRow row) throws IdentifierMismatchException {
        if (!Arrays.equals(this.getIdentifiers(), otherTable.getIdentifiers())) {
            if (populateEmptyEntries) {
                for (ColumnIdentifier<?> identifier : mergedIdentifiers) {
                    Exceptional<?> exceptionalValue = row.getValue(identifier);
                    exceptionalValue
                            .ifPresent(value -> row.addValue(identifier, value))
                            .ifAbsent(() -> row.addValue(identifier, null));
                }
            } else {
                throw new IdentifierMismatchException("Remaining rows were found in the foreign table, but identifiers are not equal. Cannot insert null values!");
            }
        }
        joinedTable.addRow(row);
    }

    private <T> List<TableRow> getMatchingRows(TableRow row, Table otherTable, ColumnIdentifier<T> column) {
        Exceptional<?> exceptionalValue = row.getValue(column);
        // No way to join on value if it is not present. Technically this should not be possible as a NPE
        // is typically thrown if a null value is added to a row.
        if (exceptionalValue.isAbsent())
            throw new IndexOutOfBoundsException("No value present for " + column.getColumnName());
        T expectedValue = (T) exceptionalValue.get();

        return otherTable.where(column, expectedValue).getRows();
    }

    /**
     Selects only the given columns of a table. Returns a new table populated with all the rows in the origin table, but
     only with the columns provided.

     @param columns
     Indicates the columns to select

     @return Return the new table with only the selected columns
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
            try {
                table.addRow(tmpRow);
            } catch (IdentifierMismatchException e) {
                throw new IllegalArgumentException(e);
            }
        });

        return table;
    }

    /**
     Gets the table's column identifiers.

     @return Return the table's identifiers
     */
    public ColumnIdentifier<?>[] getIdentifiers() {
        return this.identifiers;
    }

    /**
     Gets all rows in the table.

     @return Return the table's rows
     */
    public List<TableRow> getRows() {
        return SeleneUtils.asUnmodifiableList(this.rows);
    }

    /**
     Gets the amount of rows present in the table.

     @return Return the table's row count
     */
    public int count() {
        return this.rows.size();
    }

    /**
     Attempts to get the first row in the table. If there is no value present, a {@link Exceptional} holding a
     {@link IndexOutOfBoundsException} will be returned.

     @return Return the first row of the table
     */
    public Exceptional<TableRow> first() {
        return Exceptional.of(() -> this.rows.get(0));
    }

    /**
     Attempts to get the last row in the table. If there is no value present, a {@link Exceptional} holding a
     {@link IndexOutOfBoundsException} will be returned.

     @return Return the last row of the table
     */
    public Exceptional<TableRow> last() {
        return Exceptional.of(() -> this.rows.get(this.count() - 1));
    }

    /**
     Orders (sorts) a table based on a given column. Requires the data type of the {@link ColumnIdentifier} to be a
     implementation of {@link Comparable}. This modifies the existing table.

     @param <T>
     The type constraint indicating this method only accepts implementations of {@link Comparable}
     @param column
     Indicates the column to order by
     @param order
     Indicates what way to order the table by

     @throws IllegalArgumentException
     When the table does not contain the given column, or the data type is not a {@link Comparable}
     */
    public <T extends Comparable> void orderBy(ColumnIdentifier<T> column, Order order) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException("Table does not contains column named : " + column.getColumnName());

        if (!Comparable.class.isAssignableFrom(column.getType()))
            throw new IllegalArgumentException("Column does not contain a comparable data type : " + column.getColumnName());

        this.rows.sort((r1, r2) -> {
            Comparable c1 = r1.getValue(column).get();
            Comparable c2 = r2.getValue(column).get();
            return Order.ASC == order ? c1.compareTo(c2) : c2.compareTo(c1);
        });
    }

    /**
     Returns whether or not the table contains the given {@link ColumnIdentifier}.

     @param column
     The column

     @return Whether or not the column is present
     */
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

    /**
     Returns whether or not the table contains the given {@link TableRow}

     @param row
     The row

     @return Whether or not the row is present
     */
    public boolean hasRow(TableRow row) {
        for (TableRow tableRow : this.getRows()) {
            if (tableRow == row) {
                return true;
            }
        }
        return false;
    }

}
