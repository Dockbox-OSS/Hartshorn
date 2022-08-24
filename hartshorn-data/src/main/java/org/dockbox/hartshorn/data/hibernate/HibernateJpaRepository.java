/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.data.hibernate;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.Enableable;
import org.dockbox.hartshorn.data.jpa.EntityManagerCarrier;
import org.dockbox.hartshorn.data.jpa.EntityManagerJpaRepository;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.inject.Enable;
import org.dockbox.hartshorn.inject.Required;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.ApplicationException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@Component
public class HibernateJpaRepository<T, ID> extends EntityManagerJpaRepository<T, ID> implements Enableable {

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

    @Override
    public boolean canEnable() {
        if (this.connection != null && this.entityManager.configuration() == null) {
            this.entityManager.configuration(this.connection);
        }
        return this.entityManager.canEnable();
    }

    @Override
    public void enable() throws ApplicationException {
        this.entityManager.enable();
    }
}
