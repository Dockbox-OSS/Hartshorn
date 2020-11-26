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

import org.dockbox.selene.core.server.properties.InjectableType;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.data.table.column.ColumnIdentifier;
import org.dockbox.selene.integrated.sql.exceptions.InvalidConnectionException;

public interface ISQLMan<T> extends InjectableType {

    Table getTable(String name) throws InvalidConnectionException;
    Table getTable(String name, T target) throws InvalidConnectionException;

    void store(String name, Table table) throws InvalidConnectionException;
    void store(T target, String name, Table table) throws InvalidConnectionException;

    void store(String name, Table table, boolean reset) throws InvalidConnectionException;
    void store(T target, String name, Table table, boolean reset) throws InvalidConnectionException;

    void drop(String name) throws InvalidConnectionException;
    void drop(T target, String name) throws InvalidConnectionException;

    <C> void deleteIf(String name, ColumnIdentifier<C> identifier, C value) throws InvalidConnectionException;
    <C> void deleteIf(T target, String name, ColumnIdentifier<C> identifier, C value) throws InvalidConnectionException;

}
