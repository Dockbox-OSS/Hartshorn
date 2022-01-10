package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.data.TransactionManager;
import org.hibernate.Session;

import javax.persistence.EntityManager;

@Binds(TransactionManager.class)
public class HibernateTransactionManager implements TransactionManager {

    private final Session session;

    @Bound
    public HibernateTransactionManager(final EntityManager entityManager) {
        if (entityManager instanceof Session session) this.session = session;
        else throw new IllegalArgumentException("EntityManager must be a Session");
    }

    @Override
    public void beginTransaction() {
        this.session.beginTransaction();
    }

    @Override
    public void commitTransaction() {
        this.session.getTransaction().commit();
    }

    @Override
    public void rollbackTransaction() {
        this.session.getTransaction().rollback();
    }
}
