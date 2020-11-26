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

package org.dockbox.selene.integrated.sql;

import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.tuple.Tuple;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.data.table.TableRow;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.dockbox.selene.integrated.data.table.column.SimpleColumnIdentifier;
import org.dockbox.selene.integrated.data.table.exceptions.IdentifierMismatchException;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;
import org.dockbox.selene.integrated.sql.properties.SQLColumnProperty;
import org.dockbox.selene.integrated.sql.properties.SQLResetBehaviorProperty;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertValuesStepN;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SQLMan<T> implements ISQLMan<T> {

    private final Map<String, ColumnIdentifier<?>> identifiers = new HashMap<>();
    private Boolean resetOnStore;

    protected DSLContext getContext(T target) throws InvalidConnectionException {
        return DSL.using(this.getConnection(target), this.getDialect());
    }

    protected abstract SQLDialect getDialect();

    protected abstract Connection getConnection(T target) throws InvalidConnectionException;

    protected abstract T getDefaultTarget();

    @Override
    public Table getTable(String name) throws InvalidConnectionException {
        return this.getTable(name, this.getDefaultTarget());
    }

    @Override
    public Table getTable(String name, T target) throws InvalidConnectionException {
        return this.withContext(target, ctx -> {
            Result<Record> results = ctx.select().from(name).fetch();
            try {
                ctx.parsingConnection().close();
            } catch (SQLException e) {
                throw new IllegalStateException("Could not release connection to '" + target + "'", e);
            }
            return this.convertToTable(results);
        });
    }

    private Table convertToTable(Result<Record> results) {
        if (results.isEmpty()) return new Table();

        Table table = new Table(this.getIdentifiers(results));
        results.map((Record record) -> this.convertToTableRow(record, table))
                .forEach(row -> {
                    try {
                        table.addRow(row);
                    } catch (IdentifierMismatchException e) {
                        throw new IllegalStateException("Loaded identifiers did not match while populating table", e);
                    }
                });

        return table;
    }

    private List<ColumnIdentifier<?>> getIdentifiers(Result<Record> results) {
        List<ColumnIdentifier<?>> identifiers = new ArrayList<>();
        for (Field<?> field : results.get(0).fields()) {
            String name = field.getName();

            ColumnIdentifier<?> identifier = this.tryGetColumn(name).orElseGet(() -> {
                Class<?> type = field.getDataType().getType();
                return new SimpleColumnIdentifier<>(name, type);
            });
            identifiers.add(identifier);
        }
        return identifiers;
    }

    private TableRow convertToTableRow(Record record, Table table) {
        TableRow row = new TableRow();
        for (Field<?> field : record.fields()) {
            Object attr = record.getValue(field);
            ColumnIdentifier<?> identifier = table.getIdentifier(field.getName());
            if (null != identifier)
                row.addValue(identifier, attr);
        }
        return row;
    }

    private Exceptional<ColumnIdentifier<?>> tryGetColumn(String key) {
        return Exceptional.ofNullable(this.identifiers.get(key));
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

    public void store(T target, String name, Table table, boolean reset) throws InvalidConnectionException {
        this.withContext(target, ctx -> {
            if (reset) this.drop(target, name);

            Field<?>[] fields = this.convertIdentifiersToFields(table);
            this.createTableIfNotExists(name, ctx, fields);
            InsertValuesStepN<?> insertStep = ctx.insertInto(DSL.table(name))
                    .columns();
            this.populateRemoteTable(insertStep, table, fields);
        });
    }

    private Field<?>[] convertIdentifiersToFields(Table table) {
        Field<?>[] fields = new Field[table.getIdentifiers().length];
        ColumnIdentifier<?>[] tableIdentifiers = table.getIdentifiers();
        for (int i = 0; i < tableIdentifiers.length; i++) {
            ColumnIdentifier<?> identifier = tableIdentifiers[i];
            fields[i] = DSL.field(identifier.getColumnName(), identifier.getType());
        }
        return fields;
    }

    private void populateRemoteTable(InsertValuesStepN<?> insertStep, Table table, Field<?>[] fields) {
        table.getRows().forEach(row -> {
            Object[] values = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                Field<?> field = fields[i];
                ColumnIdentifier<?> identifier = table.getIdentifier(field.getName());
                if (null != identifier) {
                    values[i] = row.getValue(identifier).orElse(null);
                } else values[i] = null;
            }
            insertStep.values(values);
        });
        insertStep.execute();
    }

    private void createTableIfNotExists(String name, DSLContext ctx, Field<?>[] fields) {
        ctx.createTableIfNotExists(name).columns(fields).execute();
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
    public <C> void deleteIf(String name, ColumnIdentifier<C> identifier, C value) throws InvalidConnectionException {
        this.deleteIf(this.getDefaultTarget(), name, identifier, value);
    }

    @Override
    public <C> void deleteIf(T target, String name, ColumnIdentifier<C> identifier, C value) throws InvalidConnectionException {
        this.withContext(target, ctx -> {
            ctx.delete(DSL.table(name))
                    .where(this.getNamedField(identifier).eq(value))
                    .execute();
        });
    }

    private <C> Field<C> getNamedField(ColumnIdentifier<C> identifier) {
        return DSL.field(identifier.getColumnName(), identifier.getType());
    }

    private <R> R withContext(T target, DSLFunction<R> function) throws InvalidConnectionException {
        DSLContext ctx = this.getContext(target);
        R r = function.accept(ctx);
        this.closeConnection(ctx);
        return r;
    }

    private void withContext(T target, DSLConsumer consumer) throws InvalidConnectionException {
        DSLContext ctx = this.getContext(target);
        consumer.accept(ctx);
        this.closeConnection(ctx);
    }

    private void closeConnection(DSLContext ctx) {
        try {
            ctx.parsingConnection().close();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not release connection!", e);
        }
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        SeleneUtils.getSubProperties(SQLColumnProperty.class, properties)
                .forEach(property -> {
                    Tuple<String, ColumnIdentifier<?>> identifier = property.getObject();
                    this.identifiers.put(identifier.getKey(), identifier.getValue());
                });

        this.resetOnStore = SeleneUtils.getPropertyValue(
                SQLResetBehaviorProperty.KEY,
                Boolean.class,
                properties)
                .orElse(true);
    }

    @Override
    public boolean canEnable() {
        return true;
    }
}
