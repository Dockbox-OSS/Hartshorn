package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.jpa.annotations.Query.QueryType;
import org.dockbox.hartshorn.jpa.query.UnsupportedQueryTypeException;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;

public class UnnamedJpaQueryContext extends AbstractJpaQueryContext {

    private final Query annotation;

    public UnnamedJpaQueryContext(final Query annotation, final Object[] args,
                                  final MethodView<?, ?> method,
                                  final TypeView<?> entityType,
                                  final ApplicationContext applicationContext,
                                  final Object persistenceCapable) {
        super(args, method, entityType, applicationContext, persistenceCapable);
        this.annotation = annotation;
    }

    @Override
    public boolean automaticClear() {
        return this.annotation.automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.annotation.automaticFlush();
    }

    protected jakarta.persistence.Query persistenceQuery(final EntityManager entityManager) throws IllegalArgumentException {
        final String query = this.annotation.value();
        final QueryType queryType = this.annotation.type();
        final TypeView<?> resultType = this.queryResultType();

        return switch (queryType) {
            case JPQL -> {
                if (resultType.isVoid()) yield entityManager.createQuery(query);
                else yield entityManager.createQuery(query, resultType.type());
            }
            case NATIVE -> {
                if (resultType.isVoid()) yield entityManager.createNativeQuery(query);
                else yield entityManager.createNativeQuery(query, resultType.type());
            }
            default -> throw new UnsupportedQueryTypeException(queryType);
        };
    }
}
