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

package test.org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.config.properties.PropertyHolder;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.jpa.remote.RefreshableDataSourceList;
import org.testcontainers.containers.JdbcDatabaseContainer;

public class StandardLazyJdbcRepositoryInitializer implements LazyJdbcRepositoryInitializer {

    private final ApplicationContext applicationContext;

    public StandardLazyJdbcRepositoryInitializer(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize(final JdbcDatabaseContainer<?> container, final int mappedPort) {
        final PropertyHolder propertyHolder = this.applicationContext.get(PropertyHolder.class);
        this.doConfigure(container, mappedPort, propertyHolder);
        this.afterConfiguration();
    }

    protected void doConfigure(final JdbcDatabaseContainer<?> container, final int mappedPort, final PropertyHolder propertyHolder) {
        // Data API specific
        propertyHolder.set("hartshorn.data.sources.default.username", container.getUsername());
        propertyHolder.set("hartshorn.data.sources.default.password", container.getPassword());

        final String connectionUrl = "jdbc:mysql://%s:%s/%s".formatted(container.getHost(), container.getMappedPort(mappedPort), TestContractProviders.DEFAULT_DATABASE);
        propertyHolder.set("hartshorn.data.sources.default.url", connectionUrl);
    }

    protected void afterConfiguration() {
        final DataSourceList dataSourceList = this.applicationContext.get(DataSourceList.class);
        if (dataSourceList instanceof RefreshableDataSourceList refreshable) {
            refreshable.refresh();
        }
    }
}
