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

package org.dockbox.hartshorn.jpa.hibernate;

import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
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
