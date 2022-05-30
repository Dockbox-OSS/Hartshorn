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
