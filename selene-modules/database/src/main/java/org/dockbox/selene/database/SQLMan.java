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

package org.dockbox.selene.database;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.table.Table;
import org.dockbox.selene.api.domain.table.TableRow;
import org.dockbox.selene.api.domain.table.column.ColumnIdentifier;
import org.dockbox.selene.api.domain.table.column.SimpleColumnIdentifier;
import org.dockbox.selene.api.domain.table.exceptions.IdentifierMismatchException;
import org.dockbox.selene.api.domain.tuple.Tuple;
import org.dockbox.selene.api.keys.Keys;
import org.dockbox.selene.database.exceptions.InvalidConnectionException;
import org.dockbox.selene.database.exceptions.NoSuchTableException;
import org.dockbox.selene.database.properties.SQLColumnProperty;
import org.dockbox.selene.database.properties.SQLResetBehaviorProperty;
import org.dockbox.selene.di.properties.InjectorProperty;
import org.dockbox.selene.util.SeleneUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Record;
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
 * as defined in {@link ISQLMan}.
 *
 * @param <T>
 *         The type of the target source of a implementation. This is typically passed into
 *         {@link #getConnection(Object)} and {@link #getContext(Object)} dynamically, so
 *         implementations can act on the given source. If a source should be constant, the
 *         implementation should make this type {@link Void}.
 */
public abstract class SQLMan<T> implements ISQLMan<T> {

    private final Map<String, ColumnIdentifier<?>> identifiers = SeleneUtils.emptyMap();
    private Boolean resetOnStore;

    private static Field<?>[] convertIdentifiersToFields(Table table) {
        Field<?>[] fields = new Field[table.getIdentifiers().length];
        ColumnIdentifier<?>[] tableIdentifiers = table.getIdentifiers();
        for (int i = 0; i < tableIdentifiers.length; i++) {
            ColumnIdentifier<?> identifier = tableIdentifiers[i];
            fields[i] = DSL.field(identifier.getColumnName(), identifier.getType());
        }
        return fields;
    }

    private static void populateRemoteTable(InsertValuesStepN<?> insertStep, Table table, Field<?>[] fields) {
        table.getRows().forEach(row -> {
            Object[] values = new Object[fields.length];
            // Indexed over iterative to ensure the horizontal location of our value is equal to
            // the location of our field (read; column).
            for (int i = 0; i < fields.length; i++) {
                Field<?> field = fields[i];
                ColumnIdentifier<?> identifier = table.getIdentifier(field.getName());
                // If the identifier in our table is null, it is possible we are working with a
                // filtered row. In this case we accept null into the remote table.
                if (null != identifier) {
                    values[i] = row.getValue(identifier).orNull();
                }
                else values[i] = null;
            }
            insertStep.values(values);
        });
        insertStep.execute();
    }

    private static void createTableIfNotExists(String name, DSLContext ctx, Field<?>[] fields) {
        ctx.createTableIfNotExists(name).columns(fields).execute();
    }

    @Override
    public Table getOrCreateTable(String name, Table empty) throws InvalidConnectionException {
        return this.getOrCreateTable(name, this.getDefaultTarget(), empty);
    }

    @Override
    public Table getOrCreateTable(String name, T target, Table empty)
            throws InvalidConnectionException {
        try {
            return this.getTable(name, target);
        }
        catch (NoSuchTableException e) {
            this.store(name, empty);
            return this.getTableSafe(name, target).or(empty);
        }
    }

    @Override
    public Table getTable(String name) throws InvalidConnectionException {
        return this.getTable(name, this.getDefaultTarget());
    }

    @Override
    public Table getTable(String name, T target) throws InvalidConnectionException {
        try {
            return this.withContext(target, ctx -> {
                // .fetch() automatically closes the native ResultSet and leaves us with the Result.
                // This can be used safely.
                Result<Record> results = ctx.select().from(name).fetch();
                if (results.isEmpty()) {
                    // If the table is empty we still want a accurate representation of its schema
                    return new Table(this.getIdentifiers(results.fields()));
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
    public Exceptional<Table> getTableSafe(String name) {
        return Exceptional.of(() -> this.getTable(name));
    }

    /**
     * Get the default target if none is provided, this can be injected in {@link
     * #stateEnabling(InjectorProperty[])}.
     *
     * @return The default target
     */
    protected abstract T getDefaultTarget();

    private <R> R withContext(T target, DSLFunction<R> function) throws InvalidConnectionException {
        DSLContext ctx = this.getContext(target);
        R r = function.accept(ctx);
        SQLMan.closeConnection(ctx);
        return r;
    }

    private List<ColumnIdentifier<?>> getIdentifiers(Field<?>[] fields) {
        List<ColumnIdentifier<?>> identifiers = SeleneUtils.emptyList();
        for (Field<?> field : fields) {
            String name = field.getName();

            // Developers can define custom column bindings in stateEnabling, here we look those up before
            // constructing a custom column identifier.
            ColumnIdentifier<?> identifier = this.tryGetColumn(name).get(() -> {
                Class<?> type = field.getDataType().getType();
                return new SimpleColumnIdentifier<>(name, type);
            });
            identifiers.add(identifier);
        }
        return identifiers;
    }

    private Table convertToTable(Result<Record> results) {
        Table table = new Table(this.getIdentifiers(results.fields()));

        // We cannot populate the table if no results exist, in which case we return a empty table.
        if (results.isEmpty()) return table;

        results
                .map((Record record) -> this.convertToTableRow(record, table))
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
    protected DSLContext getContext(T target) throws InvalidConnectionException {
        return DSL.using(this.getConnection(target), this.getDialect());
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

    private TableRow convertToTableRow(Record record, Table table) {
        TableRow row = new TableRow();
        for (Field<?> field : record.fields()) {
            Object attr = record.getValue(field);
            ColumnIdentifier<?> identifier =
                    this.identifiers.getOrDefault(field.getName(), table.getIdentifier(field.getName()));
            // May cause the row to be rejected by the target Table, though typically the identifier is
            // never null unless the result was modified after fetching.
            if (null != identifier) row.addValue(identifier, attr);
        }
        return row;
    }

    /**
     * Get the connection which can be used by {@link #getContext(Object)} to prepare the internal
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
    protected abstract Connection getConnection(T target) throws InvalidConnectionException;

    /**
     * Get the dialect which will be used for a given implementation.
     *
     * @return The SQL dialect
     */
    protected abstract SQLDialect getDialect();

    @Override
    public Exceptional<Table> getTableSafe(String name, T target) {
        return Exceptional.of(() -> this.getTable(name, target));
    }

    @Override
    public void store(String name, Table table) throws InvalidConnectionException {
        this.store(this.getDefaultTarget(), name, table, this.resetOnStore);
    }

    @Override
    public void store(T target, String name, Table table) throws InvalidConnectionException {
        this.store(target, name, table, this.resetOnStore);
    }

    public void store(String name, Table table, boolean reset) throws InvalidConnectionException {
        this.store(this.getDefaultTarget(), name, table, reset);
    }

    public void store(T target, String name, Table table, boolean reset)
            throws InvalidConnectionException {
        this.withContext(target, ctx -> {
            // Use .drop() over .deleteIf() here, as the table definition may have changed
            if (reset) this.drop(target, name);

            Field<?>[] fields = SQLMan.convertIdentifiersToFields(table);
            SQLMan.createTableIfNotExists(name, ctx, fields);
            InsertValuesStepN<?> insertStep = ctx.insertInto(DSL.table(name)).columns();
            SQLMan.populateRemoteTable(insertStep, table, fields);
        });
    }

    @Override
    public void drop(String name) throws InvalidConnectionException {
        this.drop(this.getDefaultTarget(), name);
    }

    @Override
    public void drop(T target, String name) throws InvalidConnectionException {
        this.withContext(target, ctx -> {
            ctx.dropTableIfExists(DSL.table(name)).execute();
        });
    }

    @Override
    public <C> void deleteIf(String name, ColumnIdentifier<C> identifier, C value)
            throws InvalidConnectionException {
        this.deleteIf(this.getDefaultTarget(), name, identifier, value);
    }

    @Override
    public <C> void deleteIf(T target, String name, ColumnIdentifier<C> identifier, C value)
            throws InvalidConnectionException {
        this.withContext(target, ctx -> {
            ctx.delete(DSL.table(name)).where(SQLMan.getNamedField(identifier).eq(value)).execute();
        });
    }

    private static <C> Field<C> getNamedField(ColumnIdentifier<C> identifier) {
        return DSL.field(identifier.getColumnName(), identifier.getType());
    }

    private void withContext(T target, DSLConsumer consumer) throws InvalidConnectionException {
        DSLContext ctx = this.getContext(target);
        consumer.accept(ctx);
        SQLMan.closeConnection(ctx);
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Keys.valuesOfType(SQLColumnProperty.class, properties).forEach(property -> {
            Tuple<String, ColumnIdentifier<?>> identifier = property.getObject();
            this.identifiers.put(identifier.getKey(), identifier.getValue());
        });

        this.resetOnStore = Keys.value(SQLResetBehaviorProperty.KEY, Boolean.class, properties).or(true);
    }
}
