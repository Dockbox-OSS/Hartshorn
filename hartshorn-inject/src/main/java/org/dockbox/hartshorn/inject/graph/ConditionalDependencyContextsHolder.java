/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.inject.graph;

import java.util.Set;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.MultiMapCollector;

/**
 * A holder for conditional dependency contexts, which are used to determine whether certain dependencies
 * should be resolved based on specific conditions.
 *
 * @see ConditionalDependencyContext
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
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
