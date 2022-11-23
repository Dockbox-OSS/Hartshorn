package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.jpa.query.NamedQueryRegistry;
import org.hibernate.Session;
import org.hibernate.query.named.NamedObjectRepository;
import org.hibernate.query.sqm.spi.SqmCreationContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;

public class HibernateNamedQueryRegistry implements NamedQueryRegistry {

    private final EntityManagerFactory sessionFactory;
    private final NamedObjectRepository namedObjectRepository;

    @Bound
    public HibernateNamedQueryRegistry(final EntityManager entityManager) {
        this.sessionFactory = entityManager.getEntityManagerFactory();

        if (this.sessionFactory instanceof SqmCreationContext creationContext) {
            this.namedObjectRepository = creationContext.getQueryEngine().getNamedObjectRepository();
        }
        else if (entityManager instanceof Session session && session.getSessionFactory() instanceof SqmCreationContext creationContext) {
            this.namedObjectRepository = creationContext.getQueryEngine().getNamedObjectRepository();
        }
        else {
            throw new IllegalStateException("Unable to find NamedObjectRepository for EntityManager " + entityManager);
        }
    }

    @Bound
    public HibernateNamedQueryRegistry(final EntityManagerFactory entityManagerFactory) {
        this.sessionFactory = entityManagerFactory;

        if (this.sessionFactory instanceof SqmCreationContext creationContext) {
            this.namedObjectRepository = creationContext.getQueryEngine().getNamedObjectRepository();
        }
        else {
            throw new IllegalStateException("Unable to find NamedObjectRepository for EntityManagerFactory " + entityManagerFactory);
        }
    }

    @Override
    public boolean has(final String name) {
        return this.namedObjectRepository.getSqmQueryMemento(name) != null
                || this.namedObjectRepository.getNativeQueryMemento(name) != null;
    }

    @Override
    public void register(final String name, final Query query) {
        this.sessionFactory.addNamedQuery(name, query);
    }
}
