package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.jpa.annotations.UseTransactionManagement;
import org.dockbox.hartshorn.jpa.transaction.TransactionFactory;
import org.dockbox.hartshorn.jpa.transaction.TransactionManager;

import jakarta.persistence.EntityManager;

@Service
@RequiresActivator(UseTransactionManagement.class)
public class HibernateTransactionFactory implements TransactionFactory {

    @Override
    public TransactionManager manager(final EntityManager entityManager) {
        return new HibernateTransactionManager(entityManager);
    }
}
