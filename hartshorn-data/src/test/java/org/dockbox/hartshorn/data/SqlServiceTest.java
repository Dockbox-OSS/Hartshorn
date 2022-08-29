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

package org.dockbox.hartshorn.data;

import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.mysql.cj.jdbc.Driver;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.data.annotations.UseConfigurations;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.hibernate.HibernateDataSourceConfiguration;
import org.dockbox.hartshorn.data.hibernate.HibernateJpaRepository;
import org.dockbox.hartshorn.data.jpa.EntityManagerCarrier;
import org.dockbox.hartshorn.data.jpa.EntityManagerJpaRepository;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.DataSourceList;
import org.dockbox.hartshorn.data.remote.RefreshableDataSourceList;
import org.dockbox.hartshorn.data.service.JpaRepositoryFactory;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.hibernate.Session;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MariaDBDialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@HartshornTest
@Testcontainers(disabledWithoutDocker = true)
@UsePersistence
@UseConfigurations
class SqlServiceTest {

    @Inject
    private ApplicationContext applicationContext;

    protected static final String DEFAULT_DATABASE = "HartshornDb_" + System.nanoTime();

    // will be shared between test methods
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(DEFAULT_DATABASE);
    @Container private static final PostgreSQLContainer<?> postgreSql = new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE).withDatabaseName(DEFAULT_DATABASE);
    @Container private static final MariaDBContainer<?> mariaDb = new MariaDBContainer<>(MariaDBContainer.NAME).withDatabaseName(DEFAULT_DATABASE);
    @Container private static final MSSQLServerContainer<?> mssqlServer = new MSSQLServerContainer<>(MSSQLServerContainer.IMAGE).acceptLicense();

    public static Stream<Arguments> containers() {
        return Stream.of(
                Arguments.of(mySql),
                Arguments.of(postgreSql),
                Arguments.of(mariaDb),
                Arguments.of(mssqlServer)
        );
    }

    @ParameterizedTest
    @MethodSource("containers")
    void testContainerStates(final ContainerState container) {
        Assertions.assertTrue(container.isRunning());
    }

    public static Stream<Arguments> dialects() {
        return Stream.of(
                Arguments.of(derby()),
                Arguments.of(mssql()),
                Arguments.of(jdbc("mysql", com.mysql.cj.jdbc.Driver.class, mySql, MySQLDialect.class, MySQLContainer.MYSQL_PORT)),
                Arguments.of(jdbc("postgresql", org.postgresql.Driver.class, postgreSql, PostgreSQLDialect.class, PostgreSQLContainer.POSTGRESQL_PORT)),
                Arguments.of(jdbc("mariadb", org.mariadb.jdbc.Driver.class, mariaDb, MariaDBDialect.class, 3306))
        );
    }

    protected static DataSourceConfiguration jdbc(
            final String type, final Class<? extends java.sql.Driver> driver, final JdbcDatabaseContainer<?> container,
            final Class<? extends Dialect> dialect, final int defaultPort) {
        final String url = "jdbc:%s://%s:%s/%s".formatted(
                type,
                "localhost",
                container.getMappedPort(defaultPort),
                DEFAULT_DATABASE
        );
        return new HibernateDataSourceConfiguration(url, container.getUsername(), container.getPassword(), driver, dialect);
    }

    protected static DataSourceConfiguration mssql() {
        final Integer port = mssqlServer.getMappedPort(MSSQLServerContainer.MS_SQL_SERVER_PORT);
        final String url = "jdbc:sqlserver://localhost:%s;encrypt=true;trustServerCertificate=true;".formatted(port);
        return new HibernateDataSourceConfiguration(url, mssqlServer.getUsername(), mssqlServer.getPassword(), SQLServerDriver.class, SQLServerDialect.class);
    }

    protected static DataSourceConfiguration derby() {
        try {
            final Path dir = Files.createTempDirectory("derby");
            final String connectionString = "jdbc:derby:directory:%s/db;create=true".formatted(dir.toFile().getAbsolutePath());
            return new HibernateDataSourceConfiguration(connectionString, EmbeddedDriver.class, DerbyDialect.class);
        }
        catch (final Exception e) {
            Assumptions.assumeTrue(false);
            return null;
        }
    }

    @ParameterizedTest
    @MethodSource("dialects")
    public void testJpaSave(final DataSourceConfiguration target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        sql.save(new User("Guus"));
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));

        final Set<User> users = sql.findAll();
        Assertions.assertFalse(users.isEmpty());

        for (final User user : users) {
            Assertions.assertNotEquals(0, user.id());
        }
    }

    protected JpaRepository<User, Long> sql(final DataSourceConfiguration target) {
        this.applicationContext.get(DataSourceList.class).add("users", target);
        return this.applicationContext.get(UserJpaRepository.class);
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaDelete(final DataSourceConfiguration target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        final User guus = new User("Guus");
        sql.save(guus);
        sql.save(new User("Simon"));
        sql.save(new User("Josh"));
        sql.delete(guus);

        final Set<User> users = sql.findAll();
        Assertions.assertFalse(users.isEmpty());

        for (final User user : users) {
            Assertions.assertNotEquals("Guus", user.name());
        }
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaPersists(final DataSourceConfiguration target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        final User user = new User("Guus");
        Assertions.assertEquals(0, user.id());

        sql.save(user);
        Assertions.assertNotEquals(0, user.id());
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaUpdate(final DataSourceConfiguration target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        final User guus = new User("Guus");

        sql.save(guus);
        guus.name("NotGuus");
        sql.update(guus);

        final Result<User> persisted = sql.findById(guus.id());
        Assertions.assertTrue(persisted.present());
        Assertions.assertEquals(persisted.get().name(), "NotGuus");
    }

    @Test
    void hibernateRepositoryUsesPropertiesIfNoConnectionExists() throws ApplicationException {
        // TestContainers sometimes closes the connection after a test, so we need to make sure we have an active container
        if (!mySql.isRunning()) mySql.start();

        final JpaRepositoryFactory factory = this.applicationContext.get(JpaRepositoryFactory.class);
        final JpaRepository<User, ?> repository = factory.repository(User.class);
        Assertions.assertTrue(repository instanceof HibernateJpaRepository);

        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);

        // Data API specific
        propertyHolder.set("hartshorn.data.sources.default.username", mySql.getUsername());
        propertyHolder.set("hartshorn.data.sources.default.password", mySql.getPassword());

        final String connectionUrl = "jdbc:mysql://%s:%s/%s".formatted(mySql.getHost(), mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        propertyHolder.set("hartshorn.data.sources.default.url", connectionUrl);

        // Hibernate specific
        propertyHolder.set("hartshorn.data.sources.default.dialect", MySQL8Dialect.class.getCanonicalName());
        propertyHolder.set("hartshorn.data.sources.default.driver", Driver.class.getCanonicalName());

        final DataSourceList dataSourceList = this.applicationContext.get(DataSourceList.class);
        if (dataSourceList instanceof RefreshableDataSourceList refreshable) {
            refreshable.refresh();
        }

        final ComponentPostConstructor componentPostConstructor = applicationContext.get(ComponentPostConstructor.class);
        componentPostConstructor.doPostConstruct(repository);

        final HibernateJpaRepository<User, ?> jpaRepository = (HibernateJpaRepository<User, ?>) repository;
        final Session session = jpaRepository.manager();
        Assertions.assertNotNull(session);

        jpaRepository.close();
    }

    @Test
    void testAutomaticConnectionForUserRepository() {
        // TestContainers sometimes closes the connection after a test, so we need to make sure we have an active container
        if (!mySql.isRunning()) mySql.start();

        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);

        // Data API specific
        propertyHolder.set("hartshorn.data.sources.default.username", mySql.getUsername());
        propertyHolder.set("hartshorn.data.sources.default.password", mySql.getPassword());

        final String connectionUrl = "jdbc:mysql://%s:%s/%s".formatted(mySql.getHost(), mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        propertyHolder.set("hartshorn.data.sources.default.url", connectionUrl);

        // Hibernate specific
        propertyHolder.set("hartshorn.data.sources.default.dialect", MySQL8Dialect.class.getCanonicalName());
        propertyHolder.set("hartshorn.data.sources.default.driver", Driver.class.getCanonicalName());

        final DataSourceList dataSourceList = this.applicationContext.get(DataSourceList.class);
        if (dataSourceList instanceof RefreshableDataSourceList refreshable) {
            refreshable.refresh();
        }

        final UserJpaRepository repository = this.applicationContext.get(UserJpaRepository.class);
        final JpaRepository<?, ?> delegate = this.applicationContext.environment()
                .manager()
                .manager(repository).get()
                .delegate(JpaRepository.class).get();

        Assertions.assertTrue(delegate instanceof EntityManagerJpaRepository);

        final EntityManagerCarrier entityJpaRepository = (EntityManagerJpaRepository<?, ?>) delegate;
        final EntityManager em = entityJpaRepository.manager();
        Assertions.assertNotNull(em);
    }
}
