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

package org.dockbox.hartshorn.persistence.properties;

import java.nio.file.Path;
import java.util.function.Function;

import lombok.Getter;

public enum Remotes implements Remote {
    DERBY(Path.class, path -> "jdbc:derby:directory:%s/db;create=true".formatted(path.toFile().getAbsolutePath()), "org.apache.derby.jdbc.EmbeddedDriver"),
    MYSQL(SQLRemoteServer.class, server -> connectionString("mysql", server), "com.mysql.jdbc.Driver"),
    POSTGRESQL(SQLRemoteServer.class, server -> connectionString("postgresql", server), "org.postgresql.Driver"),
    ;

    private final Class<?> target;
    private final Function<?, String> urlGen;
    @Getter private final String driver;

    <T> Remotes(Class<T> target, Function<T, String> urlGen, String driver) {
        this.target = target;
        this.urlGen = urlGen;
        this.driver = driver;
    }

    private static String connectionString(String type, SQLRemoteServer server) {
        return "jdbc:%s://%s:%s/%s".formatted(type, server.server(), server.port(), server.database());
    }

    @Override
    public PersistenceConnection connection(Object target, String user, String password) {
        return this.connection(this.url(target), user, password);
    }

    @Override
    public PersistenceConnection connection(String url, String user, String password) {
        return new PersistenceConnection(url, user, password, this);
    }

    @Override
    public String url(Object target) {
        if (this.target.isInstance(target)) {
            //noinspection unchecked
            return ((Function<Object, String>) this.urlGen).apply(target);
        }
        throw new IllegalArgumentException("Provided target was expected to be of type " + this.target.getSimpleName() + " but was: " + target);
    }
}
