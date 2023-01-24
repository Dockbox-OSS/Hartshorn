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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerJpaRepository;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.util.ApplicationException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Component
public class HibernateJpaRepository<T, ID> extends EntityManagerJpaRepository<T, ID> {

    private final DataSourceConfiguration connection;

    @Inject
    @Enable(false) // Enabling is delegated
    @Required
    private HibernateEntityManagerCarrier entityManager;
    @Inject
    private ApplicationContext applicationContext;

    @Bound
    public HibernateJpaRepository(final Class<T> type) {
        this(type, null);
    }

    @Bound
    public HibernateJpaRepository(final Class<T> type, final DataSourceConfiguration connection) {
        super(type);
        this.connection = connection;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public void flush() {
        this.entityManager.flush();
    }

    @Override
    public void close() {
        this.entityManager.close();
    }

    @Override
    public Session manager() {
        return this.entityManager.manager();
    }

    @Override
    public EntityManagerCarrier configuration(final DataSourceConfiguration configuration) {
        return this.entityManager.configuration(configuration);
    }

    @Override
    protected Transaction transaction(final EntityManager manager) {
        return this.manager().beginTransaction();
    }

    @PostConstruct
    public void enable() throws ApplicationException {
        if (this.connection != null && this.entityManager.configuration() == null) {
            this.entityManager.configuration(this.connection);
        }
        this.entityManager.enable();
    }
}
