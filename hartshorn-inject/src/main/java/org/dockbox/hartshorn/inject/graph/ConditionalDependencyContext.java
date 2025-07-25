package org.dockbox.hartshorn.inject.graph;

import java.util.function.Predicate;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;

/**
 * Wrapper for a {@link DependencyContext} that includes a predicate to determine whether the
 * conditions for the dependency are matched. If the conditions are not matched, the dependency
 * should not be resolved or registered to the dependency graph.
 *
 * @param dependencyContext the dependency context to wrap
 * @param conditionsMatched a predicate that checks if the conditions for the dependency are matched
 * @param <T> the type of the dependency
 */
public record ConditionalDependencyContext<T>(
    DependencyContext<T> dependencyContext,
    Predicate<ConditionalDependencyContextsHolder> conditionsMatched
) {
}
