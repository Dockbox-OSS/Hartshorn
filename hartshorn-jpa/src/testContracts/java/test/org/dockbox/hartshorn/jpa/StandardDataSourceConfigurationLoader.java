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

package test.org.dockbox.hartshorn.jpa;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.junit.jupiter.api.Assumptions;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.nio.file.Path;

public class StandardDataSourceConfigurationLoader implements DataSourceConfigurationLoader {

    @Override
    public DataSourceConfiguration derby(final Path localPath) {
        try {
            final String connectionString = "jdbc:derby:directory:%s/db;create=true".formatted(localPath.toFile().getAbsolutePath());
            return new DataSourceConfiguration(connectionString, EmbeddedDriver.class);
        }
        catch (final Exception e) {
            Assumptions.assumeTrue(false);
            return null;
        }
    }

    @Override
    public DataSourceConfiguration mssql(final MSSQLServerContainer<?> container) {
        final Integer port = container.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT);
        final String url = "jdbc:sqlserver://localhost:%s;encrypt=true;trustServerCertificate=true;".formatted(port);
        return new DataSourceConfiguration(url, container.getUsername(), container.getPassword(), SQLServerDriver.class);
    }

    @Override
    public DataSourceConfiguration mysql(final MySQLContainer<?> container) {
        return this.jdbc("mysql", com.mysql.cj.jdbc.Driver.class, container, MySQLContainer.MYSQL_PORT);
    }

    @Override
    public DataSourceConfiguration postgresql(final PostgreSQLContainer<?> container) {
        return this.jdbc("postgresql", org.postgresql.Driver.class, container, PostgreSQLContainer.POSTGRESQL_PORT);
    }

    @Override
    public DataSourceConfiguration mariadb(final MariaDBContainer<?> container) {
        return this.jdbc("mariadb", org.mariadb.jdbc.Driver.class, container, 3306);
    }

    protected DataSourceConfiguration jdbc(final String type,
                                           final Class<? extends java.sql.Driver> driver,
                                           final JdbcDatabaseContainer<?> container,
                                           final int defaultPort) {
        final String url = "jdbc:%s://%s:%s/%s".formatted(
                type,
                "localhost",
                container.getMappedPort(defaultPort),
                JpaTestContractProviders.DEFAULT_DATABASE
        );

        return new DataSourceConfiguration(url, container.getUsername(), container.getPassword(), driver);
    }
}
