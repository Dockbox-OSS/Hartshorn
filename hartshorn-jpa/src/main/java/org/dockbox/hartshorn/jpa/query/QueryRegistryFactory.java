package org.dockbox.hartshorn.jpa.query;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.factory.Factory;
import org.dockbox.hartshorn.jpa.annotations.UseQuerying;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Service
@RequiresActivator(UseQuerying.class)
public interface QueryRegistryFactory {

    @Factory
    NamedQueryRegistry create(EntityManager entityManager);

    @Factory
    NamedQueryRegistry create(EntityManagerFactory entityManagerFactory);
}
