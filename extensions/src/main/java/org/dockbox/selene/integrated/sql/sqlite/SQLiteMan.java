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

package org.dockbox.selene.integrated.sql.sqlite;

import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.core.server.properties.InjectorProperty;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.data.table.TableRow;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.dockbox.selene.integrated.data.table.column.SimpleColumnIdentifier;
import org.dockbox.selene.integrated.data.table.exceptions.IdentifierMismatchException;
import org.dockbox.selene.integrated.sql.SQLMan;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;
import org.dockbox.selene.integrated.sql.exceptions.TableMoficationException;
import org.javalite.activejdbc.Base;

import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteMan implements SQLMan, InjectableType {

    public static final String PATH_KEY = "sqliteFilePath";
    private Path filePath;

    @Override
    public boolean canEnable() {
        return true;
    }

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        SeleneUtils.getPropertyValue(PATH_KEY, Path.class, properties)
                .ifPresent(path -> this.filePath = path)
                .orElseThrow(() -> new IllegalArgumentException("Missing value for '" + PATH_KEY + "'"));
    }

    @Override
    public Table getTable(String name) throws TableMoficationException, InvalidConnectionException {
        return this.getTable(this.filePath, name);
    }

    @Override
    public Table getTable(Path filePath, String name) throws TableMoficationException, InvalidConnectionException {
        this.startTransaction();

        DynamicModel model = new DynamicModel();
        model.setTableName(name);

        List<DynamicModel> modelList = DynamicModel.findAll();
        Table table = this.convertToTable(modelList);

        this.endTransaction();
        return table;
    }

    @Override
    public void store(String name, Table table) {
        this.store(this.filePath, name, table);
    }

    @Override
    public void store(Path filePath, String name, Table table) {
        // TODO, Implementation
    }

    private void startTransaction() throws InvalidConnectionException {
        try {
            Class.forName("org.sqlite.JDBC");
            // Attaches the connection to the current thread. TODO: add specific note for developers that this should be used async as much as possible
            Base.attach(DriverManager.getConnection("jdbc:sqlite:" + this.filePath.toFile().getAbsolutePath()));
        } catch (ClassNotFoundException | SQLException e) {
            throw new InvalidConnectionException("Could not open connection to '" + this.filePath.toFile().getAbsolutePath(), e);
        }
    }

    private void endTransaction() {
        Base.close();
    }

    private Table convertToTable(List<DynamicModel> models) {
        if (models.isEmpty()) return new Table();
        List<ColumnIdentifier<?>> columns = this.getColumnIdentifiers(models.get(0));
        Table table = new Table(columns);
        this.populateTable(table, models);
        return table;
    }

    private List<ColumnIdentifier<?>> getColumnIdentifiers(DynamicModel model) {
        List<ColumnIdentifier<?>> identifiers = new ArrayList<>();
        model.getAttributes().forEach((key, attr) -> {
            identifiers.add(new SimpleColumnIdentifier<>(key, attr.getClass()));
        });
        return identifiers;
    }

    private void populateTable(final Table table, final Iterable<DynamicModel> models) {
        models.forEach(model -> {
            try {
                TableRow row = new TableRow();
                model.getAttributes().forEach((key, attr) -> {
                    ColumnIdentifier<?> identifier = table.getIdentifier(key);
                    if (null != identifier)
                        row.addValue(identifier, attr);
                });
                table.addRow(row);
            } catch (IdentifierMismatchException e) {
                throw new IllegalStateException("Loaded identifiers did not match while populating table", e);
            }
        });
    }
}
