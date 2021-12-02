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

package org.dockbox.hartshorn.data.table;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.element.ConstructorContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.exceptions.EmptyEntryException;
import org.dockbox.hartshorn.data.exceptions.IdentifierMismatchException;
import org.dockbox.hartshorn.data.exceptions.UnknownIdentifierException;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * A relational table type which can easily create weak relations to other tables. Relations are
 * non-strict references so either table can be disposed of without affecting the origin.
 *
 * <p>Each table has a final set of column identifiers indicating their structure. Tables contain
 * {@link TableRow}s which can only be added if the row has the same identifiers as the table. If a
 * row has more, less, or mismatching column identifiers it cannot be added to the table.
 *
 * <p>Column identifiers are unique and should be implementations of {@link ColumnIdentifier} with a
 * generic type indicating the data type.
 *
 * @author Simbolduc, GuusLieben
 * @since feature/S124
 */
public class Table {

    private static final Map<TypeContext<?>, List<ColumnIdentifier<?>>> DEFINITIONS = HartshornUtils.emptyMap();
    private final List<TableRow> rows;
    @Getter private final ColumnIdentifier<?>[] identifiers;

    /**
     * Instantiates a new Table with a given set of column identifiers. These identifiers cannot be
     * modified later unless a select action is performed, creating a split copy of this table
     * instance.
     *
     * @param columns
     *         The column identifiers
     */
    public Table(final ColumnIdentifier<?>... columns) {
        this.identifiers = columns;
        this.rows = HartshornUtils.emptyConcurrentList();
    }

    public Table(final Collection<ColumnIdentifier<?>> columns) {
        this.identifiers = columns.toArray(new ColumnIdentifier[0]);
        this.rows = HartshornUtils.emptyConcurrentList();
    }

    private static <T> List<TableRow> matching(final TableRow row, final Table otherTable, final ColumnIdentifier<T> column) {
        final Exceptional<?> exceptionalValue = row.value(column);
        // No way to join on value if it is not present. Technically this should not be possible as a
        // NPE
        // is typically thrown if a null value is added to a row.
        if (exceptionalValue.absent())
            throw new IndexOutOfBoundsException("No value present for " + column.name());
        final T expectedValue = (T) exceptionalValue.get();

        return otherTable.where(column, expectedValue).rows();
    }

    private static void populateAtColumn(
            final Merge merge,
            final boolean populateEmptyEntries,
            final Iterable<TableRow> matchingRows,
            final TableRow joinedRow,
            final ColumnIdentifier<?> identifier
    ) throws EmptyEntryException {
        for (final TableRow matchingRow : matchingRows) {
            /*
             * If there is already a value present on this row, look up if we want to keep the existing,
             * or use the new value.
             */
            if (!joinedRow.value(identifier).present() || Merge.PREFER_FOREIGN == merge)
                joinedRow.add(identifier, matchingRow.value(identifier).get());
        }

        /*
         * If there was no value filled by either this table instance, or the foreign table, try to
         *  populate it with null. If that is not allowed throw an exception.
         */
        if (!joinedRow.value(identifier).present()) {
            if (populateEmptyEntries) joinedRow.add(identifier, null);
            else
                throw new EmptyEntryException(
                        "Could not populate empty entry for column " + identifier.name());
        }
    }

    public static Table of(final TypeContext<?> type) {
        if (!DEFINITIONS.containsKey(type)) {
            final List<ColumnIdentifier<?>> identifiers = HartshornUtils.emptyList();
            for (final FieldContext<?> field : type.fields()) {
                if (field.isTransient()) continue;

                String name = field.name();
                final Exceptional<Property> annotation = field.annotation(Property.class);
                if (annotation.present()) {
                    if (HartshornUtils.notEmpty(annotation.get().value())) name = annotation.get().value();
                }

                identifiers.add(new ColumnIdentifierImpl<>(name, field.type()));
            }
            DEFINITIONS.put(type, identifiers);
        }
        return new Table(DEFINITIONS.getOrDefault(type, HartshornUtils.emptyList()));
    }

    @SafeVarargs
    public static <T> Table of(final TypeContext<T> type, final T... defaultEntries) {
        final Table table = Table.of(type);
        for (final T entry : defaultEntries) {
            table.addRow(entry);
        }
        return table;
    }

    /**
     * Generates a {@link TableRow} from a given object based on the objects {@link Field}s. By default, the
     * field name is used to look up a matching column identifier which is present inside the table. If the
     * field is decorated with {@link Property} the contained {@link ColumnIdentifier} is used instead.
     *
     * <p>If the field is decorated with {@link Property} with {@link Property#ignore()} set to {@code true}
     * the field will not be converted to a column entry in the row. One attempt will be made to make the
     * field accessible if it is not already.
     *
     * @param object
     *         Object to "try to" add as a row to the table
     *
     * @throws IllegalArgumentException
     *         When a field cannot be accessed or cast correctly. Contains
     *         the causing {@link Exception} as the cause.
     * @throws UnknownIdentifierException
     *         When no column identifier could be found or generated, or
     *         when there are not enough fields present to satiate the column identifiers present in this
     *         table.
     */
    public void addRow(final Object object) {
        final TableRow row = new TableRow();
        final TypeContext<Object> type = TypeContext.of(object);
        for (final FieldContext<?> field : type.fields()) {
            final Exceptional<Property> annotation = field.annotation(Property.class);
            if (!(annotation.present() && annotation.get().ignore())) {
                try {
                    ColumnIdentifier columnIdentifier = annotation.map(property -> this.identifier(property.value())).orNull();

                    // If no Identifier annotation was present, try to grab it using the field name
                    if (null == columnIdentifier) columnIdentifier = this.identifier(field.name());

                    // No column identifier was found
                    if (null == columnIdentifier)
                        throw new UnknownIdentifierException(
                                "Unknown column identifier for field named : " + field.name());

                    row.add(columnIdentifier, field.get(object).orNull());
                }
                catch (final IllegalAccessError | ClassCastException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        if (row.columns().size() != this.identifiers.length) {
            throw new UnknownIdentifierException("Expected " + Arrays.toString(this.identifiers) + " (" + this.identifiers.length + ") but got " + row.columns() + " (" + row.columns().size() + ")");
        }
        this.rows.add(row);
    }

    @Nullable
    public ColumnIdentifier identifier(@NonNull final String fieldName) throws ClassCastException {
        for (final ColumnIdentifier columnIdentifier : this.identifiers) {
            if (columnIdentifier.name().equalsIgnoreCase(fieldName)) {
                return columnIdentifier;
            }
        }
        return null;
    }

    /**
     * Generates a {@link TableRow} from a given set of objects. The objects should be in the same
     * order as the table's {@link Table#identifiers()}. If the data type of object does not
     * match up with its expected {@link ColumnIdentifier} an exception is thrown and the row is not
     * inserted into the table.
     *
     * @param values
     *         Objects to "try to" add as rows to the table
     *
     * @throws IllegalArgumentException
     *         When the amount of values does not meet the amount of column
     *         headers, or when the data type of the object does not match the expected type.
     */
    public void addRow(final Object... values) {
        if (values.length != this.identifiers.length)
            throw new IllegalArgumentException(
                    "Amount of given values does not meet amount of column headers");

        final TableRow row = new TableRow();
        for (int i = 0; i < this.identifiers.length; i++) {
            final ColumnIdentifier identifier = this.identifiers[i];
            final Object value = values[i];
            row.add(identifier, value);
        }
        this.rows.add(row);
    }

    /**
     * Filters the table's rows based on their value for a given {@link ColumnIdentifier}. If the
     * value of the row at the given column matches the expected filter value the row is kept,
     * otherwise it is ignored.
     *
     * <p>Returns a new table with only the filtered rows. The origin table is not modified. Either
     * table can be disposed of without affecting the other. Row references are shared between both
     * tables.
     *
     * @param <T>
     *         Indicates the class that both the Identifier and the Filter must have
     * @param column
     *         Indicates which column is used to search through the table
     * @param filter
     *         Indicates what value to search for
     *
     * @return Returns the new table with the filter applied
     * @throws IllegalArgumentException
     *         When a row causes a {@link IdentifierMismatchException}.
     *         Typically, this is never thrown unless changes were made from another thread.
     */
    public <T> Table where(final ColumnIdentifier<T> column, final T filter) {
        if (!this.hasColumn(column))
            throw new UnknownIdentifierException("Cannot look up a column that does not exist");

        final Collection<TableRow> filteredRows = HartshornUtils.emptyList();
        for (final TableRow row : this.rows) {
            final Exceptional<T> value = row.value(column);
            if (!value.present()) continue;
            if (value.get() == filter || value.get().equals(filter)) {
                filteredRows.add(row);
            }
        }
        final Table lookupTable = new Table(this.identifiers);
        for (final TableRow filteredRow : filteredRows) {
            try {
                lookupTable.addRow(filteredRow);
            }
            catch (final IdentifierMismatchException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return lookupTable;
    }

    /**
     * Overloaded method for {@link #join(Table, ColumnIdentifier, Merge, boolean)}. Empty entries
     * will not be populated using this method.
     *
     * @param <T>
     *         The data type of the column
     * @param otherTable
     *         The other/foreign table
     * @param column
     *         The column to join on
     * @param merge
     *         The merge behavior
     *
     * @return A new table with the joined rows
     * @throws EmptyEntryException
     *         Thrown if an entry is empty and cannot be populated
     * @throws IdentifierMismatchException
     *         When an identifier does not exist across both tables
     */
    public <T> Table join(@NonNull final Table otherTable, final ColumnIdentifier<T> column, final Merge merge)
            throws EmptyEntryException, IdentifierMismatchException {
        return this.join(otherTable, column, merge, false);
    }

    private void tryPopulateMissingEntry(
            @NonNull final Table otherTable,
            final boolean populateEmptyEntries,
            final Iterable<ColumnIdentifier<?>> mergedIdentifiers,
            final Table joinedTable,
            final TableRow row
    ) throws IdentifierMismatchException {
        if (!Arrays.equals(this.identifiers(), otherTable.identifiers())) {
            if (populateEmptyEntries) {
                for (final ColumnIdentifier<?> identifier : mergedIdentifiers) {
                    final Exceptional<?> exceptionalValue = row.value(identifier);
                    exceptionalValue
                            .present(value -> row.add(identifier, value))
                            .absent(() -> row.add(identifier, null));
                }
            }
            else {
                throw new IdentifierMismatchException(
                        "Remaining rows were found in the foreign table, but identifiers are not equal. Cannot insert null values!");
            }
        }
        joinedTable.addRow(row);
    }

    /**
     * Joins two tables based on a given {@link ColumnIdentifier}. Rows are joined together based on
     * their value on the given column, if the values match the rows are joined together.
     *
     * <p>Rows from the origin and the foreign table are merged into single rows. If a column exists
     * in both tables, the {@link Merge} behavior indicates which to keep.
     *
     * <p>If a row does not have a matching row in the other table while new columns are created,
     * {@code populateEmptyEntries} indicates whether to treat this is an illegal state, or populate
     * the entry with null.
     *
     * @param <T>
     *         The data type of the column
     * @param otherTable
     *         The other/foreign table
     * @param column
     *         The column to join on
     * @param merge
     *         The merge behavior
     * @param populateEmptyEntries
     *         Whether empty entries should be populated (with null)
     *
     * @return A new table with the joined rows
     * @throws EmptyEntryException
     *         Thrown if an entry is empty and cannot be populated
     * @throws IdentifierMismatchException
     *         When an identifier does not exist across both tables
     */
    public <T> Table join(
            @NonNull final Table otherTable,
            final ColumnIdentifier<T> column,
            final Merge merge,
            final boolean populateEmptyEntries
    ) throws EmptyEntryException, IdentifierMismatchException {
        if (this.hasColumn(column) && otherTable.hasColumn(column)) {

            final List<ColumnIdentifier<?>> mergedIdentifiers = HartshornUtils.emptyList();
            for (final ColumnIdentifier<?> identifier : HartshornUtils.addAll(this.identifiers(), otherTable.identifiers())) {
                if (mergedIdentifiers.contains(identifier)) continue;
                mergedIdentifiers.add(identifier);
            }

            final Table joinedTable = new Table(mergedIdentifiers.toArray(new ColumnIdentifier<?>[0]));
            for (final TableRow row : this.rows()) {
                try {
                    this.populateMatchingRows(
                            otherTable, column, merge, populateEmptyEntries, joinedTable, row);
                }
                catch (final IllegalArgumentException e) {
                    continue;
                }
            }

            /*
            It is possible not all foreign rows had a matching value, if that is the case we will add them here if
            possible, if the foreign table has no additional identifiers which we cannot populate here.
            */
            for (final TableRow row : otherTable.rows())
                this.populateMissingEntries(otherTable, column, populateEmptyEntries, mergedIdentifiers, joinedTable, row);

            return joinedTable;
        }
        throw new IdentifierMismatchException("Column '" + column + "' does not exist in both tables");
    }

    private <T> void populateMatchingRows(
            @NonNull final Table otherTable,
            final ColumnIdentifier<T> column,
            final Merge merge,
            final boolean populateEmptyEntries,
            final Table joinedTable,
            final TableRow row
    ) throws EmptyEntryException, IdentifierMismatchException {
        final List<TableRow> matchingRows = Table.matching(row, otherTable, column);

        final TableRow joinedRow = new TableRow();
        for (final ColumnIdentifier<?> identifier : this.identifiers())
            joinedRow.add(identifier, row.value(identifier).get());

        for (final ColumnIdentifier<?> identifier : otherTable.identifiers())
            Table.populateAtColumn(merge, populateEmptyEntries, matchingRows, joinedRow, identifier);

        joinedTable.addRow(joinedRow);
    }

    private <T> void populateMissingEntries(
            @NonNull final Table otherTable,
            final ColumnIdentifier<T> column,
            final boolean populateEmptyEntries,
            final Iterable<ColumnIdentifier<?>> mergedIdentifiers,
            final Table joinedTable,
            final TableRow row
    ) {
        try {
            final List<TableRow> matchingRows = Table.matching(row, joinedTable, column);
            if (matchingRows.isEmpty())
                this.tryPopulateMissingEntry(
                        otherTable, populateEmptyEntries, mergedIdentifiers, joinedTable, row);
        }
        // skipcq: JAVA-W0052
        catch (final IdentifierMismatchException ignored) {
        }
    }

    /**
     * Selects only the given columns of a table. Returns a new table populated with all the rows in
     * the origin table, but only with the columns provided.
     *
     * @param columns
     *         Indicates the columns to select
     *
     * @return Return the new table with only the selected columns
     */
    public Table select(final ColumnIdentifier<?>... columns) {
        final Table table = new Table(columns);

        this.rows.forEach(row -> {
            final TableRow tmpRow = new TableRow();
            for (final ColumnIdentifier<?> column : columns) {
                row.columns().stream()
                        .filter(column::equals)
                        .forEach(tCol -> tmpRow.add(column, row.value(column).get()));
            }
            try {
                table.addRow(tmpRow);
            }
            catch (final IdentifierMismatchException e) {
                throw new IllegalArgumentException(e);
            }
        });

        return table;
    }

    /**
     * Adds a row to the table if the column identifiers are equal in length and type. If the row has
     * more or less column identifiers it is not accepted into the table. If a row has a column which
     * is not contained in this table it is not accepted into the table.
     *
     * @param row
     *         The row object to add to the table
     *
     * @throws IdentifierMismatchException
     *         When there is a column mismatch between the row and the
     *         table
     */
    public void addRow(final TableRow row) throws IdentifierMismatchException {
        // Check if the row has the same amount of column as this table
        if (row.columns().size() != this.identifiers.length)
            throw new IdentifierMismatchException(
                    "The row does not have the same amount of columns as the table");

        // Check if the row has the same columns with the same order
        for (final ColumnIdentifier<?> column : row.columns()) {
            if (!this.hasColumn(column)) {
                throw new IdentifierMismatchException(
                        "Column '" + column.name() + "' is not contained in table");
            }
        }

        this.rows.add(row);
    }

    /**
     * Returns whether the table contains the given {@link ColumnIdentifier}.
     *
     * @param column
     *         The column
     *
     * @return Whether the column is present
     */
    public boolean hasColumn(final ColumnIdentifier<?> column) {
        for (final ColumnIdentifier<?> identifier : this.identifiers) {
            if (identifier == column || identifier.equals(column)) return true;
        }
        return false;
    }

    /**
     * Attempts to get the first row in the table. If there is no value present, a {@link Exceptional}
     * holding a {@link IndexOutOfBoundsException} will be returned.
     *
     * @return Return the first row of the table
     */
    public Exceptional<TableRow> first() {
        return Exceptional.of(() -> this.rows.get(0));
    }

    /**
     * Attempts to get the last row in the table. If there is no value present, a {@link Exceptional}
     * holding a {@link IndexOutOfBoundsException} will be returned.
     *
     * @return Return the last row of the table
     */
    public Exceptional<TableRow> last() {
        return Exceptional.of(() -> this.rows.get(this.count() - 1));
    }

    /**
     * Gets the amount of rows present in the table.
     *
     * @return Return the table's row count
     */
    public int count() {
        return this.rows.size();
    }

    /**
     * Orders (sorts) a table based on a given column. Requires the data type of the {@link
     * ColumnIdentifier} to be an implementation of {@link Comparable}. This modifies the existing
     * table.
     *
     * @param <T>
     *         The type constraint indicating this method only accepts implementations of {@link
     *         Comparable}
     * @param column
     *         Indicates the column to order by
     * @param order
     *         Indicates what way to order the table by
     *
     * @throws IllegalArgumentException
     *         When the table does not contain the given column, or the data
     *         type is not a {@link Comparable}
     */
    public <T extends Comparable> void orderBy(final ColumnIdentifier<T> column, final Order order) {
        if (!this.hasColumn(column))
            throw new IllegalArgumentException(
                    "Table does not contains column named : " + column.name());

        if (!column.type().childOf(Comparable.class))
            throw new IllegalArgumentException(
                    "Column does not contain a comparable data type : " + column.name());

        this.rows.sort((r1, r2) -> {
            final Comparable c1 = r1.value(column).get();
            final Comparable c2 = r2.value(column).get();
            return Order.ASC == order ? c1.compareTo(c2) : c2.compareTo(c1);
        });
    }

    /**
     * Returns whether the table contains the given {@link TableRow}
     *
     * @param row
     *         The row
     *
     * @return Whether the row is present
     */
    public boolean hasRow(final TableRow row) {
        for (final TableRow tableRow : this.rows()) {
            if (tableRow.equals(row)) return true;
        }
        return false;
    }

    /**
     * Gets all rows in the table.
     *
     * @return Return the table's rows
     */
    public List<TableRow> rows() {
        return HartshornUtils.asUnmodifiableList(this.rows);
    }

    public <T> List<T> rows(final TypeContext<T> type) {
        final List<T> items = HartshornUtils.emptyList();

        final Exceptional<ConstructorContext<T>> constructor = type.defaultConstructor();
        if (constructor.absent()) {
            Hartshorn.log().warn("Could not convert rows to type " + type.name() + " as no base constructor exists");
            return items;
        }

        for (final TableRow row : this.rows) {
            final Exceptional<T> instance = constructor.get().createInstance();
            if (instance.present()) {
                final Map<String, Object> data = row.data().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().name(), Entry::getValue));
                type.populate(instance.get(), data);
            }
        }
        return items;
    }

    public void forEach(final Consumer<TableRow> consumer) {
        this.rows().forEach(consumer);
    }

    @Override
    public String toString() {
        final List<List<String>> rows = HartshornUtils.emptyList();
        final List<String> headers = HartshornUtils.emptyList();
        for (final ColumnIdentifier<?> identifier : this.identifiers) {
            headers.add(identifier.name());
        }
        rows.add(headers);
        for (final TableRow row : this.rows) {
            final List<String> rowValues = HartshornUtils.emptyList();
            // In order of identifiers to ensure values are ordered
            for (final ColumnIdentifier<?> identifier : this.identifiers) {
                rowValues.add(String.valueOf(row.value(identifier).orNull()));
            }
            rows.add(rowValues);
        }
        return HartshornUtils.asTable(rows);
    }
}
