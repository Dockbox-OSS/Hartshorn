package org.dockbox.hartshorn.persistence.service;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.MethodProxyContext;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.ProxyContext;
import org.dockbox.hartshorn.core.proxy.ProxyFunction;
import org.dockbox.hartshorn.core.services.ServiceAnnotatedMethodModifier;
import org.dockbox.hartshorn.core.services.ServiceOrder;
import org.dockbox.hartshorn.persistence.JpaRepository;
import org.dockbox.hartshorn.persistence.QueryFunction;
import org.dockbox.hartshorn.persistence.annotations.EntityModifier;
import org.dockbox.hartshorn.persistence.annotations.Query;
import org.dockbox.hartshorn.persistence.annotations.Transactional;
import org.dockbox.hartshorn.persistence.annotations.UsePersistence;
import org.dockbox.hartshorn.persistence.context.QueryContext;

public class QueryModifier extends ServiceAnnotatedMethodModifier<Query, UsePersistence> {

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
        final boolean transactional = method.annotation(Transactional.class).present();
        final boolean modifying = method.annotation(EntityModifier.class).present();
        final Query query = method.annotation(Query.class).get();

        return (T instance, Object[] args, ProxyContext proxyContext) -> {
            final JpaRepository<?, ?> repository = (JpaRepository<?, ?>) methodContext.instance();
            if (query.automaticFlush()) repository.flush();

            final QueryContext queryContext = new QueryContext(query, args, method, repository, transactional, modifying);

            final Object result = function.execute(queryContext);

            return (R) result;
        };
    }

    @Override
    public ServiceOrder order() {
        return ServiceOrder.LATE;
    }
}
