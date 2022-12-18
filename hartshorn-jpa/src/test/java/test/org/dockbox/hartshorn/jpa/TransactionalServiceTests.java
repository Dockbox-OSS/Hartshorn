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
import org.dockbox.hartshorn.jpa.annotations.UseTransactionManagement;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerLookup;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.option.Option;
import org.hibernate.dialect.MySQLDialect;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;

@HartshornTest(includeBasePackages = false)
@UseConfigurations
@UseTransactionManagement
@Testcontainers(disabledWithoutDocker = true)
public class TransactionalServiceTests {

    @Container
    private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(SqlServiceTest.DEFAULT_DATABASE);

    @InjectTest
    @TestComponents(TransactionalService.class)
    public void testTransactionalService(final ApplicationContext applicationContext) {
        final DataSourceConfiguration configuration = SqlServiceTest.jdbc("mysql", Driver.class, mySql, MySQLDialect.class, MySQLContainer.MYSQL_PORT);
        applicationContext.get(DataSourceList.class).add("users", configuration);

        final TransactionalService service = applicationContext.get(TransactionalService.class);

        // Ensure appropriate context is available
        final EntityManagerLookup entityManagerLookup = applicationContext.get(EntityManagerLookup.class);
        final Option<EntityManager> entityManager = entityManagerLookup.lookup(service);
        Assertions.assertTrue(entityManager.present());

        final EntityManager manager = entityManager.get();

        // Should not be active before transactional method is called
        Assertions.assertFalse(manager.getTransaction().isActive());
        // Should be activated when transactional method is called
        Assertions.assertTrue(service.isTransactional(manager));
        // Should close the transaction after transactional method is called
        Assertions.assertFalse(manager.getTransaction().isActive());
    }

}
