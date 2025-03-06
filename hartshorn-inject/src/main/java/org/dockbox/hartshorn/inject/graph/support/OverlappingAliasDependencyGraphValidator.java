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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.binding.BindingAliasNormalizer;
import org.dockbox.hartshorn.inject.graph.DependencyGraph;
import org.dockbox.hartshorn.inject.graph.DependencyGraphValidator;
import org.dockbox.hartshorn.inject.graph.declaration.AliasableDependencyContext;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.provider.AliasCapableComponentProviderOrchestrator;
import org.dockbox.hartshorn.inject.provider.ComponentProviderOrchestrator;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.HashSetMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;

public class OverlappingAliasDependencyGraphValidator implements DependencyGraphValidator {

    @Override
    public void validateBeforeConfiguration(
        DependencyGraph dependencyGraph,
        Introspector introspector,
        ComponentProviderOrchestrator orchestrator
    ) throws ApplicationException {
        if (orchestrator instanceof AliasCapableComponentProviderOrchestrator aliasCapableOrchestrator) {
            BindingAliasNormalizer aliasNormalizer = aliasCapableOrchestrator.aliasNormalizer();
            List<? extends AliasableDependencyContext<?>> aliasedDependencyContexts = dependencyGraph.nodes().stream()
                .filter(node -> node.value() instanceof AliasableDependencyContext<?> context && context.hasConfiguredAliases())
                .map(node -> (AliasableDependencyContext<?>) node.value())
                .toList();

            // Collect all ambiguous locations, rather than just failing on the first one
            MultiMap<ComponentKey<?>, DependencyContext<?>> contextsByAlias = new HashSetMultiMap<>();
            for (AliasableDependencyContext<?> dependencyContext : aliasedDependencyContexts) {
                for (ComponentKey<?> componentKey : this.normalizeComponentKeys(dependencyContext, aliasNormalizer)) {
                    contextsByAlias.put(componentKey, dependencyContext);
                }
            }

            for (ComponentKey<?> componentKey : contextsByAlias.keySet()) {
                Collection<DependencyContext<?>> contexts = contextsByAlias.get(componentKey);
                if (contexts.size() > 1) {
                    throw new AmbiguousAliasException(componentKey, contexts);
                }
            }
        }
    }

    private <T> Set<ComponentKey<? super T>> normalizeComponentKeys(
        AliasableDependencyContext<T> dependencyContext,
        BindingAliasNormalizer aliasNormalizer
    ) {
        Set<ComponentKey<? super T>> componentKeys = dependencyContext.aliasKeys();
        Set<QualifierKey<T>> qualifierKeys = dependencyContext.aliasQualifiers();
        Set<Class<? super T>> types = dependencyContext.aliasTypes();

        Set<ComponentKey<? super T>> normalizedKeys = new HashSet<>(componentKeys);
        for (QualifierKey<T> qualifierKey : qualifierKeys) {
            normalizedKeys.add(aliasNormalizer.alias(dependencyContext.componentKey(), qualifierKey));
        }
        for (Class<? super T> type : types) {
            normalizedKeys.add(aliasNormalizer.alias(dependencyContext.componentKey(), type));
        }
        return normalizedKeys;
    }
}
