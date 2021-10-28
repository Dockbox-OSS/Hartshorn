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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.persistence.DefaultSqlService;
import org.dockbox.hartshorn.persistence.SqlService;
import org.dockbox.hartshorn.persistence.context.EntityContext;
import org.dockbox.hartshorn.persistence.properties.ConnectionAttribute;
import org.dockbox.hartshorn.persistence.properties.PersistenceConnection;
import org.dockbox.hartshorn.persistence.properties.Remote;
import org.dockbox.hartshorn.persistence.properties.Remotes;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.inject.Inject;
import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;

@Binds(SqlService.class)
public class HibernateSqlService extends DefaultSqlService<Session> {

    private final Configuration configuration = new Configuration();
    private SessionFactory factory;

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    @Getter(AccessLevel.PROTECTED)
    private PersistenceConnection connection;

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
        this.accept(session -> {
            session.save(object);
        });
    }

    @Override
    public void update(final Object object) {
        this.accept(session -> {
            session.update(object);
        });
    }

    @Override
    public void updateOrSave(final Object object) {
        this.accept(session -> {
            session.saveOrUpdate(object);
        });
    }

    @Override
    public void delete(final Object object) {
        this.accept(session -> {
            session.delete(object);
        });
    }

    @Override
    public Session entityManager() {
        return this.factory.openSession();
    }

    @Override
    public void accept(final Consumer<Session> consumer) {
        this.applicationContext().log().debug("Opening remote session to %s".formatted(this.connection().url()));
        final Session session = this.factory.openSession();
        this.applicationContext().log().debug("Beginning transaction to %s".formatted(this.connection().url()));
        session.beginTransaction();
        consumer.accept(session);
        this.applicationContext().log().debug("Committing transaction to %s".formatted(this.connection().url()));
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public <T> T transform(final Function<Session, T> function) {
        final Session session = this.factory.openSession();
        session.beginTransaction();
        final T result = function.apply(session);
        session.getTransaction().commit();
        session.close();
        return result;
    }
}
