package org.dockbox.hartshorn.jpa.query.context.named;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.annotations.NamedQuery;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.jpa.query.context.AbstractJpaQueryContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class NamedJpaQueryContext extends AbstractJpaQueryContext {

    private final NamedQuery annotation;

    public NamedJpaQueryContext(final NamedQuery annotation,
                                final Object[] args,
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

    @Override
    public QueryExecuteType queryType() {
        return this.applicationContext().get(QueryExecuteTypeLookup.class).lookup(this.query());
    }

    @Override
    protected Query persistenceQuery(final QueryConstructor queryConstructor, final EntityManager entityManager) throws IllegalArgumentException {
        return queryConstructor.createNamedQuery(this.annotation.value(), this);
    }
}
