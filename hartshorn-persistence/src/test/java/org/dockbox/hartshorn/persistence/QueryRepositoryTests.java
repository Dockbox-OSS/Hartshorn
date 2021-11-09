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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.objects.JpaUser;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.dockbox.hartshorn.persistence.properties.SQLRemoteServer;
import org.dockbox.hartshorn.testsuite.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import javax.persistence.TransactionRequiredException;

@Testcontainers
@UsePersistence
public class QueryRepositoryTests extends ApplicationAwareTest {

    protected static final String DEFAULT_DATABASE = "HartshornDb_" + System.nanoTime();
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(DEFAULT_DATABASE);

    protected static PersistenceConnection connection() {
        SQLRemoteServer server = SQLRemoteServer.of("localhost", mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE);
        return new PersistenceConnection(Remotes.MYSQL.url(server), mySql.getUsername(), mySql.getPassword(), Remotes.MYSQL);
    }

    @Test
    void testRepositoryDeleteAll() {
        final UserQueryRepository repository = (UserQueryRepository) this.context().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 17);
        repository.save(user);
        repository.deleteAll();
        final Set<JpaUser> users = repository.findAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testRepositoryQueries() {
        final UserQueryRepository repository = (UserQueryRepository) this.context().get(UserQueryRepository.class).connection(connection());
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
        final UserQueryRepository repository = (UserQueryRepository) this.context().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(TransactionRequiredException.class, () -> repository.nonTransactionalEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateNonEntityModifier() {
        final UserQueryRepository repository = (UserQueryRepository) this.context().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.nonModifierEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateEntity() {
        final UserQueryRepository repository = (UserQueryRepository) this.context().get(UserQueryRepository.class).connection(connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        final int updated = repository.entityUpdate(user.id(), 22);
        Assertions.assertEquals(1, updated);
        Assertions.assertEquals(21, user.age());
        repository.entityManager().refresh(user);
        Assertions.assertEquals(22, user.age());
    }
}
