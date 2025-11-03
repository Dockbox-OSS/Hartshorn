package org.dockbox.hartshorn.launchpad.properties;

import org.dockbox.hartshorn.properties.PropertyRegistry;

/**
 * Factory interface for creating instances of {@link PropertyRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface PropertyRegistryFactory {

    /**
     * Creates a new {@link PropertyRegistry}.
     *
     * @return a new {@link PropertyRegistry} instance
     */
    PropertyRegistry createRegistry();
}
