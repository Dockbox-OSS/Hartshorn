package org.dockbox.hartshorn.launchpad.properties;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Set;

import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.PropertyRegistryLoader;

public class InstantLoadingPropertyRegistryFactory implements PropertyRegistryFactory {

    private final PropertyRegistryLoader propertyRegistryLoader;

    public InstantLoadingPropertyRegistryFactory(PropertyRegistryLoader propertyRegistryLoader) {
        this.propertyRegistryLoader = propertyRegistryLoader;
    }

    protected PropertyRegistry createRegistry() {
        return new MapPropertyRegistry();
    }

    @Override
    public PropertyRegistry createRegistry(Set<URI> sources) throws IOException {
        PropertyRegistry propertyRegistry = this.createRegistry();
        for(URI resource : sources) {
            propertyRegistryLoader.loadRegistry(propertyRegistry, Path.of(resource));
        }
        return propertyRegistry;
    }
}
