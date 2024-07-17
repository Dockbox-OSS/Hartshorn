package org.dockbox.hartshorn.properties.loader;

import java.io.IOException;
import java.nio.file.Path;

import org.dockbox.hartshorn.properties.PropertyRegistry;

@FunctionalInterface
public interface PropertyRegistryLoader {

    void loadRegistry(PropertyRegistry registry, Path path) throws IOException;
}
