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

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Enableable;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.context.EntityContext;
import org.dockbox.hartshorn.data.jpa.EntityManagerJpaRepository;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.DataSourceList;
import org.dockbox.hartshorn.data.remote.HibernateDataSourceConfiguration;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Driver;
import java.util.Collection;

import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;

public class HibernateJpaRepository<T, ID> extends EntityManagerJpaRepository<T, ID> implements Enableable {

    private final Configuration configuration = new Configuration();

    private SessionFactory factory;
    private DataSourceConfiguration connection;
    private Session session;

    @Inject
    private ApplicationContext applicationContext;

    @Bound
    public HibernateJpaRepository(final Class<T> type) {
        super(type);
    }

    @Bound
    public HibernateJpaRepository(final Class<T> type, final DataSourceConfiguration connection) {
        super(type);
        this.connection = connection;
    }

    protected SessionFactory factory() {
        return this.factory;
    }

    protected DataSourceConfiguration connection() {
        return this.connection;
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public boolean canEnable() {
        return this.factory == null;
    }

    protected String dialect() throws ApplicationException {
        if (this.connection instanceof HibernateDataSourceConfiguration hibernateConfiguration) {
            return hibernateConfiguration.dialect();
        }

        return this.applicationContext()
                .property("hartshorn.data.hibernate.dialect")
                .orThrow(() -> new ApplicationException("No default dialect was configured"));
    }

    @Override
    public void enable() throws ApplicationException {
        if (!this.hasValidConnection()) {
            this.applicationContext().log().debug("No (valid) connection was set for JPA repository instance, using default component instead.");
            this.connection = this.applicationContext().get(DataSourceList.class).defaultConnection();

            if (!this.hasValidConnection()) {
                throw new ApplicationException("No (valid) default connection was configured");
            }
        }

        this.prepareProperties();
        try {
            this.applicationContext().log().debug("Building session factory for Hibernate service #%d".formatted(this.hashCode()));
            this.factory = this.configuration.buildSessionFactory();
        }
        catch (final Throwable e) {
            throw new ApplicationException(e);
        }
    }

    protected boolean hasValidConnection() {
        if (this.connection != null) {
            if (this.connection instanceof HibernateDataSourceConfiguration hibernateConfiguration && hibernateConfiguration.dialect() == null) {
                return false;
            }
            // Username/password can be null, only check required properties
            return this.connection.url() != null && this.connection.driver() != null;
        }
        return false;
    }

    protected void prepareProperties() throws ApplicationException {
        if (StringUtilities.notEmpty(this.connection.username()) || StringUtilities.notEmpty(this.connection.password())) {
            this.applicationContext().log().debug("Username or password were configured in the active connection, adding to Hibernate configuration");
            this.configuration.setProperty("hibernate.connection.username", this.connection.username());
            this.configuration.setProperty("hibernate.connection.password", this.connection.password());
        }
        this.configuration.setProperty("hibernate.connection.url", this.connection.url());

        final Class<? extends Driver> driver = this.connection.driver();
        final String dialect = this.dialect();

        this.applicationContext().log().debug("Determined driver: %s and dialect: %s".formatted(driver.getCanonicalName(), dialect));

        final PropertyHolder propertyHolder = this.applicationContext().get(PropertyHolder.class);
        this.configuration.setProperty("hibernate.hbm2ddl.auto", (String) propertyHolder.get("hibernate.hbm2ddl.auto").or("update"));
        this.configuration.setProperty("hibernate.connection.driver_class", driver.getCanonicalName());
        this.configuration.setProperty("hibernate.dialect", dialect);

        Result<EntityContext> context = this.applicationContext().first(EntityContext.class);
        if (context.absent()) {
            final Collection<TypeContext<?>> entities = this.applicationContext.environment().types(Entity.class);
            final EntityContext entityContext = new EntityContext(entities);
            this.applicationContext.add(entityContext);
            context = Result.of(entityContext);
        }

        final Collection<TypeContext<?>> entities = context.get().entities();
        for (final TypeContext<?> entity : entities) {
            this.configuration.addAnnotatedClass(entity.type());
        }

        // Ensure that the user-defined properties take precedence over the defaults
        this.configuration.addProperties(propertyHolder.properties());
    }

    @Override
    public void flush() {
        if (this.session != null && this.session.isOpen()) {
            this.session.flush();
            final Transaction transaction = this.manager().getTransaction();
            if (transaction.isActive()) transaction.commit();
            this.close();
        }
    }

    @Override
    public void close() {
        if (this.session != null && this.session.isOpen()) {
            this.session.close();
            this.session = null;
        }
    }

    @Override
    public Session manager() {
        if (this.session != null && this.session.isOpen()) return this.session;

        // If the factory was not constructed at this point, the connection was not configured. This indicates the
        // repository should fall back to constructing the default remote through the active HibernateRemote binding.
        if (this.factory == null) {
            try {
                this.enable();
            }
            catch (final ApplicationException e) {
                return ExceptionHandler.unchecked(e);
            }
        }

        final DataSourceConfiguration connection = this.connection();
        if (connection == null) throw new IllegalStateException("Connection has not been configured!");

        this.applicationContext().log().debug("Opening remote session to %s".formatted(connection.url()));
        this.session = this.factory().openSession();

        return this.session;
    }

    @Override
    protected Transaction transaction(final EntityManager manager) {
        return this.manager().beginTransaction();
    }
}
