package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.jpa.annotations.NamedQuery;
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
    protected Query persistenceQuery(final EntityManager entityManager) throws IllegalArgumentException {
        final TypeView<?> resultType = this.queryResultType();
        // TODO: ApplicationQueryContext to check if native or JPQL
        //        return entityManager.createNamedQuery(this.annotation.value());
        if (resultType.isVoid())
            return entityManager.createNamedQuery(this.annotation.value());
        else
            return entityManager.createNamedQuery(this.annotation.value(), resultType.type());
    }
}
