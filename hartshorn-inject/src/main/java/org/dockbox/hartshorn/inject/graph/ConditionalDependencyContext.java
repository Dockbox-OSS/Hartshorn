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
