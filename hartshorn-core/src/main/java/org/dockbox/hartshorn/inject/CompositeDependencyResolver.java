package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentContainer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CompositeDependencyResolver implements DependencyResolver {

    private final Set<DependencyResolver> resolvers;

    public CompositeDependencyResolver(final Set<DependencyResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Set<DependencyContext<?>> resolve(final Collection<ComponentContainer> containers, final ApplicationContext applicationContext) throws DependencyResolutionException {
        final Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        for (final DependencyResolver resolver : this.resolvers) {
            final Set<DependencyContext<?>> resolvedDependencies = resolver.resolve(containers, applicationContext);
            dependencyContexts.addAll(resolvedDependencies);
        }
        return dependencyContexts;
    }
}
