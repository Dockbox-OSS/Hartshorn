/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.demo.persistence;

import org.dockbox.hartshorn.config.annotations.Configuration;
import org.dockbox.hartshorn.config.annotations.Value;
import org.dockbox.hartshorn.core.Key;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.demo.persistence.services.UserRepository;
import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.MySQLRemote;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.data.remote.JdbcRemoteConfiguration;

import javax.inject.Singleton;

/**
 * A simple configuration service capable of loading a configuration file and providing a custom {@link JpaRepository}
 * instance. {@link Configuration} is an extension of {@link Service},
 * and therefore has all abilities also found with {@link Service}.
 *
 * <p>{@link Configuration} adds the ability to load configuration files through a configured {@link Configuration#source()}.
 * By default, the {@link FileFormats} used to read the file is {@link FileFormats#YAML}, however this can be configured to
 * use any {@link FileFormats} through {@link Configuration#filetype()}.
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
     * Provides a singleton instance of {@link JpaRepository} configured to use a MySQL database, of which the
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
        final JdbcRemoteConfiguration configuration = JdbcRemoteConfiguration.of(this.host, this.port, this.database);
        final PersistenceConnection connection = MySQLRemote.INSTANCE.connection(configuration, this.user, this.password);
        return (UserRepository) context.get(UserRepository.class).connection(connection);
    }
}
