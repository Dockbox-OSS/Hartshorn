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

import org.dockbox.hartshorn.jpa.transaction.TransactionManager;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jakarta.persistence.EntityManager;

public class HibernateTransactionManager implements TransactionManager {

    private final Session session;

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
        final Transaction transaction = this.session.getTransaction();
        if (transaction.isActive()) {
            transaction.commit();
        }
    }

    @Override
    public void rollbackTransaction() {
        final Transaction transaction = this.session.getTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }
}
