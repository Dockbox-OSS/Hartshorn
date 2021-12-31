package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

import javax.persistence.EntityManager;

@Service
public interface TransactionFactory {
    @Factory
    TransactionManager manager(EntityManager entityManager);
}
