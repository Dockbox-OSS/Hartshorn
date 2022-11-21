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

package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerLookup;
import org.dockbox.hartshorn.util.option.Option;

import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class EntityQueryFunction implements QueryFunction {

    @Override
    public Object execute(final AbstractQueryContext<?> context) {
        return this.executeQuery(context, session -> {
            final Query query = context.query(session);
            return this.processQueryResult(context, query);
        });
    }

    private Object processQueryResult(final AbstractQueryContext<?> context, final Query query) {
        if (context.entityType().isVoid()) return query.executeUpdate();
        else return query.getResultList();
    }

    private Object executeQuery(final AbstractQueryContext<?> context, final Function<EntityManager, Object> action) {
        final Option<EntityManager> entityManager = context.applicationContext().get(EntityManagerLookup.class)
                .lookup(context.repository());

        if (entityManager.present()) {
            try (final EntityManager manager = entityManager.get()) {
                final Object result = action.apply(manager);
                if (context.automaticClear()) manager.clear();
                return result;
            }
        }
        throw new UnsupportedOperationException("Unsupported repository type: " + context.repository().getClass());
    }
}
