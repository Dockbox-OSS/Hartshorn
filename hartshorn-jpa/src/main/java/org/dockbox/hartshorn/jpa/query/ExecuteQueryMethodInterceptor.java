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

package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;

public class ExecuteQueryMethodInterceptor<T, R> implements MethodInterceptor<T, R> {

    private final JpaQueryContextCreator contextCreator;
    private final ApplicationContext context;
    private final TypeView<?> entityType;
    private final MethodView<T, ?> method;
    private final QueryExecutor function;
    private final ConversionService conversionService;

    public ExecuteQueryMethodInterceptor(final JpaQueryContextCreator contextCreator, final ApplicationContext context,
                                         final TypeView<?> entityType, final MethodView<T, ?> method,
                                         final QueryExecutor function, final ConversionService conversionService) {
        this.contextCreator = contextCreator;
        this.context = context;
        this.entityType = entityType;
        this.method = method;
        this.function = function;
        this.conversionService = conversionService;
    }

    @Override
    public R intercept(final MethodInterceptorContext<T, R> interceptorContext) throws Throwable {
        final T persistenceCapable = interceptorContext.instance();
        final JpaQueryContext jpaQueryContext = this.contextCreator.create(this.context, interceptorContext, this.entityType, persistenceCapable)
                .orElseThrow(() -> new IllegalStateException("No JPA query context found for method " + this.method));

        final EntityManager entityManager = jpaQueryContext.entityManager();
        if (jpaQueryContext.automaticFlush() && entityManager.getTransaction().isActive())
            entityManager.flush();

        final Object result = this.function.execute(jpaQueryContext);
        //noinspection unchecked
        return (R) this.conversionService.convert(result, this.method.returnType().type());
    }
}
