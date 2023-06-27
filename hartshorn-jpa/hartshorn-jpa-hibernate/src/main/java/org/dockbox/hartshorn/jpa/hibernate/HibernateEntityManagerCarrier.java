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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.context.ContextKey;
import org.dockbox.hartshorn.jpa.entitymanager.EntityContext;
import org.dockbox.hartshorn.jpa.entitymanager.EntityManagerCarrier;
import org.dockbox.hartshorn.jpa.entitymanager.InvalidConnectionException;
import org.dockbox.hartshorn.jpa.query.NamedQueryRegistry;
import org.dockbox.hartshorn.jpa.query.QueryComponentFactory;
import org.dockbox.hartshorn.jpa.query.context.application.ApplicationNamedQueriesContext;
import org.dockbox.hartshorn.jpa.query.context.application.ComponentNamedQueryContext;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Component
public class HibernateEntityManagerCarrier implements EntityManagerCarrier, ContextCarrier {

    public static final String HB_USERNAME = "hibernate.connection.username";
    public static final String HB_PASSWORD = "hibernate.connection.password";
    public static final String HB_URL = "hibernate.connection.url";
    public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
    public static final String HB_DRIVER_CLASS = "hibernate.connection.driver_class";
    public static final String HB_DIALECT = "hibernate.dialect";

    public static final String HH_HIBERNATE_DIALECT = "hartshorn.data.hibernate.dialect";

    private final Configuration hibernateConfiguration = new Configuration();
    private final ApplicationContext applicationContext;

    private SessionFactory factory;
    private DataSourceConfiguration configuration;
    private Session session;

    @Inject
    public HibernateEntityManagerCarrier(final ApplicationContext applicationContext) {
        this(applicationContext, null);
    }

    public HibernateEntityManagerCarrier(final ApplicationContext applicationContext, final DataSourceConfiguration configuration) {
        this.applicationContext = applicationContext;
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
                throw new ApplicationRuntimeException("Failed to enable entity manager carrier", e);
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
                this.factoryPostConstruct();
            }
            catch (final HibernateException e) {
                throw new ApplicationException(e);
            }
        }
    }

    private void factoryPostConstruct() {
        final ApplicationNamedQueriesContext queriesContext = this.applicationContext().first(ApplicationNamedQueriesContext.class).get();
        final Map<String, ComponentNamedQueryContext> queries = queriesContext.namedQueries();
        if (queries.isEmpty()) return;

        final QueryComponentFactory registryFactory = this.applicationContext().get(QueryComponentFactory.class);
        final NamedQueryRegistry registry = registryFactory.queryRegistry(this.factory);

        // Safe to auto-close as this will be invoked before the carrier is exposed to external components. It is
        // preferred to close the EM as soon as possible here, as it is not needed again until a component requests it.
        try (final EntityManager entityManager = this.manager()) {
            queries.forEach((name, context) -> {
                if (registry.has(name)) {
                    // If an entity is already registered with the same name, skip it, this is valid behaviour as this
                    // is usually handled by the backing JPA implementation.
                    if (context.isEntityDeclaration()) return;
                    // If a query is already registered with the same name, but not by an entity, yield a warning, but
                    // proceed with the registration
                    this.applicationContext().log().warn("A query with the name %s is already registered, overwriting it".formatted(name));
                }

                final Query query = context.nativeQuery()
                        ? entityManager.createNativeQuery(context.query())
                        : entityManager.createQuery(context.query());
                registry.register(name, query);
            });
        }
    }

    protected String dialect() throws ApplicationException {
        if (this.configuration instanceof HibernateDataSourceConfiguration hibernateConfiguration) {
            return hibernateConfiguration.dialect();
        }

        return this.applicationContext()
                .property(HH_HIBERNATE_DIALECT)
                .orElseThrow(() -> new ApplicationException("No default dialect was configured"));
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
            this.hibernateConfiguration.setProperty(HB_USERNAME, this.configuration.username());
            this.hibernateConfiguration.setProperty(HB_PASSWORD, this.configuration.password());
        }
        this.hibernateConfiguration.setProperty(HB_URL, this.configuration.url());

        final Class<? extends Driver> driver = this.configuration.driver();
        final String dialect = this.dialect();

        this.applicationContext().log().debug("Determined driver: %s and dialect: %s".formatted(driver.getCanonicalName(), dialect));

        final PropertyHolder propertyHolder = this.applicationContext().get(PropertyHolder.class);
        this.hibernateConfiguration.setProperty(HBM2DDL_AUTO, (String) propertyHolder.get(HBM2DDL_AUTO)
                .orElse("update"));
        this.hibernateConfiguration.setProperty(HB_DRIVER_CLASS, driver.getCanonicalName());
        this.hibernateConfiguration.setProperty(HB_DIALECT, dialect);

        final List<EntityContext> entityContexts = new ArrayList<>(this.applicationContext().all(ContextKey.of(EntityContext.class)));
        if (entityContexts.isEmpty()) {
            final Collection<TypeView<?>> entities = this.applicationContext.environment().types(Entity.class);
            final EntityContext entityContext = new EntityContext(entities);
            this.applicationContext.add(entityContext);
            entityContexts.add(entityContext);
        }

        final Collection<TypeView<?>> entities = entityContexts.stream()
                .flatMap(context -> context.entities().stream())
                .collect(Collectors.toSet());

        for (final TypeView<?> entity : entities) {
            this.hibernateConfiguration.addAnnotatedClass(entity.type());
        }

        // Ensure that the user-defined properties take precedence over the defaults
        this.hibernateConfiguration.addProperties(propertyHolder.properties());
    }
}
