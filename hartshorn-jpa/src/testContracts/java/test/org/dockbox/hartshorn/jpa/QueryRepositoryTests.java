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
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;
import jakarta.persistence.TransactionRequiredException;
import test.org.dockbox.hartshorn.jpa.entity.JpaUser;
import test.org.dockbox.hartshorn.jpa.entity.EntityCollectorLifecycleObserver;
import test.org.dockbox.hartshorn.jpa.repository.UserQueryRepository;

@Testcontainers(disabledWithoutDocker = true)
@UsePersistence
@HartshornTest(includeBasePackages = false)
@TestComponents({ UserQueryRepository.class, EntityCollectorLifecycleObserver.class})
public class QueryRepositoryTests {

    @Inject
    private ApplicationContext applicationContext;
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME)
            .withDatabaseName(JpaTestContractProviders.DEFAULT_DATABASE);

    protected DataSourceConfiguration connection() {
        return this.applicationContext.get(DataSourceConfigurationLoader.class).mysql(mySql);
    }

    @Test
    void testRepositoryDeleteAll() {
        this.applicationContext.get(DataSourceList.class).add("users", this.connection());
        final UserQueryRepository repository = this.applicationContext.get(UserQueryRepository.class);
        final JpaUser user = new JpaUser("JUnit", 17);
        repository.save(user);
        repository.deleteAll();
        final Set<JpaUser> users = repository.findAll();
        Assertions.assertTrue(users.isEmpty());
    }

    @Test
    void testRepositoryQueries() {
        this.applicationContext.get(DataSourceList.class).add("users", this.connection());
        final UserQueryRepository repository = this.applicationContext.get(UserQueryRepository.class);
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
        this.applicationContext.get(DataSourceList.class).add("users", this.connection());
        final UserQueryRepository repository = this.applicationContext.get(UserQueryRepository.class);
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        Assertions.assertThrows(TransactionRequiredException.class, () -> repository.nonTransactionalEntityUpdate(user.id(), 22));
    }

    @Test
    void testUpdateEntity() {
        this.applicationContext.get(DataSourceList.class).add("users", this.connection());
        final UserQueryRepository repository = this.applicationContext.get(UserQueryRepository.class);
        final JpaUser user = new JpaUser("JUnit", 21);
        repository.save(user);
        final int updated = repository.entityUpdate(user.id(), 22);
        Assertions.assertEquals(1, updated);
        Assertions.assertEquals(21, user.age());

        repository.refresh(user);
        Assertions.assertEquals(22, user.age());

        final Option<JpaUser> userLookup = repository.findById(user.id());
        Assertions.assertTrue(userLookup.present());
        Assertions.assertEquals(22, userLookup.get().age());
    }
}
