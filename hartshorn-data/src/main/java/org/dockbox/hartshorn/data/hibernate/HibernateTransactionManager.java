/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
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
