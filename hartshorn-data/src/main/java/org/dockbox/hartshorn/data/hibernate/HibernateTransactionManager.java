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

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.data.TransactionManager;
import org.hibernate.Session;

import javax.persistence.EntityManager;

@ComponentBinding(TransactionManager.class)
public class HibernateTransactionManager implements TransactionManager {

    private final Session session;

    @Bound
    public HibernateTransactionManager(final EntityManager entityManager) {
        if (entityManager instanceof Session session) this.session = session;
        else throw new IllegalArgumentException("EntityManager must be a Session");
    }

    @Override
    public void beginTransaction() {
        this.session.beginTransaction();
    }

    @Override
    public void commitTransaction() {
        this.session.getTransaction().commit();
    }

    @Override
    public void rollbackTransaction() {
        this.session.getTransaction().rollback();
    }
}
