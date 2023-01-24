/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
