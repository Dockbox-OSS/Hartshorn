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

package org.dockbox.hartshorn.data;

import com.mysql.cj.jdbc.Driver;

import org.dockbox.hartshorn.core.Enableable;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.hibernate.HibernateJpaRepository;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.data.remote.DerbyFileRemote;
import org.dockbox.hartshorn.data.remote.JdbcRemoteConfiguration;
import org.dockbox.hartshorn.data.remote.MariaDbRemote;
import org.dockbox.hartshorn.data.remote.SqlServerRemote;
import org.dockbox.hartshorn.data.remote.MySQLRemote;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.data.remote.PostgreSQLRemote;
import org.dockbox.hartshorn.data.remote.Remote;
import org.dockbox.hartshorn.data.service.JpaRepositoryFactory;
import org.hibernate.Session;
import org.hibernate.dialect.MySQL8Dialect;
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

import javax.inject.Inject;
import javax.persistence.EntityManager;

import lombok.Getter;

@Testcontainers
@UsePersistence
class SqlServiceTest {

    @Inject
    @Getter
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
                Arguments.of(directory(DerbyFileRemote.INSTANCE, "derby")),
                Arguments.of(connection(MySQLRemote.INSTANCE, mySql, MySQLContainer.MYSQL_PORT)),
                Arguments.of(connection(PostgreSQLRemote.INSTANCE, postgreSql, PostgreSQLContainer.POSTGRESQL_PORT)),
                Arguments.of(connection(MariaDbRemote.INSTANCE, mariaDb, 3306)),
                Arguments.of(connection(SqlServerRemote.INSTANCE, mssqlServer, MSSQLServerContainer.MS_SQL_SERVER_PORT))
        );
    }

    protected static PersistenceConnection connection(final Remote<JdbcRemoteConfiguration> remote, final JdbcDatabaseContainer<?> container, final int defaultPort) {
        final JdbcRemoteConfiguration server = JdbcRemoteConfiguration.of("localhost", container.getMappedPort(defaultPort), DEFAULT_DATABASE);
        return remote.connection(server, container.getUsername(), container.getPassword());
    }

    protected static PersistenceConnection directory(final Remote<Path> remote, final String prefix) {
        try {
            final Path dir = Files.createTempDirectory(prefix);
            return remote.connection(dir, "", "");
        }
        catch (final Exception e) {
            Assumptions.assumeTrue(false);
            return null;
        }
    }

    @ParameterizedTest
    @MethodSource("dialects")
    public void testJpaSave(final PersistenceConnection target) {
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

    protected JpaRepository<User, Long> sql(final PersistenceConnection target) {
        return this.applicationContext().get(UserJpaRepository.class).connection(target);
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaDelete(final PersistenceConnection target) {
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
    void testJpaPersists(final PersistenceConnection target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        final User user = new User("Guus");
        Assertions.assertEquals(0, user.id());

        sql.save(user);
        Assertions.assertNotEquals(0, user.id());
    }

    @ParameterizedTest
    @MethodSource("dialects")
    void testJpaUpdate(final PersistenceConnection target) {
        final JpaRepository<User, Long> sql = this.sql(target);
        final User guus = new User("Guus");

        sql.save(guus);
        guus.name("NotGuus");
        sql.update(guus);

        final Exceptional<User> persisted = sql.findById(guus.id());
        Assertions.assertTrue(persisted.present());
        Assertions.assertEquals(persisted.get().name(), "NotGuus");
    }

    @Test
    void hibernateRepositoryUsesPropertiesIfNoConnectionExists() throws ApplicationException {
        JpaRepositoryFactory factory = this.applicationContext().get(JpaRepositoryFactory.class);
        JpaRepository<User, ?> repository = factory.repository(User.class);
        Assertions.assertTrue(repository instanceof HibernateJpaRepository);

        // Data API specific
        this.applicationContext().property("hartshorn.data.username", mySql.getUsername());
        this.applicationContext().property("hartshorn.data.password", mySql.getPassword());

        String connectionUrl = "jdbc:mysql://%s:%s/%s".formatted(mySql.getHost(), mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        this.applicationContext().property("hartshorn.data.url", connectionUrl);

        // Hibernate specific
        this.applicationContext().property("hartshorn.data.hibernate.dialect", MySQL8Dialect.class.getCanonicalName());
        this.applicationContext().property("hartshorn.data.hibernate.driver_class", Driver.class.getCanonicalName());

        ((Enableable) repository).enable();

        Session session = ((HibernateJpaRepository<User, ?>) repository).session();
        Assertions.assertNotNull(session);

        ((HibernateJpaRepository<User, ?>) repository).close();
    }

    @Test
    void testAutomaticConnectionForUserRepository() {
        // Data API specific
        this.applicationContext().property("hartshorn.data.username", mySql.getUsername());
        this.applicationContext().property("hartshorn.data.password", mySql.getPassword());

        String connectionUrl = "jdbc:mysql://%s:%s/%s".formatted(mySql.getHost(), mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        this.applicationContext().property("hartshorn.data.url", connectionUrl);

        // Hibernate specific
        this.applicationContext().property("hartshorn.data.hibernate.dialect", MySQL8Dialect.class.getCanonicalName());
        this.applicationContext().property("hartshorn.data.hibernate.driver_class", Driver.class.getCanonicalName());

        UserJpaRepository repository = this.applicationContext().get(UserJpaRepository.class);

        EntityManager em = repository.entityManager();
        Assertions.assertNotNull(em);
    }
}
