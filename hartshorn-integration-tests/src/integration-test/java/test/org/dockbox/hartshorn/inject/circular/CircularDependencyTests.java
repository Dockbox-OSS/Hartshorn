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

package test.org.dockbox.hartshorn.inject.circular;

import org.dockbox.hartshorn.context.SimpleSingleElementContext;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.annotations.Priority;
import org.dockbox.hartshorn.inject.graph.ComponentMemberType;
import org.dockbox.hartshorn.inject.graph.ConfigurableDependencyContext;
import org.dockbox.hartshorn.inject.graph.DependencyGraph;
import org.dockbox.hartshorn.inject.graph.DependencyGraphBuilder;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionException;
import org.dockbox.hartshorn.inject.graph.DependencyResolutionType;
import org.dockbox.hartshorn.inject.graph.DependencyResolver;
import org.dockbox.hartshorn.inject.graph.TypePathNode;
import org.dockbox.hartshorn.inject.graph.declaration.DependencyContext;
import org.dockbox.hartshorn.inject.graph.resolve.ApplicationDependencyResolver;
import org.dockbox.hartshorn.inject.graph.resolve.IntrospectionDependencyResolver;
import org.dockbox.hartshorn.inject.graph.support.ComponentDiscoveryList;
import org.dockbox.hartshorn.inject.graph.support.ComponentDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.inject.graph.support.CyclicDependencyGraphValidator;
import org.dockbox.hartshorn.inject.provider.PrototypeInstantiationStrategy;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.test.annotations.TestComponents;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.dockbox.hartshorn.util.configure.Customizer;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleA;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleB;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleC;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleD;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@HartshornIntegrationTest(includeBasePackages = false)
public class CircularDependencyTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents({ CircularDependencyA.class, CircularDependencyB.class})
    void circularDependenciesAreCorrectOnFieldInject() {
        CircularDependencyA a = this.applicationContext.get(CircularDependencyA.class);
        CircularDependencyB b = this.applicationContext.get(CircularDependencyB.class);

        assertThat(a).isNotNull();
        assertThat(b).isNotNull();

        assertThat(b.a()).isSameAs(a);
        assertThat(a.b()).isSameAs(b);
    }

    public static Stream<Arguments> circularDelayedResolution() {
        return Stream.of(
                // Circular, but can use delayed resolution
                Arguments.of(List.of(CircularDependencyA.class, CircularDependencyB.class)),
                Arguments.of(List.of(CircularDependencyB.class, CircularDependencyA.class))
        );
    }

    public static Stream<Arguments> circularImmediateResolution() {
        return Stream.of(
                // Circular, needs immediate resolution but cannot
                Arguments.of(List.of(CircularConstructorA.class, CircularConstructorB.class)),
                Arguments.of(List.of(CircularConstructorB.class, CircularConstructorA.class)),
                // Circular, but in longer cycles
                Arguments.of(List.of(LongCycleA.class, LongCycleB.class, LongCycleC.class, LongCycleD.class)),
                Arguments.of(List.of(LongCycleB.class, LongCycleC.class, LongCycleD.class, LongCycleA.class)),
                Arguments.of(List.of(LongCycleC.class, LongCycleD.class, LongCycleA.class, LongCycleB.class)),
                Arguments.of(List.of(LongCycleD.class, LongCycleA.class, LongCycleB.class, LongCycleC.class))
        );
    }

    @ParameterizedTest
    @MethodSource("circularImmediateResolution")
    void immediateCircularDependencyPathCanBeDetermined(List<Class<?>> path) throws DependencyResolutionException {
        DependencyGraph dependencyGraph = this.buildDependencyGraph(path);
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        assertThat(roots).hasSize(0); // Cyclic, thus no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        assertThat(nodes).hasSize(path.size()); // N nodes, no duplicates, but does contain all nodes

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(path.get(0));

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        assertThat(discoveryList).isNotNull();

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponents();
        assertThat(discoveredComponents).hasSize(path.size());

        List<? extends Class<?>> discoveredTypes = discoveredComponents.stream()
                .map(DiscoveredComponent::node)
                .map(TypePathNode::type)
                .map(TypeView::type)
                .toList();
        int startIndex = discoveredTypes.indexOf(path.get(0));

        for (int i = 0; i < path.size(); i++) {
            assertThat(discoveredTypes.get((startIndex + i) % path.size())).isSameAs(path.get(i));
        }
    }

    @ParameterizedTest
    @MethodSource("circularDelayedResolution")
    void delayedCircularDependencyPathIsEmpty(List<Class<?>> path) throws DependencyResolutionException {
        DependencyGraph dependencyGraph = this.buildDependencyGraph(path);
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        assertThat(roots).hasSize(0); // Cyclic, thus no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        assertThat(nodes).hasSize(path.size()); // N nodes, no duplicates, but does contain all nodes

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(path.get(0));

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        assertThat(discoveryList).isNotNull();

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponents();
        assertThat(discoveredComponents).isEmpty();
    }

    private DependencyGraph buildDependencyGraph(List<Class<?>> components) throws DependencyResolutionException {
        Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        ApplicationEnvironment environment = this.applicationContext.environment();
        IntrospectionDependencyResolver dependencyResolver = new IntrospectionDependencyResolver(
                environment.injectionPointsResolver(),
                environment.componentKeyResolver()
        );
        for(Class<?> component : components) {
            ComponentKey<?> componentKey = ComponentKey.of(component);
            TypeView<?> typeView = environment.introspector().introspect(component);

            DependencyMap dependencyMap = DependencyMap.create()
                    // Fields and methods are always delayed, as they are not required for instantiation
                    .delayed(dependencyResolver.resolveDependencies(typeView));

            View origin = typeView;
            if (!typeView.isInterface()) {
                List<? extends ConstructorView<?>> constructorViews = typeView.constructors().all().stream()
                        .filter(environment.injectionPointsResolver()::isInjectable)
                        .toList();
                if (!constructorViews.isEmpty()) {
                    assertThat(constructorViews).hasSize(1);
                    ConstructorView<?> constructorView = constructorViews.get(0);
                    origin = constructorView;
                    // Constructors are always immediate, as they are required to instantiate the component
                    Set<ComponentKey<?>> immediateDependencies = dependencyResolver.resolveDependencies(constructorView);
                    dependencyMap.putAll(DependencyResolutionType.IMMEDIATE, immediateDependencies);
                }
            }

            ConfigurableDependencyContext<?> dependencyContext = ConfigurableDependencyContext.builder(componentKey)
                    .dependencies(dependencyMap)
                    .priority(Priority.DEFAULT_PRIORITY)
                    .memberType(ComponentMemberType.STANDALONE)
                    .view(origin)
                    .supplier(PrototypeInstantiationStrategy.empty())
                    .build();
            dependencyContexts.add(dependencyContext);
        }

        SimpleSingleElementContext<InjectionCapableApplication> context = SimpleSingleElementContext.create(this.applicationContext);
        DependencyResolver resolver = ApplicationDependencyResolver.create(Customizer.useDefaults()).initialize(context);
        DependencyGraphBuilder dependencyGraphBuilder = DependencyGraphBuilder.create(
                resolver,
                this.applicationContext.defaultBinder(),
                this.applicationContext.environment().introspector()
        );
        return dependencyGraphBuilder.buildDependencyGraph(dependencyContexts);
    }

    @Test
    void circularDependencyPathOnBoundTypeCanBeDetermined() throws DependencyResolutionException {
        // Bindings should be resolved during graph construction.
        this.applicationContext
                .bind(InterfaceCircularDependencyA.class).to(BoundCircularDependencyA.class)
                .bind(InterfaceCircularDependencyB.class).to(BoundCircularDependencyB.class);

        DependencyGraph dependencyGraph = this.buildDependencyGraph(List.of(InterfaceCircularDependencyA.class, InterfaceCircularDependencyB.class));
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        assertThat(roots).hasSize(0); // Cyclic, so no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        assertThat(nodes).hasSize(4); // 4 nodes, 2 interfaces, 2 implementations

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(BoundCircularDependencyA.class);

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());

        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        List<DiscoveredComponent> discoveredComponentsNonCyclic = discoveryList.discoveredComponents();
        assertThat(discoveredComponentsNonCyclic).hasSize(2);

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponentsCyclic();
        assertThat(discoveredComponents).hasSize(3);

        DiscoveredComponent discoveredComponentA1 = discoveredComponents.get(0);
        assertThat(discoveredComponentA1.node().type().type()).isSameAs(InterfaceCircularDependencyA.class);
        assertThat(discoveredComponentA1.actualType().type()).isSameAs(BoundCircularDependencyA.class);

        DiscoveredComponent discoveredComponentB = discoveredComponents.get(1);
        assertThat(discoveredComponentB.node().type().type()).isSameAs(InterfaceCircularDependencyB.class);
        assertThat(discoveredComponentB.actualType().type()).isSameAs(BoundCircularDependencyB.class);

        DiscoveredComponent discoveredComponentA2 = discoveredComponents.get(2);
        assertThat(discoveredComponentA2.node().type().type()).isSameAs(InterfaceCircularDependencyA.class);
        assertThat(discoveredComponentA2.actualType().type()).isSameAs(BoundCircularDependencyA.class);

        assertThat(discoveredComponentA2).isEqualTo(discoveredComponentA1);
    }
}
