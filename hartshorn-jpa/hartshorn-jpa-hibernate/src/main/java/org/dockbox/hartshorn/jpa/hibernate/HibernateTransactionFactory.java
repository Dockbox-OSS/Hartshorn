package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.jpa.transaction.TransactionFactory;
import org.dockbox.hartshorn.jpa.transaction.TransactionManager;

import jakarta.persistence.EntityManager;

public class HibernateTransactionFactory implements TransactionFactory {

    @Override
    public TransactionManager manager(final EntityManager entityManager) {
        return new HibernateTransactionManager(entityManager);
    }
}
