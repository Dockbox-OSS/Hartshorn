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
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.jpa.remote.RefreshableDataSourceList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.PostConstruct;

public class HibernateDataSourceList implements RefreshableDataSourceList {

    private final Map<String, DataSourceConfiguration> configurations = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    private HibernateDataSourceConfigurationObject sources;

    public HibernateDataSourceList(final ApplicationContext applicationContext, final HibernateDataSourceConfigurationObject sources) {
        this.applicationContext = applicationContext;
        this.sources = sources;
    }

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

    @PostConstruct
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
