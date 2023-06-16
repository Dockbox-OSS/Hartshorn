package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CompositeDependencyResolver implements DependencyResolver {

    private final Set<DependencyResolver> resolvers;

    public CompositeDependencyResolver(final Set<DependencyResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Set<DependencyContext<?>> resolve(final Collection<ComponentContainer> containers, final ApplicationContext applicationContext) {
        return this.resolvers.stream()
                .map(resolver -> resolver.resolve(containers, applicationContext))
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
