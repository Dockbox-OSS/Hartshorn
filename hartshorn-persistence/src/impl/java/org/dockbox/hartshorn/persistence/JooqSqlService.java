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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.tuple.Tuple;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.properties.InjectorProperty;
import org.dockbox.hartshorn.persistence.exceptions.InvalidConnectionException;
import org.dockbox.hartshorn.persistence.exceptions.NoSuchTableException;
import org.dockbox.hartshorn.persistence.properties.SQLColumnProperty;
import org.dockbox.hartshorn.persistence.table.Table;
import org.dockbox.hartshorn.persistence.table.TableRow;
import org.dockbox.hartshorn.persistence.table.ColumnIdentifier;
import org.dockbox.hartshorn.persistence.table.SimpleColumnIdentifier;
import org.dockbox.hartshorn.persistence.exceptions.IdentifierMismatchException;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Mid-level manager for communicating with SQL instances. Acts as middlewhere between {@link DSL}
 * and {@link Table}. All functionality inside this manager is designed to expose only the methods
 * as defined in {@link SqlService}.
 *
 * @param <T>
 *         The type of the target source of a implementation. This is typically passed into
 *         {@link #connection(Object)} and {@link #context(Object)} dynamically, so
 *         implementations can act on the given source. If a source should be constant, the
 *         implementation should make this type {@link Void}.
 */
public abstract class JooqSqlService<T> implements SqlService {

    private final Map<String, ColumnIdentifier<?>> identifiers = HartshornUtils.emptyMap();

    private static Field<?>[] convertIdentifiersToFields(Table table) {
        Field<?>[] fields = new Field[table.identifiers().length];
        ColumnIdentifier<?>[] tableIdentifiers = table.identifiers();
        for (int i = 0; i < tableIdentifiers.length; i++) {
            ColumnIdentifier<?> identifier = tableIdentifiers[i];
            fields[i] = DSL.field(identifier.name(), identifier.type());
        }
        return fields;
    }

    private static void populateRemoteTable(InsertValuesStepN<?> insertStep, Table table, Field<?>[] fields) {
        table.rows().forEach(row -> insertStep.values(JooqSqlService.values(fields, row, table)));
        insertStep.executeAsync();
    }

    private static Object[] values(Field<?>[] fields, TableRow row, Table table) {
        Object[] values = new Object[fields.length];
        // Indexed over iterative to ensure the horizontal location of our value is equal to
        // the location of our field (read; column).
        for (int i = 0; i < fields.length; i++) {
            Field<?> field = fields[i];
            ColumnIdentifier<?> identifier = table.identifier(field.getName());
            // If the identifier in our table is null, it is possible we are working with a
            // filtered row. In this case we accept null into the remote table.
            if (null != identifier) {
                values[i] = row.value(identifier).orNull();
            }
            else values[i] = null;
        }
        return values;
    }

    private static void createTableIfNotExists(String name, DSLContext ctx, Field<?>[] fields) {
        ctx.createTableIfNotExists(name).columns(fields).execute();
    }

    @Override
    public Table table(String name) throws InvalidConnectionException, NoSuchTableException {
        return this.table(name, this.defaultTarget());
    }

    @Override
    public Table table(String name, Table definition) throws InvalidConnectionException {
        try {
            return this.table(name);
        }
        catch (NoSuchTableException e) {
            this.insert(name, definition);
            return this.tableSafe(name).or(definition);
        }
    }

    public Table table(String name, T target) throws InvalidConnectionException, NoSuchTableException {
        try {
            return this.withContext(target, ctx -> {
                // .fetch() automatically closes the native ResultSet and leaves us with the Result.
                // This can be used safely.
                Result<org.jooq.Record> results = ctx.select().from(name).fetch();
                if (results.isEmpty()) {
                    // If the table is empty we still want a accurate representation of its schema
                    return new Table(this.identifiers(results.fields()));
                }
                else {
                    return this.convertToTable(results);
                }
            });
        }
        catch (DataAccessException e) {
            throw new NoSuchTableException(name, e);
        }
    }

    @Override
    public Exceptional<Table> tableSafe(String name) {
        return Exceptional.of(() -> this.table(name));
    }

    /**
     * Get the default target if none is provided, this can be injected in {@link
     * #apply(InjectorProperty)}.
     *
     * @return The default target
     */
    protected abstract T defaultTarget();

    private <R> R withContext(T target, DSLFunction<R> function) throws InvalidConnectionException {
        DSLContext ctx = this.context(target);
        R r = function.accept(ctx);
        JooqSqlService.closeConnection(ctx);
        return r;
    }

    private List<ColumnIdentifier<?>> identifiers(Field<?>[] fields) {
        List<ColumnIdentifier<?>> identifiers = HartshornUtils.emptyList();
        for (Field<?> field : fields) {
            String name = field.getName();

            // Developers can define custom column bindings in enable, here we look those up before
            // constructing a custom column identifier.
            ColumnIdentifier<?> identifier = this.tryGetColumn(name).get(() -> {
                Class<?> type = field.getDataType().getType();
                return new SimpleColumnIdentifier<>(name, type);
            });
            identifiers.add(identifier);
        }
        return identifiers;
    }

    private Table convertToTable(Result<org.jooq.Record> results) {
        Table table = new Table(this.identifiers(results.fields()));

        // We cannot populate the table if no results exist, in which case we return a empty table.
        if (results.isEmpty()) return table;

        results
                .map((org.jooq.Record record) -> this.convertToTableRow(record, table))
                .forEach(row -> {
                    try {
                        table.addRow(row);
                    }
                    catch (IdentifierMismatchException e) {
                        // This should typically never be thrown unless either the table or result was
                        // modified after fetching.
                        throw new IllegalStateException(
                                "Loaded identifiers did not match while populating table", e);
                    }
                });

        return table;
    }

    /**
     * Get the {@link DSLContext} which will be used to communicate to the database. Automatically
     * constructs a {@link Connection} based on the given target.
     *
     * @param target
     *         The target database source
     *
     * @return The context
     * @throws InvalidConnectionException
     *         When the connection could not be prepared, either because of
     *         it being unavailable or because of invalid configurations.
     */
    protected DSLContext context(T target) throws InvalidConnectionException {
        return DSL.using(this.connection(target), this.dialect());
    }

    private static void closeConnection(DSLContext ctx) {
        try {
            // While DSLContext also holds a diagnostics connection, only the parsing connection is active
            // in our use case.
            ctx.parsingConnection().close();
        }
        catch (SQLException e) {
            throw new IllegalStateException("Could not release connection!", e);
        }
    }

    private Exceptional<ColumnIdentifier<?>> tryGetColumn(String key) {
        return Exceptional.of(this.identifiers.get(key));
    }

    private TableRow convertToTableRow(org.jooq.Record record, Table table) {
        TableRow row = new TableRow();
        for (Field<?> field : record.fields()) {
            Object attr = record.getValue(field);
            ColumnIdentifier<?> identifier =
                    this.identifiers.getOrDefault(field.getName(), table.identifier(field.getName()));
            // May cause the row to be rejected by the target Table, though typically the identifier is
            // never null unless the result was modified after fetching.
            if (null != identifier) row.add(identifier, attr);
        }
        return row;
    }

    /**
     * Get the connection which can be used by {@link #context(Object)} to prepare the internal
     * {@link DSLContext}.
     *
     * @param target
     *         The target database source
     *
     * @return The connection
     * @throws InvalidConnectionException
     *         When the connection could not be prepared, either because of
     *         it being unavailable or because of invalid configurations.
     */
    protected abstract Connection connection(T target) throws InvalidConnectionException;

    /**
     * Get the dialect which will be used for a given implementation.
     *
     * @return The SQL dialect
     */
    protected abstract SQLDialect dialect();

    @Override
    public void insert(String name, Table table) throws InvalidConnectionException {
        this.withContext(this.defaultTarget(), ctx -> {
            Field<?>[] fields = JooqSqlService.convertIdentifiersToFields(table);
            JooqSqlService.createTableIfNotExists(name, ctx, fields);
            InsertValuesStepN<?> insertStep = ctx.insertInto(DSL.table(name)).columns();
            JooqSqlService.populateRemoteTable(insertStep, table, fields);
        });
    }

    @Override
    public void insertUnique(String name, Table table, ColumnIdentifier<?> identifier) throws InvalidConnectionException {
        this.handleUniqueInsert(name, table, identifier);
    }

    private <F> void handleUniqueInsert(String name, Table table, ColumnIdentifier<F> identifier) throws InvalidConnectionException {
        this.withContext(this.defaultTarget(), ctx -> {
            org.jooq.Table<org.jooq.Record> remote = DSL.table(name);
            final Field<?>[] fields = JooqSqlService.convertIdentifiersToFields(table);
            //noinspection unchecked
            final Field<F> field = (Field<F>) remote.field(identifier.name());
            for (TableRow row : table.rows()) {
                ctx.insertInto(remote, fields)
                        .values(JooqSqlService.values(fields, row, table))
                        .select(DSL.select(DSL.asterisk()).whereNotExists(DSL.selectOne()
                                .from(remote)
                                .where(field.eq(DSL.field(identifier.name(), identifier.type())))
                        ))
                        .executeAsync();
            }
        });
    }

    @Override
    public void insert(String name, Table table, boolean reset) throws InvalidConnectionException {
        if (reset) this.drop(name);
        this.insert(name, table);
    }

    @Override
    public void drop(String name) throws InvalidConnectionException {
        this.withContext(this.defaultTarget(), ctx -> {
            ctx.dropTableIfExists(DSL.table(name)).execute();
        });
    }

    @Override
    public <C> void deleteIf(String name, ColumnIdentifier<C> identifier, C value)
            throws InvalidConnectionException {
        this.withContext(this.defaultTarget(), ctx -> {
            ctx.delete(DSL.table(name)).where(JooqSqlService.namedField(identifier).eq(value)).execute();
        });
    }

    private static <C> Field<C> namedField(ColumnIdentifier<C> identifier) {
        return DSL.field(identifier.name(), identifier.type());
    }

    private void withContext(T target, DSLConsumer consumer) throws InvalidConnectionException {
        DSLContext ctx = this.context(target);
        consumer.accept(ctx);
        JooqSqlService.closeConnection(ctx);
    }

    @Override
    public void apply(InjectorProperty<?> property) throws ApplicationException {
        if (property instanceof SQLColumnProperty columnProperty) {
            Tuple<String, ColumnIdentifier<?>> identifier = columnProperty.value();
            this.identifiers.put(identifier.getKey(), identifier.getValue());
        }
    }
}
