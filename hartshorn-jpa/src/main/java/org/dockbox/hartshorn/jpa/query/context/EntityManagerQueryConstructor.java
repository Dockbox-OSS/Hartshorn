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

package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class EntityManagerQueryConstructor implements QueryConstructor {

    @Inject
    private QueryExecuteTypeLookup queryExecuteTypeLookup;
    private final EntityManager entityManager;

    @Bound
    public EntityManagerQueryConstructor(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Query createJpqlQuery(final String jpql, final AbstractJpaQueryContext context) {
        return this.entityManager.createQuery(jpql);
    }

    @Override
    public Query createNativeQuery(final String nativeQuery, final AbstractJpaQueryContext context) {
        return this.entityManager.createNativeQuery(nativeQuery);
    }

    @Override
    public Query createNamedQuery(final String name, final AbstractJpaQueryContext context) {
        return this.entityManager.createNamedQuery(name);
    }
}
