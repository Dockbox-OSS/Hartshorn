package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.JpaQueryContextCreator;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
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
