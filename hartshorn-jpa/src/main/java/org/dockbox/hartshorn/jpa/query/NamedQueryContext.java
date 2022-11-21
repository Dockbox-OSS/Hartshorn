package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.JpaRepository;
import org.dockbox.hartshorn.jpa.annotations.NamedQuery;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class NamedQueryContext extends AbstractQueryContext<NamedQuery> {

    private final NamedQuery namedQuery;

    public NamedQueryContext(final NamedQuery namedQuery,
                             final Object[] args,
                             final MethodView<?, ?> method,
                             final TypeView<?> entityType,
                             final ApplicationContext applicationContext,
                             final JpaRepository<?, ?> repository) {
        super(null, args, method, entityType, applicationContext, repository);
        this.namedQuery = namedQuery;
    }

    @Override
    public boolean automaticClear() {
        return this.namedQuery.automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.namedQuery.automaticFlush();
    }

    @Override
    protected Query persistenceQuery(final EntityManager entityManager,
                                     final NamedQuery query) throws IllegalArgumentException {
        return entityManager.createNamedQuery(this.namedQuery.value(), this.entityType().type());
    }
}
