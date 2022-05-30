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

package org.dockbox.hartshorn.data.jpa;

import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.context.QueryContext;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.util.Result;

import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class EntityQueryFunction implements QueryFunction {

    @Override
    public Object execute(final QueryContext context) {
        return this.executeQuery(context, session -> {
            final Query query = context.query(session);
            return this.processQueryResult(context, query);
        });
    }

    private Object processQueryResult(final QueryContext context, final Query query) {
        if (context.modifiesEntity()) return query.executeUpdate();
        else return query.getResultList();
    }

    private Object executeQuery(final QueryContext context, final Function<EntityManager, Object> action) {
        EntityManager entityManager = null;

        // TODO: Move this to a common method (also in TransactionalProxyCallbackPostProcessor)
        if (context.repository() instanceof EntityManagerJpaRepository jpaRepository) {
            entityManager = jpaRepository.manager();
        }
        else if (context.repository() instanceof Proxy<?> proxy) {
            final Result<JpaRepository> repository = proxy.manager().delegate(JpaRepository.class);
            if (repository.present() && repository.get() instanceof EntityManagerJpaRepository jpaRepository) {
                entityManager = jpaRepository.manager();
            }
        }
        if (entityManager != null) {
            try (final EntityManager manager = entityManager) {
                final Object result = action.apply(manager);
                if (context.automaticClear()) manager.clear();
                return result;
            }
        }
        throw new UnsupportedOperationException("Unsupported repository type: " + context.repository().getClass());
    }
}
