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

package org.dockbox.hartshorn.jpa.query.context.unnamed;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class UnnamedJpaQueryContextCreator implements JpaQueryContextCreator {

    @Override
    public <T> Option<JpaQueryContext> create(final ApplicationContext applicationContext,
                                              final MethodInterceptorContext<T, ?> interceptorContext,
                                              final TypeView<?> entityType,
                                              final Object persistenceCapable) {

        final MethodView<T, ?> method = interceptorContext.method();
        return method.annotations()
                .get(Query.class)
                .map(query -> new UnnamedJpaQueryContext(
                        query,
                        interceptorContext.args(),
                        method,
                        entityType,
                        applicationContext,
                        persistenceCapable
                ));
    }

    @Override
    public <T> boolean supports(final ComponentProcessingContext<T> processingContext, final MethodView<T, ?> method) {
        return method.annotations().has(Query.class);
    }
}
