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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.objects.JpaUser;
import org.dockbox.hartshorn.data.remote.JdbcRemoteConfiguration;
import org.dockbox.hartshorn.data.remote.MySQLRemote;
import org.dockbox.hartshorn.data.remote.PersistenceConnection;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.TransactionRequiredException;

@Testcontainers(disabledWithoutDocker = true)
@UsePersistence
@HartshornTest
public class QueryRepositoryTests {

    @Inject
    private ApplicationContext applicationContext;

    protected static final String DEFAULT_DATABASE = "HartshornDb_" + System.nanoTime();
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(DEFAULT_DATABASE);

    protected static PersistenceConnection connection() {
        final JdbcRemoteConfiguration server = JdbcRemoteConfiguration.of("localhost", mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        return MySQLRemote.INSTANCE.connection(server, mySql.getUsername(), mySql.getPassword());
    }

    @Test
    void testRepositoryDeleteAll() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext.get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 17);
        repository.save(user);
        repository.deleteAll();
        final Set<JpaUser> users = repository.findAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testRepositoryQueries() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext.get(UserQueryRepository.class).connection(connection());
        repository.deleteAll();
        final JpaUser userA = new JpaUser("JUnit", 17);
        final JpaUser userB = new JpaUser("JUnitAdult", 21);
        repository.save(userA);
        repository.save(userB);
        final List<JpaUser> adults = repository.findAdults();
        Assertions.assertEquals(1, adults.size());
        Assertions.assertEquals("JUnitAdult", adults.get(0).name());
    }

    @Test
    void testUpdateNonTransactional() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext.get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(TransactionRequiredException.class, () -> repository.nonTransactionalEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateNonEntityModifier() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext.get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.nonModifierEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateEntity() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext.get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        final int updated = repository.entityUpdate(user.id(), 22);
        Assertions.assertEquals(1, updated);
        Assertions.assertEquals(21, user.age());
        repository.entityManager().refresh(user);
        Assertions.assertEquals(22, user.age());
    }
}
