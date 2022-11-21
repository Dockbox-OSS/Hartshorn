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
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.NamedQuery;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.jpa.annotations.Query.QueryType;
import org.dockbox.hartshorn.jpa.annotations.Transactional;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class QueryPostProcessor extends ServiceMethodInterceptorPostProcessor {

    @Override
    protected <T> Collection<MethodView<T, ?>> modifiableMethods(final TypeView<T> type) {
        return type.methods().all().stream()
                .filter(m -> m.annotations().hasAny(Query.class, NamedQuery.class))
                .collect(Collectors.toSet());
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final MethodView<T, ?> method = methodContext.method();
        final TypeView<T> parent = method.declaredBy();
        if (parent.isChildOf(JpaRepository.class)) {
            final boolean hasQuery = method.annotations().has(Query.class);
            return hasQuery || method.annotations().has(NamedQuery.class);
        }
        return false;
    }

    @Override
    public <T, R> MethodInterceptor<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final MethodView<T, ?> method = methodContext.method();
        final QueryFunction function = context.get(QueryFunction.class);

        final ElementAnnotationsIntrospector annotations = method.annotations();
        final boolean transactional = annotations.has(Transactional.class);

        final BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>,
                AbstractQueryContext<?>> queryContextFunction = this.createQueryFunction(context, method, annotations);

        return interceptorContext -> {
            final JpaRepository<?, ?> repository = (JpaRepository<?, ?>) interceptorContext.instance();
            final AbstractQueryContext<?> queryContext = queryContextFunction.apply(interceptorContext, repository);

            if (queryContext.automaticFlush() && !transactional) repository.flush();
            final Object result = function.execute(queryContext);
            return interceptorContext.checkedCast(result);
        };
    }

    @NonNull
    private <T, R> BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>, AbstractQueryContext<?>> createQueryFunction(
            final ApplicationContext context, final MethodView<T, ?> method,
            final ElementAnnotationsIntrospector annotations) {
        if (annotations.has(NamedQuery.class))
            return this.namedQueryContextFunction(context, method, annotations);
        else if (annotations.has(Query.class))
            return this.unnamedQueryContextFunction(context, method, annotations);
        else
            throw new IllegalStateException("No applicable query annotation found on method " + method);
    }

    @NonNull
    private <T, R> BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>, AbstractQueryContext<?>> unnamedQueryContextFunction(
            final ApplicationContext context, final MethodView<T, ?> method,
            final ElementAnnotationsIntrospector annotations) {

        final BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>, AbstractQueryContext<?>> queryContextFunction;
        final Query query = annotations.get(Query.class).get();
        final TypeView<?> entityType = this.entityType(context, method, query);

        queryContextFunction = (interceptorContext, repository) -> {
            return new UnnamedQueryContext(query, interceptorContext.args(), method, entityType, context, repository);
        };
        return queryContextFunction;
    }

    @NonNull
    private <T, R> BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>, AbstractQueryContext<?>> namedQueryContextFunction(
            final ApplicationContext context, final MethodView<T, ?> method,
            final ElementAnnotationsIntrospector annotations) {

        final BiFunction<MethodInterceptorContext<T, R>, JpaRepository<?, ?>, AbstractQueryContext<?>> queryContextFunction;
        final NamedQuery namedQuery = annotations.get(NamedQuery.class).get();
        final TypeView<?> entityType = this.entityType(context, method, namedQuery);

        queryContextFunction = (interceptorContext, repository) -> {
            return new NamedQueryContext(namedQuery, interceptorContext.args(), method, entityType, context, repository);
        };
        return queryContextFunction;
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context, final Query query) {
        final TypeView<?> typeView = this.entityType(applicationContext, context, query.entityType());
        if (typeView == null) {
            if (query.type() == QueryType.NATIVE)
                throw new UndeterminedEntityTypeException(context.qualifiedName());
            else return applicationContext.environment().introspect(Void.class);
        }
        return typeView;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context, final NamedQuery query) {
        final TypeView<?> typeView = this.entityType(applicationContext, context, query.entityType());
        if (typeView == null) return applicationContext.environment().introspect(Void.class);
        return typeView;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context, final Class<?> entityType) {
        final TypeView<?> queryEntityType = applicationContext.environment().introspect(entityType);
        if (!queryEntityType.isVoid()) return queryEntityType;

        final TypeView<?> returnType = context.genericReturnType();
        if (returnType.isVoid()) {
            final List<TypeView<?>> parameters = context.declaredBy().typeParameters().from(JpaRepository.class);
            return parameters.get(0);
        }

        if (returnType.isChildOf(Collection.class)) {
            final List<TypeView<?>> typeParameters = returnType.typeParameters().all();
            if (typeParameters.isEmpty()) {
                return null;
            }
            return typeParameters.get(0);
        }
        else return returnType;
    }
}
