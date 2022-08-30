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
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.data.InvalidConnectionException;
import org.dockbox.hartshorn.data.config.PropertyHolder;
import org.dockbox.hartshorn.data.context.EntityContext;
import org.dockbox.hartshorn.data.jpa.EntityManagerCarrier;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.DataSourceList;
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

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;

@Component
public class HibernateEntityManagerCarrier implements EntityManagerCarrier, ContextCarrier {

    private final Configuration hibernateConfiguration = new Configuration();

    private SessionFactory factory;
    private DataSourceConfiguration configuration;
    private Session session;

    @Inject
    private ApplicationContext applicationContext;

    @Inject
    public HibernateEntityManagerCarrier() {
        this(null);
    }

    @Bound
    public HibernateEntityManagerCarrier(final DataSourceConfiguration configuration) {
        this.configuration = configuration;
    }

    public DataSourceConfiguration configuration() {
        return this.configuration;
    }

    @Override
    public HibernateEntityManagerCarrier configuration(final DataSourceConfiguration connection) {
        if (this.configuration != null) {
            throw new IllegalArgumentException("Configuration already set");
        }
        this.configuration = connection;
        return this;
    }

    public SessionFactory factory() {
        return this.factory;
    }

    public void flush() {
        if (this.session != null && this.session.isOpen()) {
            final Transaction transaction = this.manager().getTransaction();
            if (transaction.isActive()) transaction.commit();
            this.close();
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

        final DataSourceConfiguration connection = this.configuration();
        if (connection == null) throw new InvalidConnectionException("Connection has not been configured!");

        this.applicationContext().log().debug("Opening remote session to %s".formatted(connection.url()));
        this.session = this.factory().openSession();

        return this.session;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }

    @Override
    public void close() {
        if (this.session != null && this.session.isOpen()) {
            this.session.close();
            this.session = null;
        }
    }

    @PostConstruct
    public void enable() throws ApplicationException {
        if (this.factory == null) {
            if (this.isInvalidConnection()) {
                this.applicationContext().log().debug("No (valid) connection was set for JPA repository instance, using default component instead.");
                this.configuration = this.applicationContext().get(DataSourceList.class).defaultConnection();

                if (this.isInvalidConnection()) {
                    throw new ApplicationException("No (valid) default connection was configured");
                }
            }

            this.prepareProperties();
            try {
                this.applicationContext().log().debug("Building session factory for Hibernate service #%d".formatted(this.hashCode()));
                this.factory = this.hibernateConfiguration.buildSessionFactory();
            }
            catch (final Throwable e) {
                throw new ApplicationException(e);
            }
        }
    }

    protected String dialect() throws ApplicationException {
        if (this.configuration instanceof HibernateDataSourceConfiguration hibernateConfiguration) {
            return hibernateConfiguration.dialect();
        }

        return this.applicationContext()
                .property("hartshorn.data.hibernate.dialect")
                .orThrow(() -> new ApplicationException("No default dialect was configured"));
    }

    protected boolean isInvalidConnection() {
        if (this.configuration != null) {
            if (this.configuration instanceof HibernateDataSourceConfiguration hibernateConfiguration && hibernateConfiguration.dialect() == null) {
                return true;
            }
            // Username/password can be null, only check required properties
            return this.configuration.url() == null || this.configuration.driver() == null;
        }
        return true;
    }

    protected void prepareProperties() throws ApplicationException {
        if (StringUtilities.notEmpty(this.configuration.username()) || StringUtilities.notEmpty(this.configuration.password())) {
            this.applicationContext().log().debug("Username or password were configured in the active connection, adding to Hibernate configuration");
            this.hibernateConfiguration.setProperty("hibernate.connection.username", this.configuration.username());
            this.hibernateConfiguration.setProperty("hibernate.connection.password", this.configuration.password());
        }
        this.hibernateConfiguration.setProperty("hibernate.connection.url", this.configuration.url());

        final Class<? extends Driver> driver = this.configuration.driver();
        final String dialect = this.dialect();

        this.applicationContext().log().debug("Determined driver: %s and dialect: %s".formatted(driver.getCanonicalName(), dialect));

        final PropertyHolder propertyHolder = this.applicationContext().get(PropertyHolder.class);
        this.hibernateConfiguration.setProperty("hibernate.hbm2ddl.auto", (String) propertyHolder.get("hibernate.hbm2ddl.auto").or("update"));
        this.hibernateConfiguration.setProperty("hibernate.connection.driver_class", driver.getCanonicalName());
        this.hibernateConfiguration.setProperty("hibernate.dialect", dialect);

        Result<EntityContext> context = this.applicationContext().first(EntityContext.class);
        if (context.absent()) {
            final Collection<TypeContext<?>> entities = this.applicationContext.environment().types(Entity.class);
            final EntityContext entityContext = new EntityContext(entities);
            this.applicationContext.add(entityContext);
            context = Result.of(entityContext);
        }

        final Collection<TypeContext<?>> entities = context.get().entities();
        for (final TypeContext<?> entity : entities) {
            this.hibernateConfiguration.addAnnotatedClass(entity.type());
        }

        // Ensure that the user-defined properties take precedence over the defaults
        this.hibernateConfiguration.addProperties(propertyHolder.properties());
    }
}
