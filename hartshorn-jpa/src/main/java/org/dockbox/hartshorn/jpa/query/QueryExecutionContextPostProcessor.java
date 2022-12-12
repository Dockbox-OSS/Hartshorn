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
