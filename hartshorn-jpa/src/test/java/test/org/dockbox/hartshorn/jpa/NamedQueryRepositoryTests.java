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

package test.org.dockbox.hartshorn.jpa;

import com.mysql.cj.jdbc.Driver;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.annotations.UseConfigurations;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.hibernate.dialect.MySQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.inject.Inject;

@Testcontainers(disabledWithoutDocker = true)
@UsePersistence
@UseConfigurations
@HartshornTest(includeBasePackages = false)
@TestComponents({ UserNamedQueryRepository.class, PersistentTestComponentsService.class})
public class NamedQueryRepositoryTests {

    @Inject
    private ApplicationContext applicationContext;
    @Container private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(SqlServiceTest.DEFAULT_DATABASE);

    protected static DataSourceConfiguration connection() {
        return SqlServiceTest.jdbc("mysql", Driver.class, mySql, MySQLDialect.class, MySQLContainer.MYSQL_PORT);
    }

    @Test
    void testRepositoryAttachesEntityLevelQuery() {
        this.applicationContext.get(DataSourceList.class).add("users", connection());

        final UserNamedQueryRepository repository = this.applicationContext.get(UserNamedQueryRepository.class);
        final UserWithNamedQuery waldo = new UserWithNamedQuery("Waldo");
        repository.save(waldo);

        final UserWithNamedQuery maybeWaldo = repository.findWaldo();
        Assertions.assertNotNull(maybeWaldo);
        Assertions.assertEquals(waldo, maybeWaldo);
    }
}
