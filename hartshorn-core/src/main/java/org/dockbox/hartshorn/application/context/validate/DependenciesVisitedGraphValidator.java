/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.application.context.validate;

import java.util.Set;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyPresenceValidationVisitor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.GraphNode;

/**
 * A validator that checks whether all dependencies in the graph have been visited. If not, it will throw an exception.
 * This validator is intended to be used after the configuration phase, so it can check that all dependencies have been
 * configured correctly.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DependenciesVisitedGraphValidator implements DependencyGraphValidator {

    @Override
    public void validateAfterConfiguration(DependencyGraph dependencyGraph, ApplicationContext applicationContext, Set<GraphNode<DependencyContext<?>>> visited) throws ApplicationException {
        DependencyPresenceValidationVisitor validationVisitor = new DependencyPresenceValidationVisitor(visited);
        validationVisitor.iterate(dependencyGraph);
        Set<GraphNode<DependencyContext<?>>> missingDependencies = validationVisitor.missingDependencies();
        if (!missingDependencies.isEmpty()) {
            throw new ComponentInitializationException("Failed to resolve dependencies: %s".formatted(missingDependencies));
        }
    }
}
