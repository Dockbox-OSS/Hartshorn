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

package org.dockbox.hartshorn.data.remote;

public abstract class JdbcRemote implements Remote<JdbcRemoteConfiguration> {

    protected String connectionString(final JdbcRemoteConfiguration server) {
        String connectionString = "jdbc:%s://%s:%s".formatted(this.type(), server.server(), server.port());
        if (this.includeDatabase()) connectionString = "%s/%s".formatted(connectionString, server.database());
        return connectionString;
    }

    protected abstract String type();

    protected boolean includeDatabase() {
        return true;
    }

    @Override
    public PersistenceConnection connection(final JdbcRemoteConfiguration target, final String user, final String password) {
        return new PersistenceConnection(this.connectionString(target), user, password, this);
    }
}
