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

package org.dockbox.hartshorn.data.jpa;

import org.dockbox.hartshorn.util.Result;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

public abstract class EntityManagerJpaRepository<T, ID> implements JpaRepository<T, ID>, EntityManagerCarrier {

    private final Class<T> type;

    protected EntityManagerJpaRepository(final Class<T> type) {
        this.type = type;
    }

    @Override
    public T refresh(final T entity) {
        return this.transformTransactional(session -> {
            session.refresh(entity);
            return entity;
        });
    }

    @Override
    public T save(final T object) {
        return this.transformTransactional(session -> {
            session.persist(object);
            return object;
        });
    }

    @Override
    public T update(final T object) {
        return this.transformTransactional(session -> {
            session.merge(object);
            return object;
        });
    }

    @Override
    public T updateOrSave(final T object) {
        return this.transformTransactional(session -> {
            session.merge(object);
            return object;
        });
    }

    @Override
    public void delete(final T object) {
        this.performTransactional(session -> session.remove(object));
    }

    @Override
    public Set<T> findAll() {
        final EntityManager session = this.manager();
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(this.reify());
        criteria.from(this.reify());
        final List<T> data = session.createQuery(criteria).getResultList();
        return Set.copyOf(data);
    }

    @Override
    public Result<T> findById(final ID id) {
        final EntityManager session = this.manager();
        return Result.of(session.find(this.reify(), id));
    }

    private void performTransactional(final Consumer<EntityManager> action) {
        final EntityManager manager = this.manager();
        final Transaction transaction = this.transaction(manager);
        action.accept(manager);
        transaction.commit();
    }

    private <R> R transformTransactional(final Function<EntityManager, R> action) {
        final EntityManager manager = this.manager();
        final Transaction transaction = this.transaction(manager);
        final R out = action.apply(manager);
        transaction.commit();
        return out;
    }

    public Class<T> reify() {
        return this.type;
    }

    @Override
    public abstract void close();

    protected abstract Transaction transaction(EntityManager manager);
}
