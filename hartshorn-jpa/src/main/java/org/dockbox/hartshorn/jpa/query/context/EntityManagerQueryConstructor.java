package org.dockbox.hartshorn.jpa.query.context;

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.QueryExecuteTypeLookup;

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
        return this.entityManager.createQuery(jpql);
    }

    @Override
    public Query createNativeQuery(final String nativeQuery, final AbstractJpaQueryContext context) {
        return this.entityManager.createNativeQuery(nativeQuery);
    }

    @Override
    public Query createNamedQuery(final String name, final AbstractJpaQueryContext context) {
        return this.entityManager.createNamedQuery(name);
    }
}
