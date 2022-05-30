package org.dockbox.hartshorn.data.remote;

import org.dockbox.hartshorn.data.annotations.ConfigurationObject;
import org.dockbox.hartshorn.util.Result;

import java.util.Map;

@ConfigurationObject(prefix = "hartshorn.data")
public class DataSourceConfigurations {

    private Map<String, DataSourceConfiguration> sources;

    public DataSourceConfigurations() {
    }

    public Map<String, DataSourceConfiguration> sources() {
        return this.sources;
    }

    public Result<DataSourceConfiguration> source(final String id) {
        return Result.of(this.sources.get(id));
    }
}
