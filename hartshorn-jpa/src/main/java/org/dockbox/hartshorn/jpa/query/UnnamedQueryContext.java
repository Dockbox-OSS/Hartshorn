package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.Query;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;

public class UnnamedQueryContext extends AbstractQueryContext<Query> {

    public UnnamedQueryContext(final Query annotation, final Object[] args,
                               final MethodView<?, ?> method,
                               final TypeView<?> entityType,
                               final ApplicationContext applicationContext,
                               final JpaRepository<?, ?> repository) {
        super(annotation, args, method, entityType, applicationContext, repository);
    }

    @Override
    public boolean automaticClear() {
        return this.annotation().automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.annotation().automaticFlush();
    }

    protected jakarta.persistence.Query persistenceQuery(final EntityManager entityManager, final Query query) throws IllegalArgumentException {
        return switch (query.type()) {
            case JPQL -> {
                if (this.entityType().isVoid()) yield entityManager.createQuery(query.value());
                else yield entityManager.createQuery(query.value(), this.entityType().type());
            }
            case NATIVE -> {
                if (this.entityType().isVoid()) yield entityManager.createNativeQuery(query.value());
                else yield entityManager.createNativeQuery(query.value(), this.entityType().type());
            }
            default -> throw new UnsupportedQueryTypeException(query.type());
        };
    }
}
