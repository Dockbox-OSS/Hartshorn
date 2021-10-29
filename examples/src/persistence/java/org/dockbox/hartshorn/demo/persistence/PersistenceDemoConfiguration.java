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

package org.dockbox.hartshorn.demo.persistence;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.demo.persistence.services.UserRepository;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.dockbox.hartshorn.persistence.properties.SQLRemoteServer;

import javax.inject.Singleton;

/**
 * A simple configuration service capable of loading a configuration file and providing a custom {@link SqlService}
 * instance. {@link Configuration} is an extension of {@link org.dockbox.hartshorn.core.annotations.service.Service},
 * and therefore has all abilities also found with {@link org.dockbox.hartshorn.core.annotations.service.Service}.
 *
 * <p>{@link Configuration} adds the ability to load configuration files through a configured {@link Configuration#source()}.
 * By default, the {@link FileType} used to read the file is {@link FileType#YAML}, however this can be configured to
 * use any {@link FileType} through {@link Configuration#filetype()}.
 *
 * <p>This configuration is loaded from the {@code persistence-demo.yml} file in the {@code src/main/resources} directory,
 * which will thus be present on the classpath when the application is active. As this means the file will not be present
 * in the local filesystem, the source is prefixes with {@code classpath:}. This allows the configuration activator to
 * load the file from the classpath directly.
 *
 * <p>Values present in configurations are flattened and added to {@link ApplicationContext#property(String, Object) the application properties}.
 * This allows you to use the configuration outside this type directly if required, however it is recommended to keep
 * values restricted to the configuration service which defines them.
 */
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

    /**
     * Provides a singleton instance of {@link SqlService} configured to use a MySQL database, of which the
     * connection information is provided by the {@code persistence-demo.yml} file. As this is a {@link Singleton}
     * provider, the result of this provider will be saved after it is first called.
     *
     * <p>{@link Provider} methods automatically register as the highest priority in the type's {@link org.dockbox.hartshorn.core.binding.BindingHierarchy}
     * in the active {@link ApplicationContext}.
     *
     * @see ApplicationContext#hierarchy(Key)
     * @see org.dockbox.hartshorn.core.binding.BindingHierarchy
     */
    @Provider
    @Singleton
    public UserRepository sql(final ApplicationContext context) {
        final ConnectionAttribute connection = ConnectionAttribute.of(Remotes.MYSQL,
                SQLRemoteServer.of(this.host, this.port, this.database),
                this.user, this.password
        );
        return context.get(UserRepository.class, connection);
    }
}
