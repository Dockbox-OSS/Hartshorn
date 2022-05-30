package org.dockbox.hartshorn.data.remote;

import org.dockbox.hartshorn.data.annotations.ConfigurationObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationObject(prefix = "hartshorn.data")
public class StandardDataSourceList implements DataSourceList {

    private Map<String, DataSourceConfiguration> sources = new ConcurrentHashMap<>();

    @Override
    public void add(final String id, final DataSourceConfiguration configuration) {
        this.sources.put(id, configuration);
    }

    @Override
    public Map<String, DataSourceConfiguration> sources() {
        return this.sources;
    }

    @Override
    public DataSourceConfiguration get(final String id) {
        return this.sources.get(id);
    }

    @Override
    public DataSourceConfiguration defaultConnection() {
        return this.get("default");
    }
}
