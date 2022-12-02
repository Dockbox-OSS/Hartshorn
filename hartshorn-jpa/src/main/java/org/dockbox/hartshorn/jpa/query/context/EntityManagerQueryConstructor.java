package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteType;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

public class EntityManagerQueryConstructor implements QueryConstructor {

    @Inject
    private QueryExecuteTypeLookup queryExecuteTypeLookup;
    private final EntityManager entityManager;

    @Bound
    public EntityManagerQueryConstructor(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Query createJpqlQuery(final String jpql, final AbstractJpaQueryContext context) {
        final TypeView<?> resultType = context.queryResultType();
        if (resultType.isVoid()) {
            return this.entityManager.createQuery(jpql);
        }

        final QueryExecuteType queryExecuteType = this.queryExecuteTypeLookup.lookup(jpql);
        if (queryExecuteType == QueryExecuteType.SELECT) {
            return this.entityManager.createQuery(jpql, resultType.type());
        }
        else {
            return this.entityManager.createQuery(jpql);
        }
    }

    @Override
    public Query createNativeQuery(final String nativeQuery, final AbstractJpaQueryContext context) {
        final TypeView<?> resultType = context.queryResultType();
        // Unlike JPQL, we cannot make assumptions about the type of native query. If we only filter for SELECT, we
        // would not be able to support EXEC, WITH, etc. as those are also valid, but not explicitly SELECT queries.
        if (resultType.isVoid()) return this.entityManager.createNativeQuery(nativeQuery);
        else return this.entityManager.createNativeQuery(nativeQuery, resultType.type());
    }

    @Override
    public Query createNamedQuery(final String name, final AbstractJpaQueryContext context) {
        final TypeView<?> resultType = context.queryResultType();
        if (resultType.isVoid()) return this.entityManager.createNamedQuery(name);
        else return this.entityManager.createNamedQuery(name, resultType.type());
    }
}
