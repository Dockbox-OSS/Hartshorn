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
