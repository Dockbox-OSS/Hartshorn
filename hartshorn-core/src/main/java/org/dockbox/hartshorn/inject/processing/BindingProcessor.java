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

package org.dockbox.hartshorn.inject.processing;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.graph.Graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BindingProcessor {

    private final DependencyGraphBuilder graphBuilder = new DependencyGraphBuilder();
    private final Set<DependencyContextGraphVisitor> visitors = new HashSet<>();

    public void process(final ProviderContextList context, final ApplicationContext applicationContext) throws ApplicationException {
        final MultiMap<Integer, ProviderContext> elements = context.elements();

        for (final Integer phase : elements.keySet()) {
            final DependencyContextGraphVisitor graphVisitor = new DependencyContextGraphVisitor(phase, applicationContext);
            this.visitors.add(graphVisitor);
            final Collection<ProviderContext> providerContexts = elements.get(phase);
            final Graph<ProviderContext> contextGraph = graphBuilder.buildDependencyGraph(providerContexts);
            graphVisitor.iterate(contextGraph);
        }
    }

    public void finalizeProxies(final ApplicationContext context) {
        for (final DependencyContextGraphVisitor visitor : Set.copyOf(this.visitors)) {
            visitor.finalizeProxies(context);
            this.visitors.remove(visitor);
        }
    }
}
