package org.dockbox.hartshorn.data.remote;

import java.util.Map;

public interface DataSourceList {
    void add(String id, DataSourceConfiguration configuration);
    DataSourceConfiguration get(String id);
    DataSourceConfiguration defaultConnection();
    Map<String, DataSourceConfiguration> sources();
}
