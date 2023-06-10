package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.jpa.annotations.UseQuerying;
import org.dockbox.hartshorn.jpa.query.NamedQueryRegistry;
import org.dockbox.hartshorn.jpa.query.QueryComponentFactory;
import org.dockbox.hartshorn.jpa.query.QueryConstructor;
import org.dockbox.hartshorn.jpa.query.context.EntityManagerQueryConstructor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@Service
@RequiresActivator(UseQuerying.class)
public class HibernateQueryComponentFactory implements QueryComponentFactory {

    @Override
    public NamedQueryRegistry queryRegistry(final EntityManager entityManager) {
        return new HibernateNamedQueryRegistry(entityManager);
    }

    @Override
    public NamedQueryRegistry queryRegistry(final EntityManagerFactory entityManagerFactory) {
        return new HibernateNamedQueryRegistry(entityManagerFactory);
    }

    @Override
    public QueryConstructor queryConstructor(final EntityManager entityManager) {
        return new EntityManagerQueryConstructor(entityManager);
    }
}
