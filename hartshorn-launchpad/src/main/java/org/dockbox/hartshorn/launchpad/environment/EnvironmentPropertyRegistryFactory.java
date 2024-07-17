package org.dockbox.hartshorn.launchpad.environment;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.inject.graph.support.ComponentInitializationException;
import org.dockbox.hartshorn.launchpad.properties.InstantLoadingPropertyRegistryFactory;
import org.dockbox.hartshorn.launchpad.properties.PropertyRegistryFactory;
import org.dockbox.hartshorn.launchpad.properties.PropertySourceResolver;
import org.dockbox.hartshorn.launchpad.resources.ResourceLookup;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.properties.loader.PredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.PropertyRegistryLoader;
import org.dockbox.hartshorn.properties.loader.support.CompositePredicatePropertyRegistryLoader;
import org.dockbox.hartshorn.spi.DiscoveryService;
import org.dockbox.hartshorn.spi.ServiceDiscoveryException;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.CollectionUtilities;

public class EnvironmentPropertyRegistryFactory {

    public PropertyRegistry createRegistry(List<PropertySourceResolver> propertySourceResolvers, ResourceLookup resourceLookup) {
        Set<PropertyRegistryLoader> propertyRegistryLoaders = resolveRegistryLoaders();
        PropertyRegistryLoader propertyRegistryLoader = createRegistryLoader(propertyRegistryLoaders);
        PropertyRegistryFactory propertyRegistryFactory = new InstantLoadingPropertyRegistryFactory(propertyRegistryLoader);
        try {
            Set<URI> resources = resolveResources(propertySourceResolvers, resourceLookup);
            return propertyRegistryFactory.createRegistry(resources);
        }
        catch(IOException e) {
            throw new ComponentInitializationException("Could not initialize PropertyRegistry", e);
        }
    }

    private Set<URI> resolveResources(List<PropertySourceResolver> propertySourceResolvers, ResourceLookup resourceLookup) {
        Set<String> sources = propertySourceResolvers.stream()
                .flatMap(resolver -> resolver.resolve().stream())
                .collect(Collectors.toSet());
        return sources.stream()
                .flatMap(source -> resourceLookup.lookup(source).stream())
                .collect(Collectors.toSet());
    }

    private Set<PropertyRegistryLoader> resolveRegistryLoaders() {
        Set<PropertyRegistryLoader> propertyRegistryLoaders;
        try {
            propertyRegistryLoaders = DiscoveryService.instance().discoverAll(PropertyRegistryLoader.class);
        }
        catch(ServiceDiscoveryException e) {
            throw new ComponentInitializationException("Failed to initialize PropertyRegistryLoaders", e);
        }
        return propertyRegistryLoaders;
    }

    private PropertyRegistryLoader createRegistryLoader(Set<PropertyRegistryLoader> propertyRegistryLoaders) {
        PropertyRegistryLoader propertyRegistryLoader;
        if (propertyRegistryLoaders.size() == 1) {
            propertyRegistryLoader = CollectionUtilities.first(propertyRegistryLoaders);
        }
        else {
            CompositePredicatePropertyRegistryLoader composite = new CompositePredicatePropertyRegistryLoader();
            for(PropertyRegistryLoader registryLoader : propertyRegistryLoaders) {
                if (registryLoader instanceof PredicatePropertyRegistryLoader predicateLoader) {
                    composite.addLoader(predicateLoader);
                }
                else {
                    throw new ApplicationRuntimeException("Found multiple PropertyRegistryLoaders, but cannot differentiate between them. "
                            + "Please implement PredicatePropertyRegistryLoader for " + registryLoader.getClass().getName());
                }
            }
            propertyRegistryLoader = composite;
        }
        return propertyRegistryLoader;
    }
}
