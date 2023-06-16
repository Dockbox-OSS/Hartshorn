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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.jpa.JpaRepositoryFactory;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.annotations.UseTransactionManagement;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerFactory;
import org.dockbox.hartshorn.jpa.query.EntityQueryExecutor;
import org.dockbox.hartshorn.jpa.query.QueryComponentFactory;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.jpa.query.QueryExecutor;
import org.dockbox.hartshorn.jpa.query.QueryResultTransformer;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.jpa.transaction.TransactionFactory;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UsePersistence.class)
@RequiresClass("org.hibernate.Hibernate")
public class HibernateProviders {

    @Binds(priority = 0)
    public DataSourceList dataSourceList(final ApplicationContext applicationContext,
                                         final HibernateDataSourceConfigurationObject sources) {
        return new HibernateDataSourceList(applicationContext, sources);
    }

    @Binds
    public JpaRepositoryFactory repositoryFactory() {
        return new HibernateJpaRepositoryFactory();
    }

    @Binds
    public EntityManagerFactory entityManagerFactory() {
        return new HibernateEntityManagerFactory();
    }

    @Binds
    public QueryComponentFactory queryComponentFactory() {
        return new HibernateQueryComponentFactory();
    }

    @Binds
    public QueryExecutor queryFunction(final QueryResultTransformer queryResultTransformer) {
        return new EntityQueryExecutor(queryResultTransformer);
    }

    @Binds
    @Singleton
    public QueryExecuteTypeLookup queryTypeLookup() {
        return new HibernateQueryExecuteTypeLookup();
    }

    @Binds
    @Singleton
    public QueryResultTransformer queryResultTransformer() {
        return new HibernateQueryResultTransformer();
    }

    @Binds
    @RequiresActivator(UseTransactionManagement.class)
    public TransactionFactory transactionManager() {
        return new HibernateTransactionFactory();
    }
}
