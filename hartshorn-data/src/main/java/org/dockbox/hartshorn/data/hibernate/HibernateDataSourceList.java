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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.Enableable;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.RefreshableDataSourceList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.inject.Inject;

@Component(singleton = true)
public class HibernateDataSourceList implements RefreshableDataSourceList, Enableable {

    @Inject
    private HibernateDataSourceConfigurationObject sources;
    @Inject
    private ApplicationContext applicationContext;
    private final Map<String, DataSourceConfiguration> configurations = new ConcurrentHashMap<>();

    @Override
    public void add(final String id, final DataSourceConfiguration configuration) {
        this.configurations.put(id, configuration);
    }

    @Override
    public Map<String, DataSourceConfiguration> sources() {
        return this.configurations;
    }

    @Override
    public DataSourceConfiguration get(final String id) {
        return this.configurations.get(id);
    }

    @Override
    public DataSourceConfiguration defaultConnection() {
        return this.get("default");
    }

    @Override
    public void enable() {
        this.configurations.clear();
        this.configurations.putAll(this.sources.sources());
    }

    @Override
    public void refresh() {
        this.sources = this.applicationContext.get(HibernateDataSourceConfigurationObject.class);
        this.configurations.putAll(this.sources.sources());
    }
}
