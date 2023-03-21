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

import org.dockbox.hartshorn.config.annotations.ConfigurationObject;
import org.dockbox.hartshorn.jpa.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.util.introspect.annotations.Property;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationObject(prefix = "hartshorn.data", singleton = false)
public class HibernateDataSourceConfigurationObject {

    @Property(type = ConcurrentHashMap.class, content = HibernateDataSourceConfiguration.class)
    private final Map<String, DataSourceConfiguration> sources = new ConcurrentHashMap<>();

    public Map<String, DataSourceConfiguration> sources() {
        // TODO: Find out why this is deserializing to DataSourceConfiguration instead of HibernateDataSourceConfiguration
        return this.sources;
    }
}
