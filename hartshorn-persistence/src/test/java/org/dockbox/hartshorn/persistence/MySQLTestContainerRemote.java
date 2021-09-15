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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.exceptions.NotImplementedException;
import org.dockbox.hartshorn.persistence.hibernate.HibernateRemote;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;
import org.dockbox.hartshorn.persistence.properties.SQLRemoteServer;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL57Dialect;

public class MySQLTestContainerRemote extends HibernateRemote {

    private static final String format = "jdbc:tc:mysql:5.7.34://%s/%s";

    @Override
    public PersistenceConnection connection(Object target, String user, String password) {
        if (target instanceof SQLRemoteServer remoteServer) {
            return new PersistenceConnection(format.formatted(remoteServer.server(), remoteServer.database()), "test", "test", this);
        }
        throw new IllegalArgumentException("Container tests can only create from SQLRemoteServer instance");
    }

    @Override
    public PersistenceConnection connection(String url, String user, String password) {
        throw new NotImplementedException();
    }

    @Override
    public String url(Object target) {
        return null;
    }

    @Override
    public String driver() {
        return "com.mysql.jdbc.Driver";
    }

    @Override
    public Class<? extends Dialect> dialect() {
        return MySQL57Dialect.class;
    }
}
