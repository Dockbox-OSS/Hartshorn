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

package org.dockbox.hartshorn.inject.graph.support;

import java.util.Collection;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.util.ApplicationException;

public class AmbiguousAliasException extends ApplicationException {

    private final ComponentKey<?> componentKey;
    private final Collection<DependencyContext<?>> contexts;

    public AmbiguousAliasException(ComponentKey<?> componentKey, Collection<DependencyContext<?>> contexts) {
        super("Ambiguous alias " + componentKey + " found. The alias was defined in the following locations: " + contexts.stream()
            .map(DependencyContext::describe)
            .collect(Collectors.joining(", ")));
        this.componentKey = componentKey;
        this.contexts = contexts;
    }

    public ComponentKey<?> componentKey() {
        return this.componentKey;
    }

    public Collection<DependencyContext<?>> contexts() {
        return this.contexts;
    }
}
