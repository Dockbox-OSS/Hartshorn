package org.dockbox.hartshorn.launchpad.properties;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dockbox.hartshorn.inject.component.ComponentContainer;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;

public class TypeDiscoveryPropertySourceResolver implements PropertySourceResolver {

    private final ApplicationEnvironment environment;

    public TypeDiscoveryPropertySourceResolver(ApplicationEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Set<String> resolve() {
        Collection<ComponentContainer<?>> containers = environment.componentRegistry().containers();
        return containers.stream()
                .map(ComponentContainer::type)
                .flatMap(type -> type.annotations().get(PropertiesSource.class).stream())
                .flatMap(source -> Stream.of(source.value()))
                .collect(Collectors.toSet());
    }
}
