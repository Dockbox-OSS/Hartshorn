package org.dockbox.hartshorn.launchpad.properties;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.dockbox.hartshorn.properties.PropertyRegistry;

@FunctionalInterface
public interface PropertyRegistryFactory {

    PropertyRegistry createRegistry(Set<URI> sources) throws IOException;
}
