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

import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.ProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.services.ProcessingOrder;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodInterceptorPostProcessor;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.annotations.EntityModifier;
import org.dockbox.hartshorn.data.annotations.Query;
import org.dockbox.hartshorn.data.annotations.Query.QueryType;
import org.dockbox.hartshorn.data.annotations.Transactional;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.context.QueryContext;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

import java.util.Collection;
import java.util.List;

@AutomaticActivation
public class QueryPostProcessor extends ServiceAnnotatedMethodInterceptorPostProcessor<Query, UsePersistence> {

    @Override
    public Class<UsePersistence> activator() {
        return UsePersistence.class;
    }

    @Override
    public Class<Query> annotation() {
        return Query.class;
    }

    @Override
    public <T> boolean preconditions(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final MethodContext<?, T> method = methodContext.method();
        final TypeContext<T> parent = method.parent();
        return parent.childOf(JpaRepository.class);
    }

    @Override
    public <T, R> ProxyFunction<T, R> process(final ApplicationContext context, final MethodProxyContext<T> methodContext) {
        final MethodContext<?, T> method = methodContext.method();
        final QueryFunction function = context.get(QueryFunction.class);
        final boolean modifying = method.annotation(EntityModifier.class).present();
        final boolean transactional = method.annotation(Transactional.class).present();
        final Query query = method.annotation(Query.class).get();
        final TypeContext<?> entityType = this.entityType(method, query);

        return (T instance, Object[] args, ProxyContext proxyContext) -> {
            final JpaRepository<?, ?> repository = (JpaRepository<?, ?>) methodContext.instance();
            if (query.automaticFlush() && !transactional) repository.flush();

            final QueryContext queryContext = new QueryContext(query, args, method, entityType, context, repository, modifying);

            final Object result = function.execute(queryContext);

            return (R) result;
        };
    }

    @Override
    public Integer order() {
        return ProcessingOrder.LATE;
    }

    protected TypeContext<?> entityType(final MethodContext<?, ?> context, final Query query) {
        final TypeContext<?> queryEntityType = TypeContext.of(query.entityType());
        if (queryEntityType.isVoid()) {
            final TypeContext<?> returnType = context.genericReturnType();
            if (returnType.childOf(Collection.class)) {
                final List<TypeContext<?>> typeParameters = returnType.typeParameters();
                if (typeParameters.isEmpty()) {
                    if (query.type() == QueryType.NATIVE)
                        throw new IllegalStateException("Could not determine entity type of " + context.qualifiedName() + ". Alternatively, set the entityType in the @Query annotation on this method or change the query to JPQL.");
                    else return TypeContext.VOID;
                }
                return typeParameters.get(0);
            }
            else return returnType;
        }
        else return queryEntityType;
    }
}
