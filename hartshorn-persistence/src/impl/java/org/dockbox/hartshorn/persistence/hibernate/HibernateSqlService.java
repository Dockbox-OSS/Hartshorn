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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.annotations.inject.Binds;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.Attribute;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.context.EntityContext;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;
import org.dockbox.hartshorn.persistence.properties.Remote;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.Entity;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import lombok.Getter;

@Binds(SqlService.class)
public class HibernateSqlService implements SqlService {

    private final Configuration configuration = new Configuration();
    private SessionFactory factory;

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Override
    public boolean canEnable() {
        return this.factory == null;
    }

    @Override
    public void apply(final Attribute<?> property) throws ApplicationException {
        if (property instanceof ConnectionAttribute connectionAttribute) {
            final PersistenceConnection connection = connectionAttribute.value();
            if (HartshornUtils.notEmpty(connection.username()) || HartshornUtils.notEmpty(connection.password())) {
                this.configuration.setProperty("hibernate.connection.username", connection.username());
                this.configuration.setProperty("hibernate.connection.password", connection.password());
            }
            this.configuration.setProperty("hibernate.connection.url", connection.url());

            final String driver = connection.remote().driver();
            final String dialect = this.dialect(connection);

            this.configuration.setProperty("hibernate.hbm2ddl.auto", "update");
            this.configuration.setProperty("hibernate.connection.driver_class", driver);
            this.configuration.setProperty("hibernate.dialect", dialect);
        }
    }

    protected String dialect(final PersistenceConnection connection) throws ApplicationException {
        Remote remote = connection.remote();
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
        Exceptional<EntityContext> context = this.applicationContext().first(EntityContext.class);
        if (context.absent()) {
            final Collection<TypeContext<?>> entities = this.applicationContext.environment().types(Entity.class);
            EntityContext entityContext = new EntityContext(entities);
            this.applicationContext.add(entityContext);
            context = Exceptional.of(entityContext);
        }

        final Collection<TypeContext<?>> entities = context.get().entities();
        for (final TypeContext<?> entity : entities) {
            this.configuration.addAnnotatedClass(entity.type());
        }

        final Map<Object, Object> properties = this.configuration.getProperties();

        // Early validation to ensure our configuration is valid
        if (properties.isEmpty()) throw new ApplicationException("Expected connection to be applied, ensure you provided a ConnectionProperty before enabling.");
        if (!properties.containsKey("hibernate.connection.driver_class")) throw new ApplicationException("No driver class provided! Ensure you provided a valid Remote instance");
        if (!properties.containsKey("hibernate.dialect")) throw new ApplicationException("No dialect class provided! Ensure you provided a valid Remote instance");

        try {
            this.factory = this.configuration.buildSessionFactory();
        }
        catch (final Throwable e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public void save(final Object object) {
        this.session(session -> {
            session.save(object);
        });
    }

    private void session(final Consumer<Session> consumer) {
        final Session session = this.factory.openSession();
        session.beginTransaction();
        consumer.accept(session);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(final Object object) {
        this.session(session -> {
            session.update(object);
        });
    }

    @Override
    public void updateOrSave(final Object object) {
        this.session(session -> {
            session.saveOrUpdate(object);
        });
    }

    @Override
    public void delete(final Object object) {
        this.session(session -> {
            session.delete(object);
        });
    }

    @Override
    public <T> Set<T> findAll(final Class<T> type) {
        return this.session(session -> {
            final CriteriaBuilder builder = session.getCriteriaBuilder();
            final CriteriaQuery<T> criteria = builder.createQuery(type);
            criteria.from(type);
            final List<T> data = session.createQuery(criteria).getResultList();
            return HartshornUtils.asUnmodifiableSet(data);
        });
    }

    private <T> T session(final Function<Session, T> function) {
        final Session session = this.factory.openSession();
        session.beginTransaction();
        final T result = function.apply(session);
        session.getTransaction().commit();
        session.close();
        return result;
    }

    @Override
    public <T> Exceptional<T> findById(final Class<T> type, final Object id) {
        return this.session(session -> {
            return Exceptional.of(() -> session.find(type, id));
        });
    }
}
