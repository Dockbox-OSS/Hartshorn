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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ProcessingOrder;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.annotations.EntityModifier;
import org.dockbox.hartshorn.data.annotations.Query;
import org.dockbox.hartshorn.data.annotations.Query.QueryType;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.context.QueryContext;
import org.dockbox.hartshorn.data.jpa.JpaRepository;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.processing.MethodProxyContext;
import org.dockbox.hartshorn.proxy.processing.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Collection;
import java.util.List;

public class QueryPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Query> {

    @Override
    public Class<Query> annotation() {
        return Query.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final MethodView<T, ?> method = methodContext.method();
        final TypeView<T> parent = method.declaredBy();
        return parent.isChildOf(JpaRepository.class);
    }

    @Override
    public <T, R> MethodInterceptor<T> process(final ApplicationContext context, final MethodProxyContext<T> methodContext, final ComponentProcessingContext<T> processingContext) {
        final MethodView<T, ?> method = methodContext.method();
        final QueryFunction function = context.get(QueryFunction.class);

        final ElementAnnotationsIntrospector annotations = method.annotations();
        final boolean modifying = annotations.has(EntityModifier.class);
        final boolean transactional = annotations.has(Transactional.class);
        final Query query = annotations.get(Query.class).get();
        final TypeView<?> entityType = this.entityType(context, method, query);

        return interceptorContext -> {
            final JpaRepository<?, ?> repository = (JpaRepository<?, ?>) interceptorContext.instance();
            if (query.automaticFlush() && !transactional) repository.flush();

            final QueryContext queryContext = new QueryContext(query, interceptorContext.args(), method, entityType, context, repository, modifying);

            return function.execute(queryContext);
        };
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE;
    }

    protected TypeView<?> entityType(final ApplicationContext applicationContext, final MethodView<?, ?> context, final Query query) {
        final TypeView<?> queryEntityType = applicationContext.environment().introspect(query.entityType());
        if (!queryEntityType.isVoid()) return queryEntityType;

        final TypeView<?> returnType = context.genericReturnType();
        if (returnType.isVoid()) {
            final List<TypeView<?>> parameters = context.declaredBy().typeParameters().from(JpaRepository.class);
            return parameters.get(0);
        }

        if (returnType.isChildOf(Collection.class)) {
            final List<TypeView<?>> typeParameters = returnType.typeParameters().all();
            if (typeParameters.isEmpty()) {
                if (query.type() == QueryType.NATIVE)
                    throw new UndeterminedEntityTypeException(context.qualifiedName());
                else return applicationContext.environment().introspect(Void.class);
            }
            return typeParameters.get(0);
        }
        else return returnType;
    }
}
