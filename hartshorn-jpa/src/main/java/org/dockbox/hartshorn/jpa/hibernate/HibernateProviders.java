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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.condition.RequiresClass;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.query.EntityQueryExecutor;
import org.dockbox.hartshorn.jpa.query.NamedQueryRegistry;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.jpa.query.QueryExecutor;
import org.dockbox.hartshorn.jpa.query.QueryResultTransformer;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.jpa.transaction.TransactionManager;
import org.dockbox.hartshorn.util.TypeUtils;

import jakarta.inject.Singleton;

@Service
@RequiresActivator(UsePersistence.class)
@RequiresClass("org.hibernate.Hibernate")
public class HibernateProviders {

    @Provider(priority = 0)
    public Class<? extends DataSourceList> dataSourceList() {
        return HibernateDataSourceList.class;
    }

    @Provider
    public Class<? extends JpaRepository<?, ?>> jpaRepository() {
        return TypeUtils.adjustWildcards(HibernateJpaRepository.class, Class.class);
    }

    @Provider
    public Class<? extends EntityManagerCarrier> entityManagerCarrier() {
        return HibernateEntityManagerCarrier.class;
    }

    @Provider
    public Class<? extends TransactionManager> transactionManager() {
        return HibernateTransactionManager.class;
    }

    @Provider
    public Class<? extends NamedQueryRegistry> namedQueryRegistry() {
        return HibernateNamedQueryRegistry.class;
    }

    @Provider
    public QueryExecutor queryFunction(final QueryResultTransformer queryResultTransformer) {
        return new EntityQueryExecutor(queryResultTransformer);
    }

    @Provider
    @Singleton
    public QueryExecuteTypeLookup queryTypeLookup() {
        return new HibernateQueryExecuteTypeLookup();
    }

    @Provider
    @Singleton
    public QueryResultTransformer queryResultTransformer() {
        return new HibernateQueryResultTransformer();
    }
}
