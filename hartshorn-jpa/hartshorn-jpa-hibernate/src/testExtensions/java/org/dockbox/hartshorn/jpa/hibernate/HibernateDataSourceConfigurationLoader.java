/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Path;

import test.org.dockbox.hartshorn.jpa.DataSourceConfigurationLoader;
import test.org.dockbox.hartshorn.jpa.StandardDataSourceConfigurationLoader;

public class HibernateDataSourceConfigurationLoader implements DataSourceConfigurationLoader {

    private final StandardDataSourceConfigurationLoader delegate;

    public HibernateDataSourceConfigurationLoader(final StandardDataSourceConfigurationLoader delegate) {
        this.delegate = delegate;
    }

    @Override
    public DataSourceConfiguration derby(final Path localPath) {
        final DataSourceConfiguration derby = this.delegate.derby(localPath);
        return new HibernateDataSourceConfiguration(derby.url(), derby.driver(), DerbyDialect.class);
    }

    @Override
    public DataSourceConfiguration mssql(final MSSQLServerContainer<?> container) {
        return this.jdbc(this.delegate.mssql(container), SQLServerDialect.class);
    }

    @Override
    public DataSourceConfiguration mariadb(final MariaDBContainer<?> container) {
        return this.jdbc(this.delegate.mariadb(container), MariaDBDialect.class);
    }

    @Override
    public DataSourceConfiguration postgresql(final PostgreSQLContainer<?> container) {
        return this.jdbc(this.delegate.postgresql(container), PostgreSQLDialect.class);
    }

    @Override
    public DataSourceConfiguration mysql(final MySQLContainer<?> container) {
        return this.jdbc(this.delegate.mysql(container), MySQLDialect.class);
    }

    private DataSourceConfiguration jdbc(final DataSourceConfiguration origin, final Class<? extends Dialect> dialect) {
        return new HibernateDataSourceConfiguration(origin.url(), origin.username(), origin.password(), origin.driver(), dialect);
    }
}
