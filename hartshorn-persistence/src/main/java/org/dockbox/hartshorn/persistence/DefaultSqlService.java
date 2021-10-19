package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.util.HartshornUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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

public abstract class DefaultSqlService<EM extends EntityManager & AutoCloseable> implements SqlService {

    public void persist(final Object entity) {
        this.accept(em -> em.persist(entity));
    }

    public <T> T merge(final T entity) {
        return this.transform(em -> em.merge(entity));
    }

    public void remove(final Object entity) {
        this.accept(em -> em.remove(entity));
    }

    public <T> T find(final Class<T> entityClass, final Object primaryKey) {
        return this.transform(em -> em.find(entityClass, primaryKey));
    }

    public <T> T find(final Class<T> entityClass, final Object primaryKey, final Map<String, Object> properties) {
        return this.transform(em -> em.find(entityClass, primaryKey, properties));
    }

    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode) {
        return this.transform(em -> em.find(entityClass, primaryKey, lockMode));
    }

    public <T> T find(final Class<T> entityClass, final Object primaryKey, final LockModeType lockMode, final Map<String, Object> properties) {
        return this.transform(em -> em.find(entityClass, primaryKey, lockMode, properties));
    }

    public <T> T getReference(final Class<T> entityClass, final Object primaryKey) {
        return this.transform(em -> em.getReference(entityClass, primaryKey));
    }

    public void flush() {
        this.entityManager().flush();
    }

    public void setFlushMode(final FlushModeType flushMode) {
        this.entityManager().setFlushMode(flushMode);
    }

    public FlushModeType getFlushMode() {
        return this.entityManager().getFlushMode();
    }

    public void lock(final Object entity, final LockModeType lockMode) {
        this.accept(em -> em.lock(entity, lockMode));
    }

    public void lock(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        this.accept(em -> em.lock(entity, lockMode, properties));
    }

    public void refresh(final Object entity) {
        this.accept(em -> em.refresh(entity));
    }

    public void refresh(final Object entity, final Map<String, Object> properties) {
        this.accept(em -> em.refresh(entity, properties));
    }

    public void refresh(final Object entity, final LockModeType lockMode) {
        this.accept(em -> em.refresh(entity, lockMode));
    }

    public void refresh(final Object entity, final LockModeType lockMode, final Map<String, Object> properties) {
        this.accept(em -> em.refresh(entity, lockMode, properties));
    }

    public void clear() {
        this.entityManager().clear();
    }

    public void detach(final Object entity) {
        this.entityManager().detach(entity);
    }

    public boolean contains(final Object entity) {
        return this.transform(em -> em.contains(entity));
    }

    public LockModeType getLockMode(final Object entity) {
        return this.entityManager().getLockMode(entity);
    }

    public void setProperty(final String propertyName, final Object value) {
        this.entityManager().setProperty(propertyName, value);
    }

    public Map<String, Object> getProperties() {
        return this.entityManager().getProperties();
    }

    public Query createQuery(final String qlString) {
        return this.entityManager().createQuery(qlString);
    }

    public <T> TypedQuery<T> createQuery(final CriteriaQuery<T> criteriaQuery) {
        return this.entityManager().createQuery(criteriaQuery);
    }

    public Query createQuery(final CriteriaUpdate updateQuery) {
        return this.entityManager().createQuery(updateQuery);
    }

    public Query createQuery(final CriteriaDelete deleteQuery) {
        return this.entityManager().createQuery(deleteQuery);
    }

    public <T> TypedQuery<T> createQuery(final String qlString, final Class<T> resultClass) {
        return this.entityManager().createQuery(qlString, resultClass);
    }

    public Query createNamedQuery(final String name) {
        return this.entityManager().createNamedQuery(name);
    }

    public <T> TypedQuery<T> createNamedQuery(final String name, final Class<T> resultClass) {
        return this.entityManager().createNamedQuery(name, resultClass);
    }

    public Query createNativeQuery(final String sqlString) {
        return this.entityManager().createNativeQuery(sqlString);
    }

    public Query createNativeQuery(final String sqlString, final Class resultClass) {
        return this.entityManager().createNativeQuery(sqlString, resultClass);
    }

    public Query createNativeQuery(final String sqlString, final String resultSetMapping) {
        return this.entityManager().createNativeQuery(sqlString, resultSetMapping);
    }

    public StoredProcedureQuery createNamedStoredProcedureQuery(final String name) {
        return this.entityManager().createNamedStoredProcedureQuery(name);
    }

    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName) {
        return this.entityManager().createStoredProcedureQuery(procedureName);
    }

    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final Class... resultClasses) {
        return this.entityManager().createStoredProcedureQuery(procedureName, resultClasses);
    }

    public StoredProcedureQuery createStoredProcedureQuery(final String procedureName, final String... resultSetMappings) {
        return this.entityManager().createStoredProcedureQuery(procedureName, resultSetMappings);
    }

    public void joinTransaction() {
        this.entityManager().joinTransaction();
    }

    public boolean isJoinedToTransaction() {
        return this.entityManager().isJoinedToTransaction();
    }

    public <T> T unwrap(final Class<T> cls) {
        return this.entityManager().unwrap(cls);
    }

    public Object getDelegate() {
        return this.entityManager().getDelegate();
    }

    public void close() {
        this.entityManager().close();
    }

    public boolean isOpen() {
        return this.entityManager().isOpen();
    }

    public EntityTransaction getTransaction() {
        return this.entityManager().getTransaction();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManager().getEntityManagerFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return this.entityManager().getCriteriaBuilder();
    }

    public Metamodel getMetamodel() {
        return this.entityManager().getMetamodel();
    }

    public <T> EntityGraph<T> createEntityGraph(final Class<T> rootType) {
        return this.entityManager().createEntityGraph(rootType);
    }

    public EntityGraph<?> createEntityGraph(final String graphName) {
        return this.entityManager().createEntityGraph(graphName);
    }

    public EntityGraph<?> getEntityGraph(final String graphName) {
        return this.entityManager().getEntityGraph(graphName);
    }

    public <T> List<EntityGraph<? super T>> getEntityGraphs(final Class<T> entityClass) {
        return this.entityManager().getEntityGraphs(entityClass);
    }

    @Override
    public <T> Set<T> findAll(final Class<T> type) {
        return this.transform(em -> {
            final CriteriaBuilder builder = em.getCriteriaBuilder();
            final CriteriaQuery<T> criteria = builder.createQuery(type);
            criteria.from(type);
            final List<T> data = em.createQuery(criteria).getResultList();
            return HartshornUtils.asUnmodifiableSet(data);
        });
    }

    @Override
    public <T> Exceptional<T> findById(final Class<T> type, final Object id) {
        return this.transform(em -> Exceptional.of(() -> em.find(type, id)));
    }

    protected abstract void accept(final Consumer<EM> consumer);

    protected abstract <T> T transform(final Function<EM, T> function);

    protected abstract EntityManager entityManager();
}
