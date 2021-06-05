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
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.persistence.exceptions.InvalidConnectionException;
import org.dockbox.hartshorn.persistence.table.Table;
import org.dockbox.hartshorn.persistence.table.column.ColumnIdentifier;

/**
 * Low-level interface for communicating with SQL databases.
 *
 * @param <T>
 *         The type of the target database source
 */
public interface ISQLMan<T> extends InjectableType {

    Table getOrCreateTable(String name, Table empty) throws InvalidConnectionException;

    Table getOrCreateTable(String name, T target, Table empty) throws InvalidConnectionException;

    /**
     * Gets a table from the database and converts it to the internal {@link Table} type. Uses the
     * default or no target, depending on the implementation.
     *
     * @param name
     *         The name of the table as defined in the database.
     *
     * @return The table
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    Table getTable(String name) throws InvalidConnectionException;

    /**
     * Gets a table from the database and converts it to the internal {@link Table} type.
     *
     * @param name
     *         The name of the table as defined in the database.
     * @param target
     *         The target database source
     *
     * @return The table
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    Table getTable(String name, T target) throws InvalidConnectionException;

    /**
     * Attempts to get a table from the database and converts it to the internal {@link Table} type
     * wrapped in a {@link Exceptional}. If the table does not exist, or no connection could be made
     * to the database, {@link Exceptional#none()} is returned.
     *
     * @param name
     *         The name of the table as defined in the database.
     *
     * @return The table wrapped in a {@link Exceptional}, or {@link Exceptional#none()}
     */
    Exceptional<Table> getTableSafe(String name);

    /**
     * Attempts to get a table from the database and converts it to the internal {@link Table} type
     * wrapped in a {@link Exceptional}. If the table does not exist, or no connection could be made
     * to the database, {@link Exceptional#none()} is returned.
     *
     * @param name
     *         The name of the table as defined in the database.
     * @param target
     *         The target database source
     *
     * @return The table wrapped in a {@link Exceptional}, or {@link Exceptional#none()}
     */
    Exceptional<Table> getTableSafe(String name, T target);

    /**
     * Stores a table to the database under a given name. Depending on the implementation this may
     * update a existing table, or create a new one. Uses the default or no target, depending on the
     * implementation.
     *
     * @param name
     *         The name of the table in the database
     * @param table
     *         The table to store
     *
     * @throws InvalidConnectionException
     *         the invalid connection exception
     */
    void store(String name, Table table) throws InvalidConnectionException;

    /**
     * Stores a table to the database under a given name. Depending on the implementation this may
     * update a existing table, or create a new one.
     *
     * @param name
     *         The name of the table in the database
     * @param table
     *         The table to store
     * @param target
     *         The target database source
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    void store(T target, String name, Table table) throws InvalidConnectionException;

    /**
     * Stores a table to the database under a given name. If a table already exists it will be dropped
     * and recreated depending on the given argument. Uses the default or no target, depending on the
     * implementation. Uses the default or no target, depending on the implementation.
     *
     * @param name
     *         The name of the table in the database
     * @param table
     *         The table to store
     * @param reset
     *         Whether or not to drop a table if it already exists
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    void store(String name, Table table, boolean reset) throws InvalidConnectionException;

    /**
     * Stores a table to the database under a given name. If a table already exists it will be dropped
     * and recreated depending on the given argument. Uses the default or no target, depending on the
     * implementation.
     *
     * @param name
     *         The name of the table in the database
     * @param table
     *         The table to store
     * @param reset
     *         Whether or not to drop a table if it already exists
     * @param target
     *         The target database source
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    void store(T target, String name, Table table, boolean reset) throws InvalidConnectionException;

    /**
     * Drops a table on the database if it exists. Uses the default or no target, depending on the
     * implementation.
     *
     * @param name
     *         The name of the table to drop
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    void drop(String name) throws InvalidConnectionException;

    /**
     * Drops a table on the database if it exists.
     *
     * @param name
     *         The name of the table to drop
     * @param target
     *         The target database source
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    void drop(T target, String name) throws InvalidConnectionException;

    /**
     * Deletes a set of rows from a table if they meet a certain value on a given column. Uses the
     * default or no target, depending on the implementation.
     *
     * @param <C>
     *         The type constraint for the identifier and value
     * @param name
     *         The name of the table
     * @param identifier
     *         The column identifier
     * @param value
     *         The matching value
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    <C> void deleteIf(String name, ColumnIdentifier<C> identifier, C value)
            throws InvalidConnectionException;

    /**
     * Deletes a set of rows from a table if they meet a certain value on a given column.
     *
     * @param <C>
     *         The type constraint for the identifier and value
     * @param name
     *         The name of the table
     * @param identifier
     *         The column identifier
     * @param value
     *         The matching value
     * @param target
     *         The target database source
     *
     * @throws InvalidConnectionException
     *         If no connection could be prepared to the database
     */
    <C> void deleteIf(T target, String name, ColumnIdentifier<C> identifier, C value)
            throws InvalidConnectionException;
}
