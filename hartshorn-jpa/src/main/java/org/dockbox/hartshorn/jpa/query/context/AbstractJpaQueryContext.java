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

package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.jpa.JpaParameterLoaderContext;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerLookup;
import org.dockbox.hartshorn.jpa.query.QueryComponentFactory;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecutionContext;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.Query;

public abstract class AbstractJpaQueryContext extends DefaultContext implements JpaQueryContext {

    private ParameterLoader<JpaParameterLoaderContext> parameterLoader;

    private final Object[] args;
    private final MethodView<?, ?> method;
    private final TypeView<?> entityType;

    private final ApplicationContext applicationContext;
    private final Object persistenceCapable;

    public AbstractJpaQueryContext(
            final Object[] args, final MethodView<?, ?> method,
            final TypeView<?> entityType, final ApplicationContext applicationContext,
            final Object persistenceCapable) {
        this.args = args;
        this.method = method;
        this.entityType = entityType;
        this.applicationContext = applicationContext;
        this.persistenceCapable = persistenceCapable;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public Object persistenceCapable() {
        return this.persistenceCapable;
    }

    @Override
    public MethodView<?, ?> method() {
        return this.method;
    }

    @Override
    public TypeView<?> entityType() {
        return this.entityType;
    }

    @Override
    public EntityManager entityManager() {
        return this.applicationContext().get(EntityManagerLookup.class)
                .lookup(this.persistenceCapable())
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported persistence capable type: " + this.persistenceCapable().getClass()));
    }

    @Override
    public jakarta.persistence.Query query() {
        final EntityManager entityManager = this.entityManager();
        final QueryConstructor queryConstructor = this.queryConstructor(entityManager);
        final jakarta.persistence.Query persistenceQuery = this.persistenceQuery(queryConstructor, entityManager);

        this.modifyQueryFromContext(persistenceQuery);

        // Process parameters to modify query
        final JpaParameterLoaderContext loaderContext = new JpaParameterLoaderContext(this.method, this.entityType, null, this.applicationContext, persistenceQuery);
        this.parameterLoader().loadArguments(loaderContext, this.args);

        return persistenceQuery;
    }

    protected void modifyQueryFromContext(final Query persistenceQuery) {
        final ApplicationEnvironment environment = this.applicationContext().environment();
        if (environment.isProxy(this.persistenceCapable)) {
            environment.manager(this.persistenceCapable).peek(manager -> {
                manager.first(QueryExecutionContext.class).peek(queryExecutionContext -> {
                    final LockModeType lockMode = queryExecutionContext.lockMode(this.method);
                    if (lockMode != null) persistenceQuery.setLockMode(lockMode);

                    final FlushModeType flushMode = queryExecutionContext.flushMode(this.method);
                    if (flushMode != null) persistenceQuery.setFlushMode(flushMode);
                });
            });
        }
    }

    protected abstract jakarta.persistence.Query persistenceQuery(final QueryConstructor queryConstructor, final EntityManager entityManager) throws IllegalArgumentException;

    @Override
    public TypeView<?> queryResultType() {
        return this.method().genericReturnType();
    }

    protected ParameterLoader<JpaParameterLoaderContext> parameterLoader() {
        if (this.parameterLoader == null) {
            this.parameterLoader = TypeUtils.adjustWildcards(this.applicationContext().get(ComponentKey.of(ParameterLoader.class, "jpa_query")), ParameterLoader.class);
        }
        return this.parameterLoader;
    }

    private QueryConstructor queryConstructor(final EntityManager entityManager) {
        return this.applicationContext().get(QueryComponentFactory.class).constructor(entityManager);
    }
}
