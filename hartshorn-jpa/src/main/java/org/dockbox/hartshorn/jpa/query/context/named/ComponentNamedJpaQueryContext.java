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

package org.dockbox.hartshorn.jpa.query.context.named;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.jpa.query.context.AbstractJpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.application.ComponentNamedQueryContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class ComponentNamedJpaQueryContext extends AbstractJpaQueryContext {

    private final ComponentNamedQueryContext context;

    public ComponentNamedJpaQueryContext(final ComponentNamedQueryContext context,
                                         final Object[] args,
                                         final MethodView<?, ?> method,
                                         final TypeView<?> entityType,
                                         final ApplicationContext applicationContext,
                                         final Object persistenceCapable) {
        super(args, method, entityType, applicationContext, persistenceCapable);
        this.context = context;
    }

    @Override
    protected Query persistenceQuery(final QueryConstructor queryConstructor, final EntityManager entityManager) throws IllegalArgumentException {
        return queryConstructor.createNamedQuery(this.context.name(), this);
    }

    @Override
    public boolean automaticClear() {
        return this.context.automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.context.automaticFlush();
    }

    @Override
    public QueryExecuteType queryType() {
        final QueryExecuteTypeLookup queryExecuteTypeLookup = this.applicationContext().get(QueryExecuteTypeLookup.class);
        return queryExecuteTypeLookup.lookup(this.query());
    }
}
