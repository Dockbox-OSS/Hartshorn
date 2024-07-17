package org.dockbox.hartshorn.properties.loader.support;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;

public class CompositePredicatePropertyRegistryLoader implements PredicatePropertyRegistryLoader {

    private final Set<PredicatePropertyRegistryLoader> loaders = new HashSet<>();

    public void addLoader(PredicatePropertyRegistryLoader loader) {
        this.loaders.add(loader);
    }

    public void removeLoader(PredicatePropertyRegistryLoader loader) {
        this.loaders.remove(loader);
    }

    public Set<PredicatePropertyRegistryLoader> loaders() {
        return this.loaders;
    }

    @Override
    public boolean isCompatible(Path path) {
        return this.loaders.stream().anyMatch(loader -> loader.isCompatible(path));
    }

    @Override
    public void loadRegistry(PropertyRegistry registry, Path path) throws IOException {
        for(PredicatePropertyRegistryLoader loader : this.loaders) {
            if(loader.isCompatible(path)) {
                loader.loadRegistry(registry, path);
            }
        }
    }
}
