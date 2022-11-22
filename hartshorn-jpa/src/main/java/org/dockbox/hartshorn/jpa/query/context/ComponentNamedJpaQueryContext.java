package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
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
    protected Query persistenceQuery(final EntityManager entityManager) throws IllegalArgumentException {
        final TypeView<?> resultType = this.queryResultType();

        if (resultType.isVoid() || this.context.nativeQuery())
            return entityManager.createNamedQuery(this.context.name());
        else
            return entityManager.createNamedQuery(this.context.name(), resultType.type());
    }

    @Override
    public boolean automaticClear() {
        return this.context.automaticClear();
    }

    @Override
    public boolean automaticFlush() {
        return this.context.automaticFlush();
    }
}
