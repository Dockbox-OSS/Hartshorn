package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.inject.binding.BindingHierarchy;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

public class ConfigurationDependencyVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final HierarchicalComponentProvider componentProvider;

    public ConfigurationDependencyVisitor(final HierarchicalComponentProvider componentProvider) {
        this.componentProvider = componentProvider;
    }

    @Override
    protected boolean visit(final GraphNode<DependencyContext<?>> node) throws GraphException {
        final DependencyContext<?> dependencyContext = node.value();
        this.registerProvider(dependencyContext);
        return true;
    }

    private <T> void registerProvider(final DependencyContext<T> dependencyContext) throws GraphException {
        final Provider<T> provider = dependencyContext.provider();
        if (provider == null) {
            throw new GraphException("Illegal null provider for dependency context " + dependencyContext);
        }
        final BindingHierarchy<T> hierarchy = this.componentProvider.hierarchy(dependencyContext.componentKey());
        final int priority = dependencyContext.priority();
        hierarchy.add(priority, provider);
    }
}
