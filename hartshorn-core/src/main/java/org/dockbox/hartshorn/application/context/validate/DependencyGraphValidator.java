package org.dockbox.hartshorn.application.context.validate;

import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.GraphNode;

import java.util.Set;

public interface DependencyGraphValidator {

    default void validateBeforeConfiguration(DependencyGraph dependencyGraph) throws ApplicationException {}

    default void validateAfterConfiguration(DependencyGraph dependencyGraph, Set<GraphNode<DependencyContext<?>>> visited) throws ApplicationException {}
}
