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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.context.validate.DependencyGraphValidator;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder;
import org.dockbox.hartshorn.util.graph.SimpleContentAwareGraph;

/**
 * A dependency graph is a graph of {@link DependencyContext} instances. It is used to resolve dependencies
 * and to determine the order in which they should be resolved. This can also be used by
 * {@link DependencyGraphValidator validators} to validate whether dependencies are valid.
 *
 * <p>You should not use this class directly, but instead use the {@link DependencyGraphBuilder} to build
 * a dependency graph from a collection of {@link DependencyContext} instances. This will ensure the graph
 * contains valid references internally.
 *
 * @see DependencyContext
 * @see DependencyGraphValidator
 * @see DependencyGraphBuilder
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DependencyGraph extends SimpleContentAwareGraph<DependencyContext<?>> {
}
