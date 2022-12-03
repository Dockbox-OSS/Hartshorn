package org.dockbox.hartshorn.jpa.query.context.named;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.jpa.query.context.AbstractJpaQueryContext;
import org.dockbox.hartshorn.jpa.query.context.application.ComponentNamedQueryContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class ComponentNamedJpaQueryContext extends AbstractJpaQueryContext {

    private final ComponentNamedQueryContext context;

    public ComponentNamedJpaQueryContext(final ComponentNamedQueryContext context,
                                         final Object[] args,
                                         final MethodView<?, ?> method,
                                         final TypeView<?> entityType,
                                         final ApplicationContext applicationContext,
                                         final Object persistenceCapable) {
        super(args, method, entityType, applicationContext, persistenceCapable);
        this.context = context;
    }

    @Override
    protected Query persistenceQuery(final QueryConstructor queryConstructor, final EntityManager entityManager) throws IllegalArgumentException {
        return queryConstructor.createNamedQuery(this.context.name(), this);
    }

    @Override
    public boolean automaticClear() {
        return this.context.automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.context.automaticFlush();
    }

    @Override
    public QueryExecuteType queryType() {
        final QueryExecuteTypeLookup queryExecuteTypeLookup = this.applicationContext().get(QueryExecuteTypeLookup.class);
        return queryExecuteTypeLookup.lookup(this.query());
    }
}
