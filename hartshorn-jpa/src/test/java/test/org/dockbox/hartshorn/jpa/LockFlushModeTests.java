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
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.query.QueryExecutionContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.unnamed.UnnamedJpaQueryContextCreator;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.hibernate.dialect.MySQLDialect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.inject.Inject;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;

@HartshornTest(includeBasePackages = false)
@TestComponents({
        PersistentTestComponentsService.class,
        LockFlushModeUserJpaRepository.class,
})
@UsePersistence
@Testcontainers(disabledWithoutDocker = true)
public class LockFlushModeTests {

    @Container
    private static final MySQLContainer<?> mySql = new MySQLContainer<>(MySQLContainer.NAME).withDatabaseName(SqlServiceTest.DEFAULT_DATABASE);

    @Inject
    private UnnamedJpaQueryContextCreator contextCreator;

    @Inject
    private ApplicationContext applicationContext;

    @BeforeEach
    void prepareDataSource() {
        final DataSourceConfiguration configuration = SqlServiceTest.jdbc("mysql", Driver.class, mySql, MySQLDialect.class, MySQLContainer.MYSQL_PORT);
        this.applicationContext.get(DataSourceList.class).add("users", configuration);
    }

    @Test
    void testQueryExecutionContextIsConfigured() {
        final LockFlushModeUserJpaRepository repository = this.applicationContext.get(LockFlushModeUserJpaRepository.class);
        final ProxyManager<LockFlushModeUserJpaRepository> manager = this.applicationContext.environment().manager(repository).get();
        final Option<QueryExecutionContext> executionContextOption = manager.first(QueryExecutionContext.class);

        Assertions.assertTrue(executionContextOption.present());

        final QueryExecutionContext executionContext = executionContextOption.get();
        final TypeView<LockFlushModeUserJpaRepository> repositoryView = this.applicationContext.environment().introspect(repository);

        final MethodView<LockFlushModeUserJpaRepository, ?> findWaldoWithLockMode = repositoryView.methods().named("findWaldoWithLockMode").get();
        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, executionContext.lockMode(findWaldoWithLockMode));

        final MethodView<LockFlushModeUserJpaRepository, ?> findWaldoWithFlushMode = repositoryView.methods().named("findWaldoWithFlushMode").get();
        Assertions.assertEquals(FlushModeType.COMMIT, executionContext.flushMode(findWaldoWithFlushMode));
    }

    @Test
    void testLockModeIsPresentInPersistenceCapable() {
        final Query query = this.createNonExecutedQuery("findWaldoWithLockMode");
        final LockModeType lockMode = query.getLockMode();

        Assertions.assertEquals(LockModeType.PESSIMISTIC_WRITE, lockMode);
    }

    @Test
    void testFlushModeIsPresentInPersistenceCapable() {
        final Query query = this.createNonExecutedQuery("findWaldoWithFlushMode");
        final FlushModeType flushMode = query.getFlushMode();

        Assertions.assertEquals(FlushModeType.COMMIT, flushMode);
    }

    private Query createNonExecutedQuery(final String methodName) {
        final LockFlushModeUserJpaRepository repository = this.applicationContext.get(LockFlushModeUserJpaRepository.class);
        final TypeView<LockFlushModeUserJpaRepository> repositoryView = this.applicationContext.environment().introspect(repository);
        final MethodView<LockFlushModeUserJpaRepository, ?> methodView = repositoryView.methods().named(methodName).get();
        final MethodInterceptorContext<LockFlushModeUserJpaRepository, ?> interceptorContext = new MethodInterceptorContext<>(methodView, new Object[0], repository, null, null, null);

        final TypeView<User> entityType = this.applicationContext.environment().introspect(User.class);
        final JpaQueryContext queryContext = this.contextCreator.create(this.applicationContext, interceptorContext, entityType, repository).get();

        return queryContext.query();
    }
}
