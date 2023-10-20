package org.dockbox.hartshorn.application.context.validate;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CompositeDependencyGraphValidator implements DependencyGraphValidator {

    private final Set<DependencyGraphValidator> validators = new HashSet<>();

    public CompositeDependencyGraphValidator(Collection<DependencyGraphValidator> validators) {
        this.validators.addAll(validators);
    }

    public void add(DependencyGraphValidator validator) {
        this.validators.add(validator);
    }

    @Override
    public void validateBeforeConfiguration(DependencyGraph dependencyGraph, ApplicationContext applicationContext) throws ApplicationException {
        for (DependencyGraphValidator validator : this.validators) {
            validator.validateBeforeConfiguration(dependencyGraph, applicationContext);
        }
    }

    @Override
    public void validateAfterConfiguration(DependencyGraph dependencyGraph, ApplicationContext applicationContext, Set<GraphNode<DependencyContext<?>>> visited) throws ApplicationException {
        for (DependencyGraphValidator validator : this.validators) {
            validator.validateAfterConfiguration(dependencyGraph, applicationContext, visited);
        }
    }
}
