/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.Introspector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A composite validator that can be used to combine multiple validators into a single validator.
 *
 * @see DependencyGraphValidator
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CompositeDependencyGraphValidator implements DependencyGraphValidator {

    private final Set<DependencyGraphValidator> validators = new HashSet<>();

    public CompositeDependencyGraphValidator(Collection<DependencyGraphValidator> validators) {
        this.validators.addAll(validators);
    }

    /**
     * Adds a validator to the composite. If the validator is already present, it will not be added again.
     *
     * @param validator the validator to add
     */
    public void add(DependencyGraphValidator validator) {
        this.validators.add(validator);
    }

    @Override
    public void validateBeforeConfiguration(DependencyGraph dependencyGraph, Introspector introspector) throws ApplicationException {
        for (DependencyGraphValidator validator : this.validators) {
            validator.validateBeforeConfiguration(dependencyGraph, introspector);
        }
    }

    @Override
    public void validateAfterConfiguration(DependencyGraph dependencyGraph, Introspector introspector, Set<GraphNode<DependencyContext<?>>> visited) throws ApplicationException {
        for (DependencyGraphValidator validator : this.validators) {
            validator.validateAfterConfiguration(dependencyGraph, introspector, visited);
        }
    }
}
