package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.BindingFunction;
import org.dockbox.hartshorn.util.graph.BreadthFirstGraphVisitor;
import org.dockbox.hartshorn.util.graph.GraphException;
import org.dockbox.hartshorn.util.graph.GraphNode;

public class ConfigurationDependencyVisitor extends BreadthFirstGraphVisitor<DependencyContext<?>> {

    private final Binder binder;

    public ConfigurationDependencyVisitor(final Binder binder) {
        this.binder = binder;
    }

    @Override
    protected boolean visit(final GraphNode<DependencyContext<?>> node) throws GraphException {
        final DependencyContext<?> dependencyContext = node.value();
        try {
            this.registerProvider(dependencyContext);
            return true;
        }
        catch (final ComponentConfigurationException e) {
            throw new GraphException(e);
        }
    }

    private <T> void registerProvider(final DependencyContext<T> dependencyContext) throws ComponentConfigurationException {
        final BindingFunction<T> function = this.binder.bind(dependencyContext.componentKey());
        dependencyContext.configure(function);
    }
}
