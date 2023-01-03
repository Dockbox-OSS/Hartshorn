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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.jpa.annotations.NamedQuery;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.jpa.annotations.Query.QueryType;
import org.dockbox.hartshorn.jpa.entitymanager.EntityTypeLookup;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import jakarta.persistence.EntityManager;

public class QueryPostProcessor extends ServiceMethodInterceptorPostProcessor {

    @Override
    protected <T> Collection<MethodView<T, ?>> modifiableMethods(final ComponentProcessingContext<T> processingContext) {
        final ApplicationContext applicationContext = processingContext.applicationContext();
        final Attempt<JpaQueryContextCreator, ComponentResolutionException> attempt = Attempt.of(() -> {
            return applicationContext.get(JpaQueryContextCreator.class);
        }, ComponentResolutionException.class);

        if (attempt.absent()) return Collections.emptyList();

        return processingContext.type().methods().declared().stream()
                .filter(m -> attempt.get().supports(processingContext, m))
                .collect(Collectors.toSet());
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext,
                                     final ComponentProcessingContext<T> processingContext) {
        // Already filtered by #modifiableMethods
        return true;
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context,
                                                  final MethodProxyContext<T> methodContext,
                                                  final ComponentProcessingContext<T> processingContext) {
        final MethodView<T, ?> method = methodContext.method();
        final QueryExecutor function = context.get(QueryExecutor.class);

        final TypeView<?> entityType = this.tryDetermineEntityType(context, method);
        final JpaQueryContextCreator contextCreator = context.get(JpaQueryContextCreator.class);

        return interceptorContext -> {
            final T persistenceCapable = interceptorContext.instance();
            final JpaQueryContext jpaQueryContext = contextCreator.create(context, interceptorContext, entityType, persistenceCapable)
                    .orElseThrow(() -> new IllegalStateException("No JPA query context found for method " + method));

            final EntityManager entityManager = jpaQueryContext.entityManager();
            if (jpaQueryContext.automaticFlush() && entityManager.getTransaction().isActive())
                entityManager.flush();

            final Object result = function.execute(jpaQueryContext);
            return interceptorContext.checkedCast(result);
        };
    }

    @NonNull
    private TypeView<?> tryDetermineEntityType(final ApplicationContext context, final MethodView<?, ?> method) {
        final ElementAnnotationsIntrospector annotations = method.annotations();
        if (annotations.has(NamedQuery.class))
            return this.entityType(context, method, annotations.get(NamedQuery.class).get());
        else if (annotations.has(Query.class))
            return this.entityType(context, method, annotations.get(Query.class).get());
        else
            return this.entityType(context, method, (Class<?>) null);
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context,
                                     final Query query) {
        final TypeView<?> typeView = this.entityType(applicationContext, context, query.entityType());
        if (typeView == null) {
            if (query.type() == QueryType.NATIVE)
                throw new UndeterminedEntityTypeException(context.qualifiedName());
            else return applicationContext.environment().introspect(Void.class);
        }
        return typeView;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context,
                                     final NamedQuery query) {
        final TypeView<?> typeView = this.entityType(applicationContext, context, query.entityType());
        if (typeView == null) return applicationContext.environment().introspect(Void.class);
        return typeView;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context,
                                     final Class<?> entityType) {
        return applicationContext.get(EntityTypeLookup.class).entityType(applicationContext, context, entityType);
    }
}
