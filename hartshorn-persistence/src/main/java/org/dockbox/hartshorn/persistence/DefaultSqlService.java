/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General default License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General default License for more details.
 *
 * You should have received a copy of the GNU Lesser General default License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;

public interface DefaultSqlService<EM extends EntityManager & AutoCloseable> extends DefaultJpaRepository<EM>, SqlService {

    default void persist(final Object entity) {
        this.accept(em -> em.persist(entity));
    }

    default <T> T merge(final T entity) {
        return this.transform(em -> em.merge(entity));
    }

    default void remove(final Object entity) {
        this.accept(em -> em.remove(entity));
    }

    default <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return this.transform(em -> em.find(entityClass, primaryKey));
    }

    default <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        return this.transform(em -> em.find(entityClass, primaryKey, properties));
    }

    default <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        return this.transform(em -> em.find(entityClass, primaryKey, lockMode));
    }

    default <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties) {
        return this.transform(em -> em.find(entityClass, primaryKey, lockMode, properties));
    }

    default <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
        return this.transform(em -> em.getReference(entityClass, primaryKey));
    }

    default void flush() {
        throw this.unsupported();
    }

    default void setFlushMode(final FlushModeType flushMode) {
        throw this.unsupported();
    }

    default FlushModeType getFlushMode() {
        throw this.unsupported();
    }

    default void lock(final Object entity, final LockModeType lockMode) {
        this.accept(em -> em.lock(entity, lockMode));
    }

    default void lock(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        this.accept(em -> em.lock(entity, lockMode, properties));
    }

    default void refresh(final Object entity) {
        this.accept(em -> em.refresh(entity));
    }

    default void refresh(final Object entity, final Map<String, Object> properties) {
        this.accept(em -> em.refresh(entity, properties));
    }

    default void refresh(final Object entity, final LockModeType lockMode) {
        this.accept(em -> em.refresh(entity, lockMode));
    }

    default void refresh(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        this.accept(em -> em.refresh(entity, lockMode, properties));
    }

    default void clear() {
        throw this.unsupported();
    }

    default void detach(final Object entity) {
        this.entityManager().detach(entity);
    }

    default boolean contains(final Object entity) {
        return this.transform(em -> em.contains(entity));
    }

    default LockModeType getLockMode(final Object entity) {
        throw this.unsupported();
    }

    default void setProperty(final String propertyName, final Object value) {
        throw this.unsupported();
    }

    default Map<String, Object> getProperties() {
        throw this.unsupported();
    }

    default Query createQuery(final String qlString) {
        return this.entityManager().createQuery(qlString);
    }

    default <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return this.entityManager().createQuery(criteriaQuery);
    }

    default Query createQuery(final CriteriaUpdate updateQuery) {
        return this.entityManager().createQuery(updateQuery);
    }

    default Query createQuery(final CriteriaDelete deleteQuery) {
        return this.entityManager().createQuery(deleteQuery);
    }

    default <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        return this.entityManager().createQuery(qlString, resultClass);
    }

    default Query createNamedQuery(final String name) {
        return this.entityManager().createNamedQuery(name);
    }

    default <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        return this.entityManager().createNamedQuery(name, resultClass);
    }

    default Query createNativeQuery(final String sqlString) {
        return this.entityManager().createNativeQuery(sqlString);
    }

    default Query createNativeQuery(final String sqlString, final Class resultClass) {
        return this.entityManager().createNativeQuery(sqlString, resultClass);
    }

    default Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        return this.entityManager().createNativeQuery(sqlString, resultSetMapping);
    }

    default StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
        return this.entityManager().createNamedStoredProcedureQuery(name);
    }

    default StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
        return this.entityManager().createStoredProcedureQuery(procedureName);
    }

    default StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses) {
        return this.entityManager().createStoredProcedureQuery(procedureName, resultClasses);
    }

    default StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings) {
        return this.entityManager().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    default void joinTransaction() {
        throw this.unsupported();
    }

    default boolean isJoinedToTransaction() {
        throw this.unsupported();
    }

    default <T> T unwrap(final Class<T> cls) {
        return this.entityManager().unwrap(cls);
    }

    default Object getDelegate() {
        return this.entityManager().getDelegate();
    }

    default void close() {
        throw this.unsupported();
    }

    default boolean isOpen() {
        throw this.unsupported();
    }

    default EntityTransaction getTransaction() {
        return this.entityManager().getTransaction();
    }

    default EntityManagerFactory getEntityManagerFactory() {
        return this.entityManager().getEntityManagerFactory();
    }

    default CriteriaBuilder getCriteriaBuilder() {
        return this.entityManager().getCriteriaBuilder();
    }

    default Metamodel getMetamodel() {
        return this.entityManager().getMetamodel();
    }

    default <T> EntityGraph<T> createEntityGraph(final Class<T> rootType) {
        return this.entityManager().createEntityGraph(rootType);
    }

    default EntityGraph<?> createEntityGraph(final String graphName) {
        return this.entityManager().createEntityGraph(graphName);
    }

    default EntityGraph<?> getEntityGraph(final String graphName) {
        return this.entityManager().getEntityGraph(graphName);
    }

    default <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
        return this.entityManager().getEntityGraphs(entityClass);
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Action is not supported, use #entityManager() instead");
    }
}
