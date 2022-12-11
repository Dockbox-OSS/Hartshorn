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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.jpa.annotations.UsePersistence;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.DataSourceList;
import org.dockbox.hartshorn.jpa.remote.StandardDataSourceList;

/**
 * Default providers for global data sources.
 * @author Guus Lieben
 * @since 22.4
 */
@Service
@RequiresActivator(UsePersistence.class)
public class DataSourceProviders {

    /**
     * The default data source list. This is typically overridden by an ORM-specific
     * implementation.
     */
    @Binds
    public Class<? extends DataSourceList> dataSourceList = StandardDataSourceList.class;

    /**
     * The default data source configuration. This is automatically selected when no
     * explicit data source configuration is provided for a repository.
     *
     * @param sourceList The data source list implementation
     * @return The default data source configuration
     */
    @Binds
    public DataSourceConfiguration defaultConnection(final DataSourceList sourceList) {
        return sourceList.defaultConnection();
    }
}
