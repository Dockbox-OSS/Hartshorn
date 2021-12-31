/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.context.QueryContext;
import org.hibernate.Session;

import java.util.function.Function;

import javax.persistence.Query;

@Binds(QueryFunction.class)
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
