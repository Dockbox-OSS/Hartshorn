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

package test.org.dockbox.hartshorn.inject.graph.support;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.binding.DefaultBindingAliasNormalizer;
import org.dockbox.hartshorn.inject.graph.DependencyGraph;
import org.dockbox.hartshorn.inject.graph.DependencyGraphValidator;
import org.dockbox.hartshorn.inject.graph.declaration.AliasableDependencyContext;
import org.dockbox.hartshorn.inject.graph.support.AmbiguousAliasException;
import org.dockbox.hartshorn.inject.graph.support.OverlappingAliasDependencyGraphValidator;
import org.dockbox.hartshorn.inject.provider.AliasCapableComponentProviderOrchestrator;
import org.dockbox.hartshorn.util.graph.SimpleGraphNode;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class OverlappingAliasDependencyGraphValidatorTests {

    @Test
    void overlappingAliasWithSamePriorityFails() {
        DependencyGraphValidator validator = new OverlappingAliasDependencyGraphValidator();
        DependencyGraph graph = new DependencyGraph();

        AliasableDependencyContext<String> declarationString =
            createMockCharSequenceDependencyContext(String.class);
        AliasableDependencyContext<StringBuilder> declarationStringBuilder =
            createMockCharSequenceDependencyContext(StringBuilder.class);

        graph.addRoot(new SimpleGraphNode<>(declarationString));
        graph.addRoot(new SimpleGraphNode<>(declarationStringBuilder));

        AliasCapableComponentProviderOrchestrator orchestrator =
            Mockito.mock(AliasCapableComponentProviderOrchestrator.class);
        Mockito.when(orchestrator.aliasNormalizer())
            .thenReturn(new DefaultBindingAliasNormalizer());
        AmbiguousAliasException exception = assertThatExceptionOfType(AmbiguousAliasException.class).isThrownBy(() -> validator.validateBeforeConfiguration(graph, null, orchestrator)).actual();

        assertThat(exception.contexts()).hasSize(2);
        assertThat(exception.componentKey()).isEqualTo(ComponentKey.of(CharSequence.class));
    }

    @Test
    void overlappingAliasWithDifferentPrioritiesPasses() throws Exception {
        DependencyGraphValidator validator = new OverlappingAliasDependencyGraphValidator();
        DependencyGraph graph = new DependencyGraph();

        AliasableDependencyContext<String> declarationString =
            createMockCharSequenceDependencyContext(String.class);
        Mockito.when(declarationString.priority()).thenReturn(1);

        AliasableDependencyContext<StringBuilder> declarationStringBuilder =
            createMockCharSequenceDependencyContext(StringBuilder.class);
        Mockito.when(declarationStringBuilder.priority()).thenReturn(2);

        graph.addRoot(new SimpleGraphNode<>(declarationString));
        graph.addRoot(new SimpleGraphNode<>(declarationStringBuilder));

        AliasCapableComponentProviderOrchestrator orchestrator =
            Mockito.mock(AliasCapableComponentProviderOrchestrator.class);
        Mockito.when(orchestrator.aliasNormalizer())
            .thenReturn(new DefaultBindingAliasNormalizer());
        validator.validateBeforeConfiguration(graph, null, orchestrator);
    }

    private static <T extends CharSequence> AliasableDependencyContext<T> createMockCharSequenceDependencyContext(
        Class<T> keyType
    ) {
        AliasableDependencyContext<T> declarationString =
            Mockito.mock(AliasableDependencyContext.class);
        Mockito.when(declarationString.componentKey()).thenReturn(ComponentKey.of(keyType));
        Mockito.when(declarationString.describe()).thenReturn("mockLocation");
        Mockito.when(declarationString.aliasTypes()).thenReturn(Set.of(CharSequence.class));
        Mockito.when(declarationString.aliasQualifiers()).thenReturn(Set.of());
        Mockito.when(declarationString.aliasKeys()).thenReturn(Set.of());
        Mockito.when(declarationString.hasConfiguredAliases()).thenReturn(true);
        Mockito.when(declarationString.priority()).thenReturn(0);
        return declarationString;
    }
}
