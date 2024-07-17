package org.dockbox.hartshorn.properties.loader;

import java.nio.file.Path;

public interface PredicatePropertyRegistryLoader extends PropertyRegistryLoader {

    boolean isCompatible(Path path);
}
