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
import org.dockbox.hartshorn.component.processing.ComponentProcessingContext;
import org.dockbox.hartshorn.component.processing.ContextConfiguringComponentProcessor;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class QueryExecutionContextPostProcessor extends ContextConfiguringComponentProcessor<QueryExecutionContext> {

    public QueryExecutionContextPostProcessor() {
        super(QueryExecutionContext.class);
    }

    @Override
    protected boolean supports(final ComponentProcessingContext<?> processingContext) {
        final TypeMethodsIntrospector<?> introspector = processingContext.type().methods();
        if (introspector.annotatedWith(LockMode.class).isEmpty()) {
            return false;
        }
        return !introspector.annotatedWith(FlushMode.class).isEmpty();
    }

    @Override
    protected <T> void configure(final ApplicationContext context, final QueryExecutionContext componentContext,
                                 final ComponentProcessingContext<T> processingContext) {

        final TypeMethodsIntrospector<T> methods = processingContext.type().methods();
        this.configure(methods, LockMode.class, LockMode::value, componentContext::lockMode);
        this.configure(methods, FlushMode.class, FlushMode::value, componentContext::flushMode);
    }

    private <A extends Annotation, E> void configure(final TypeMethodsIntrospector<?> methods, final Class<A> annotation,
                                                     final Function<A, E> mapper, final BiConsumer<MethodView<?, ?>, E> setter) {
        for (final MethodView<?, ?> view : methods.annotatedWith(annotation)) {
            final A a = view.annotations().get(annotation).get();
            setter.accept(view, mapper.apply(a));
        }
    }

    @Override
    protected QueryExecutionContext createContext(final ApplicationContext context, final ComponentProcessingContext<?> processingContext) {
        return new QueryExecutionContext();
    }
}
