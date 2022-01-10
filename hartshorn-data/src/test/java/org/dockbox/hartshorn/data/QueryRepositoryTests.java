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

import org.dockbox.hartshorn.core.context.ApplicationContext;
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

import lombok.Getter;

@Testcontainers
@UsePersistence
@HartshornTest
public class QueryRepositoryTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    protected static final String DEFAULT_DATABASE = "HartshornDb_" + System.nanoTime();
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(DEFAULT_DATABASE);

    protected static PersistenceConnection connection() {
        final JdbcRemoteConfiguration server = JdbcRemoteConfiguration.of("localhost", mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        return MySQLRemote.INSTANCE.connection(server, mySql.getUsername(), mySql.getPassword());
    }

    @Test
    void testRepositoryDeleteAll() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 17);
        repository.save(user);
        repository.deleteAll();
        final Set<JpaUser> users = repository.findAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testRepositoryQueries() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext().get(UserQueryRepository.class).connection(connection());
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
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(TransactionRequiredException.class, () -> repository.nonTransactionalEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateNonEntityModifier() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.nonModifierEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateEntity() {
        final UserQueryRepository repository = (UserQueryRepository) this.applicationContext().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        final int updated = repository.entityUpdate(user.id(), 22);
        Assertions.assertEquals(1, updated);
        Assertions.assertEquals(21, user.age());
        repository.entityManager().refresh(user);
        Assertions.assertEquals(22, user.age());
    }
}
