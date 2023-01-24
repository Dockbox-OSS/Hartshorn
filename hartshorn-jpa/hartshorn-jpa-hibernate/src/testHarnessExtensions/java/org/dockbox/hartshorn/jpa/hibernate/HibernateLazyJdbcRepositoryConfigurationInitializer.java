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

import com.mysql.cj.jdbc.Driver;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.hibernate.dialect.MySQL8Dialect;
import org.testcontainers.containers.JdbcDatabaseContainer;

import test.org.dockbox.hartshorn.jpa.StandardLazyJdbcRepositoryConfigurationInitializer;

public class HibernateLazyJdbcRepositoryConfigurationInitializer extends StandardLazyJdbcRepositoryConfigurationInitializer {

    public HibernateLazyJdbcRepositoryConfigurationInitializer(final ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void doConfigure(final JdbcDatabaseContainer<?> container, final int mappedPort, final PropertyHolder propertyHolder) {
        super.doConfigure(container, mappedPort, propertyHolder);

        // Hibernate specific
        propertyHolder.set("hartshorn.data.sources.default.dialect", MySQL8Dialect.class.getCanonicalName());
        propertyHolder.set("hartshorn.data.sources.default.driver", Driver.class.getCanonicalName());
    }
}
