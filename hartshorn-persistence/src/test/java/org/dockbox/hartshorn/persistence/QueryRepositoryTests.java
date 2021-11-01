package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.objects.JpaUser;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
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

    protected static ConnectionAttribute connection() {
        return ConnectionAttribute.of(Remotes.MYSQL,
                SQLRemoteServer.of("localhost", mySql.getMappedPort(MySQLContainer.MYSQL_PORT), DEFAULT_DATABASE),
                mySql.getUsername(),
                mySql.getPassword()
        );
    }

    @Test
    void testRepositoryDeleteAll() {
        final UserQueryRepository repository = this.context().get(UserQueryRepository.class, connection());
        final JpaUser user = new JpaUser("JUnit", 17);
        repository.save(user);
        repository.deleteAll();
        final Set<JpaUser> users = repository.findAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testRepositoryQueries() {
        final UserQueryRepository repository = this.context().get(UserQueryRepository.class, connection());
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
        final UserQueryRepository repository = this.context().get(UserQueryRepository.class, connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(TransactionRequiredException.class, () -> repository.nonTransactionalEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateNonEntityModifier() {
        final UserQueryRepository repository = this.context().get(UserQueryRepository.class, connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(IllegalArgumentException.class, () -> repository.nonModifierEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateEntity() {
        final UserQueryRepository repository = this.context().get(UserQueryRepository.class, connection());
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        final int updated = repository.entityUpdate(user.id(), 22);
        Assertions.assertEquals(1, updated);
        Assertions.assertEquals(21, user.age());
        repository.entityManager().refresh(user);
        Assertions.assertEquals(22, user.age());
    }
}
