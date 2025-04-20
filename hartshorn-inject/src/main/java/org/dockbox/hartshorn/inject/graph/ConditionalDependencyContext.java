package org.dockbox.hartshorn.inject.graph;

import java.util.function.Predicate;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;

public record ConditionalDependencyContext<T>(
    DependencyContext<T> dependencyContext,
    Predicate<ConditionalDependencyContextsHolder> conditionsMatched
) {
}
