package org.dockbox.hartshorn.inject.graph;

import java.util.Set;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.MultiMapCollector;

public class ConditionalDependencyContextsHolder extends DefaultContext {

    private final MultiMap<ComponentKey<?>, ConditionalDependencyContext<?>> conditionalDependencyContexts;

    private ConditionalDependencyContextsHolder(MultiMap<ComponentKey<?>, ConditionalDependencyContext<?>> conditionalDependencyContexts) {
        this.conditionalDependencyContexts = conditionalDependencyContexts;
    }

    public MultiMap<ComponentKey<?>, ConditionalDependencyContext<?>> conditionalDependencyContexts() {
        return this.conditionalDependencyContexts;
    }

    public Set<ConditionalDependencyContext<?>> conditionalDependencyContextsAsSet() {
        return Set.copyOf(this.conditionalDependencyContexts.allValues());
    }

    public static ConditionalDependencyContextsHolder create(Set<ConditionalDependencyContext<?>> contexts) {
        MultiMap<ComponentKey<?>, ConditionalDependencyContext<?>> conditionalDependencyContexts = contexts.stream()
            .collect(MultiMapCollector.groupingBy(context -> context.dependencyContext().componentKey()));
        return new ConditionalDependencyContextsHolder(conditionalDependencyContexts);
    }
}
