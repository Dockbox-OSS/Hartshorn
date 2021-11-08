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

package org.dockbox.hartshorn.persistence.hibernate;

import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.properties.AttributeHolder;
import org.dockbox.hartshorn.persistence.JpaRepository;
import org.dockbox.hartshorn.persistence.context.EntityContext;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;
import org.dockbox.hartshorn.persistence.properties.Remote;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import lombok.AccessLevel;
import lombok.Getter;

@Binds(JpaRepository.class)
public class HibernateJpaRepository<T, ID> implements JpaRepository<T, ID>, AttributeHolder, Closeable {

    private final Configuration configuration = new Configuration();
    private final Class<T> type;

    /**
     Temporary solution to work with factory-based provision, see #477.
     */
    @Deprecated(since = "4.2.3", forRemoval = true)
    private final ConnectionAttribute connectionAttribute;

    @Getter(AccessLevel.PROTECTED)
    private SessionFactory factory;

    @Getter(AccessLevel.PROTECTED)
    private PersistenceConnection connection;

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    private Session session;

    @Bound
    public HibernateJpaRepository(final Class<T> type, final ConnectionAttribute connectionAttribute) throws ApplicationException {
        this.type = type;
        this.connectionAttribute = connectionAttribute;
    }

    @Override
    public boolean canEnable() {
        return this.factory == null;
    }

    @Override
    public void apply(final Attribute<?> property) throws ApplicationException {
        if (property instanceof ConnectionAttribute connectionAttribute) {
            this.connection = connectionAttribute.value();

            if (HartshornUtils.notEmpty(this.connection.username()) || HartshornUtils.notEmpty(this.connection.password())) {
                this.applicationContext().log().debug("Username or password were configured in the active connection, adding to Hibernate configuration");
                this.configuration.setProperty("hibernate.connection.username", this.connection.username());
                this.configuration.setProperty("hibernate.connection.password", this.connection.password());
            }
            this.configuration.setProperty("hibernate.connection.url", this.connection.url());

            final String driver = this.connection.remote().driver();
            final String dialect = this.dialect(this.connection);

            this.applicationContext().log().debug("Determined driver: %s and dialect: %s".formatted(driver, dialect));

            this.configuration.setProperty("hibernate.hbm2ddl.auto", (String) this.applicationContext().property("hibernate.hbm2ddl.auto").or("update"));
            this.configuration.setProperty("hibernate.connection.driver_class", driver);
            this.configuration.setProperty("hibernate.dialect", dialect);
        }
    }

    protected String dialect(final PersistenceConnection connection) throws ApplicationException {
        final Remote remote = connection.remote();
        if (remote instanceof Remotes remotes) {
            return switch (remotes) {
                case DERBY -> "org.hibernate.dialect.DerbyTenSevenDialect";
                case MYSQL -> "org.hibernate.dialect.MySQL8Dialect";
                case POSTGRESQL -> "org.hibernate.dialect.PostgreSQL95Dialect";
                case MARIADB -> "org.hibernate.dialect.MariaDB103Dialect";
                default -> throw new ApplicationException("Unsupported native remote connection: " + remotes);
            };
        }
        else if (remote instanceof HibernateRemote hibernateRemote) {
            return hibernateRemote.dialect().getCanonicalName();
        }
        throw new ApplicationException("Unexpected remote connection: " + remote);
    }

    @Override
    public void enable() throws ApplicationException {
        this.apply(this.connectionAttribute);

        Exceptional<EntityContext> context = this.applicationContext().first(EntityContext.class);
        if (context.absent()) {
            final Collection<TypeContext<?>> entities = this.applicationContext.environment().types(Entity.class);
            final EntityContext entityContext = new EntityContext(entities);
            this.applicationContext.add(entityContext);
            context = Exceptional.of(entityContext);
        }

        final Collection<TypeContext<?>> entities = context.get().entities();
        for (final TypeContext<?> entity : entities) {
            this.configuration.addAnnotatedClass(entity.type());
        }

        this.configuration.addProperties(this.applicationContext.properties());
        final Map<Object, Object> properties = this.configuration.getProperties();

        // Early validation to ensure our configuration is valid
        if (properties.isEmpty()) throw new ApplicationException("Expected connection to be applied, ensure you provided a ConnectionProperty before enabling.");
        if (!properties.containsKey("hibernate.connection.driver_class")) throw new ApplicationException("No driver class provided! Ensure you provided a valid Remote instance");
        if (!properties.containsKey("hibernate.dialect")) throw new ApplicationException("No dialect class provided! Ensure you provided a valid Remote instance");

        try {
            this.applicationContext().log().debug("Building session factory for Hibernate service #%d".formatted(this.hashCode()));
            this.factory = this.configuration.buildSessionFactory();
        }
        catch (final Throwable e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void save(final Object object) {
        this.performTransactional(session -> session.save(object));
    }

    @Override
    public void update(final Object object) {
        this.performTransactional(session -> session.update(object));
    }

    @Override
    public void updateOrSave(final Object object) {
        this.performTransactional(session -> session.saveOrUpdate(object));
    }

    @Override
    public void delete(final Object object) {
        this.performTransactional(session -> session.delete(object));
    }

    private void performTransactional(final Consumer<Session> action) {
        final Session session = this.session();
        final Transaction transaction = session.beginTransaction();
        action.accept(session);
        transaction.commit();
        this.close();
    }

    @Override
    public Set<T> findAll() {
        final Session session = this.session();
        final CriteriaBuilder builder = session.getCriteriaBuilder();
        final CriteriaQuery<T> criteria = builder.createQuery(this.reify());
        criteria.from(this.reify());
        final List<T> data = session.createQuery(criteria).getResultList();
        return HartshornUtils.asUnmodifiableSet(data);
    }

    @Override
    public Exceptional<T> findById(final ID id) {
        final Session session = this.session();
        return Exceptional.of(session.find(this.reify(), id));
    }

    @Override
    public Class<T> reify() {
        return this.type;
    }

    @Override
    public void flush() {
        if (this.session != null && this.session.isOpen()) {
            this.session.flush();
            this.close();
        }
    }

    @Override
    public void close() {
        if (this.session != null && this.session.isOpen()) {
            this.session.close();
        }
    }

    @Override
    public EntityManager entityManager() {
        return this.session();
    }

    public Session session() {
        if (this.session != null && this.session.isOpen()) return this.session;
        this.applicationContext().log().debug("Opening remote session to %s".formatted(this.connection().url()));
        return this.factory().openSession();
    }
}
