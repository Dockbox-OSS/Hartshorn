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

package org.dockbox.hartshorn.demo.persistence;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.di.annotations.inject.Provider;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.dockbox.hartshorn.persistence.properties.SQLRemoteServer;

import javax.inject.Singleton;

@Configuration(source = "classpath:persistence-demo")
public class PersistenceDemoConfiguration {

    @Value("demo.persistence.db.host")
    private String host;
    @Value("demo.persistence.db.port")
    private int port;
    @Value("demo.persistence.db.database")
    private String database;
    @Value("demo.persistence.db.user")
    private String user;
    @Value("demo.persistence.db.password")
    private String password;

    @Provider
    @Singleton
    public SqlService sql(ApplicationContext context) {
        ConnectionAttribute connection = ConnectionAttribute.of(Remotes.MYSQL,
                SQLRemoteServer.of(this.host, this.port, this.database),
                this.user, this.password
        );
        return context.get(SqlService.class, connection);
    }
}
