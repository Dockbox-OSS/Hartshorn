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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.jpa.JpaParameterLoaderContext;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;

import jakarta.persistence.EntityManager;

public abstract class AbstractQueryContext<A> extends DefaultContext {

    private ParameterLoader<JpaParameterLoaderContext> parameterLoader;

    private final A annotation;
    private final Object[] args;
    private final MethodView<?, ?> method;
    private final TypeView<?> entityType;

    private final ApplicationContext applicationContext;
    private final JpaRepository<?, ?> repository;

    public AbstractQueryContext(
            final A annotation, final Object[] args, final MethodView<?, ?> method,
            final TypeView<?> entityType, final ApplicationContext applicationContext,
            final JpaRepository<?, ?> repository) {
        this.annotation = annotation;
        this.args = args;
        this.method = method;
        this.entityType = entityType;
        this.applicationContext = applicationContext;
        this.repository = repository;
    }

    public A annotation() {
        return this.annotation;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    public JpaRepository<?, ?> repository() {
        return this.repository;
    }

    public MethodView<?, ?> method() {
        return this.method;
    }

    public TypeView<?> entityType() {
        return this.entityType;
    }

    public abstract boolean automaticClear();

    public abstract boolean automaticFlush();

    public jakarta.persistence.Query query(final EntityManager entityManager) {
        final jakarta.persistence.Query persistenceQuery = this.persistenceQuery(entityManager, this.annotation);
        final JpaParameterLoaderContext loaderContext = new JpaParameterLoaderContext(this.method, this.entityType, null, this.applicationContext, persistenceQuery);
        this.parameterLoader().loadArguments(loaderContext, this.args);
        return persistenceQuery;
    }

    protected abstract jakarta.persistence.Query persistenceQuery(final EntityManager entityManager, final A query) throws IllegalArgumentException;

    protected ParameterLoader<JpaParameterLoaderContext> parameterLoader() {
        if (this.parameterLoader == null) {
            this.parameterLoader = TypeUtils.adjustWildcards(this.applicationContext().get(Key.of(ParameterLoader.class, "jpa_query")), ParameterLoader.class);
        }
        return this.parameterLoader;
    }
}
