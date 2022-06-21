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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationObject(prefix = "hartshorn.data", singleton = false)
public class HibernateDataSourceConfigurationObject {

    @JsonDeserialize(contentAs = org.dockbox.hartshorn.data.remote.HibernateDataSourceConfiguration.class, as = ConcurrentHashMap.class)
    private Map<String, DataSourceConfiguration> sources = new ConcurrentHashMap<>();

    public Map<String, DataSourceConfiguration> sources() {
        return this.sources;
    }
}
