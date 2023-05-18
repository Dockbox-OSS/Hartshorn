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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.JpaRepositoryFactory;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerJpaRepository;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.MSSQLServerContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import test.org.dockbox.hartshorn.jpa.entity.EntityCollectorLifecycleObserver;
import test.org.dockbox.hartshorn.jpa.entity.User;
import test.org.dockbox.hartshorn.jpa.repository.UserJpaRepository;

@HartshornTest(includeBasePackages = false)
@Testcontainers(disabledWithoutDocker = true)
@UsePersistence
@TestComponents({ UserJpaRepository.class, EntityCollectorLifecycleObserver.class})
@TestInstance(Lifecycle.PER_CLASS)
public abstract class JpaRepositoryTests implements DataSourceConfigurationLoaderTest {

    @Inject
    private ApplicationContext applicationContext;


    // will be shared between test methods
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(JpaTestHarnessProviders.DEFAULT_DATABASE);
    @Container private static final PostgreSQLContainer<?> postgreSql = new PostgreSQLContainer<>(PostgreSQLContainer.IMAGE).withDatabaseName(JpaTestHarnessProviders.DEFAULT_DATABASE);
    @Container private static final MariaDBContainer<?> mariaDb = new MariaDBContainer<>(MariaDBContainer.NAME).withDatabaseName(JpaTestHarnessProviders.DEFAULT_DATABASE);
    @Container private static final MSSQLServerContainer<?> mssqlServer = new MSSQLServerContainer<>(MSSQLServerContainer.IMAGE).acceptLicense();

    public static Stream<Arguments> containers() {
        return Stream.of(
                Arguments.of(mySql),
                Arguments.of(postgreSql),
                Arguments.of(mariaDb),
                Arguments.of(mssqlServer)
        );
    }

    public Stream<Arguments> dialects() {
        final DataSourceConfigurationLoader configurationLoader = this.configurationLoader();
        return Stream.of(
                Arguments.of(configurationLoader.mysql(mySql)),
                Arguments.of(configurationLoader.postgresql(postgreSql)),
                Arguments.of(configurationLoader.mariadb(mariaDb)),
                Arguments.of(configurationLoader.mssql(mssqlServer))
        );
    }

    @ParameterizedTest
    @MethodSource("containers")
    void testContainerStates(final ContainerState container) {
        Assertions.assertTrue(container.isRunning());
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

        final Option<User> persisted = sql.findById(guus.id());
        Assertions.assertTrue(persisted.present());
        Assertions.assertEquals(persisted.get().name(), "NotGuus");
    }

    @Test
    void repositoryUsesPropertiesIfNoConnectionExists() throws ApplicationException, IOException {
        // TestContainers sometimes closes the connection after a test, so we need to make sure we have an active container
        if (!mySql.isRunning()) mySql.start();

        final JpaRepositoryFactory factory = this.applicationContext.get(JpaRepositoryFactory.class);
        final JpaRepository<User, ?> repository = factory.repository(User.class);
        Assertions.assertTrue(repository instanceof EntityManagerCarrier);

        this.applicationContext.get(LazyJdbcRepositoryConfigurationInitializer.class).initialize(mySql, MySQLContainer.MYSQL_PORT);

        final ComponentPostConstructor componentPostConstructor = this.applicationContext.get(ComponentPostConstructor.class);
        componentPostConstructor.doPostConstruct(repository);

        final EntityManagerCarrier jpaRepository = (EntityManagerCarrier) repository;
        final EntityManager entityManager = jpaRepository.manager();
        Assertions.assertNotNull(entityManager);

        jpaRepository.close();
    }

    @Test
    void testAutomaticConnectionForUserRepository() {
        // TestContainers sometimes closes the connection after a test, so we need to make sure we have an active container
        if (!mySql.isRunning()) mySql.start();

        this.applicationContext.get(LazyJdbcRepositoryConfigurationInitializer.class).initialize(mySql, MySQLContainer.MYSQL_PORT);

        final UserJpaRepository repository = this.applicationContext.get(UserJpaRepository.class);
        final JpaRepository<?, ?> delegate = this.applicationContext.environment()
                .manager(repository).get()
                .delegate(JpaRepository.class).get();

        Assertions.assertTrue(delegate instanceof EntityManagerJpaRepository);

        final EntityManagerCarrier entityJpaRepository = (EntityManagerCarrier) delegate;
        final EntityManager em = entityJpaRepository.manager();
        Assertions.assertNotNull(em);
    }
}
