package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.data.QueryFunction;
import org.dockbox.hartshorn.data.TransactionManager;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.jpa.JpaRepository;

@Service(activators = UsePersistence.class, requires = "org.hibernate.Hibernate")
public class HibernateProviders {

    @Provider
    public Class<? extends JpaRepository> jpaRepository() {
        return HibernateJpaRepository.class;
    }

    @Provider
    public Class<? extends TransactionManager> transactionManager() {
        return HibernateTransactionManager.class;
    }

    @Provider
    public QueryFunction queryFunction() {
        return new HibernateQueryFunction();
    }

    @Provider
    public Class<? extends HibernateRemote> remote() {
        return HibernateRemoteImpl.class;
    }
}
