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

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.context.QueryContext;
import org.hibernate.Session;

import java.util.function.Function;

import javax.persistence.Query;

@ComponentBinding(QueryFunction.class)
public class HibernateQueryFunction implements QueryFunction {

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

    private Object executeQuery(final QueryContext context, final Function<Session, Object> action) {
        try (final Session session = (Session) context.repository().entityManager()) {
            final Object result = action.apply(session);
            if (context.automaticClear()) session.clear();
            return result;
        }
    }
}
