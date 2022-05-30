package org.dockbox.hartshorn.data.remote;

import org.hibernate.dialect.Dialect;

import java.sql.Driver;

public class HibernateDataSourceConfiguration extends DataSourceConfiguration {

    private String dialect;

    public HibernateDataSourceConfiguration() {
        // For configuration
    }

    public HibernateDataSourceConfiguration(final String url, final Class<? extends Driver> driver, final Class<? extends Dialect> dialect) {
        this(url, driver, dialect.getCanonicalName());
    }

    public HibernateDataSourceConfiguration(final String url, final String username, final String password, final Class<? extends Driver> driver, final Class<? extends Dialect> dialect) {
        this(url, username, password, driver, dialect.getCanonicalName());
    }

    public HibernateDataSourceConfiguration(final String url, final Class<? extends Driver> driver, final String dialect) {
        super(url, driver);
        this.dialect = dialect;
    }

    public HibernateDataSourceConfiguration(final String url, final String username, final String password, final Class<? extends Driver> driver, final String dialect) {
        super(url, username, password, driver);
        this.dialect = dialect;
    }

    public String dialect() {
        return this.dialect;
    }
}
