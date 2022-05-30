package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Provider;
import org.dockbox.hartshorn.data.annotations.UsePersistence;
import org.dockbox.hartshorn.data.remote.DataSourceConfiguration;
import org.dockbox.hartshorn.data.remote.DataSourceList;
import org.dockbox.hartshorn.data.remote.StandardDataSourceList;

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
    @Provider
    public Class<? extends DataSourceList> dataSourceList() {
        return StandardDataSourceList.class;
    }

    /**
     * The default data source configuration. This is automatically selected when no
     * explicit data source configuration is provided for a repository.
     *
     * @param sourceList The data source list implementation
     * @return The default data source configuration
     */
    @Provider
    public DataSourceConfiguration defaultConnection(final DataSourceList sourceList) {
        return sourceList.defaultConnection();
    }
}
