package test.org.dockbox.hartshorn.inject.circular;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.inject.graph.ComponentMemberType;
import org.dockbox.hartshorn.inject.graph.ConfigurableDependencyContext;
import org.dockbox.hartshorn.inject.graph.DependencyGraph;
import org.dockbox.hartshorn.inject.graph.DependencyGraphBuilder;
import org.dockbox.hartshorn.inject.graph.DependencyMap;
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
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleA;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleB;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleC;
import test.org.dockbox.hartshorn.inject.circular.LongCycles.LongCycleD;

@HartshornIntegrationTest(includeBasePackages = false)
public class CircularDependencyTests {

    @Inject
    private ApplicationContext applicationContext;

    @Test
    @TestComponents(components = { CircularDependencyA.class, CircularDependencyB.class})
    void testCircularDependenciesAreCorrectOnFieldInject() {
        CircularDependencyA a = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyA.class));
        CircularDependencyB b = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyB.class));

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);

        Assertions.assertSame(a, b.a());
        Assertions.assertSame(b, a.b());
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
    void testImmediateCircularDependencyPathCanBeDetermined(List<Class<?>> path) {
        DependencyGraph dependencyGraph = this.buildDependencyGraph(path);
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        Assertions.assertEquals(0, roots.size()); // Cyclic, thus no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        Assertions.assertEquals(path.size(), nodes.size()); // N nodes, no duplicates, but does contain all nodes

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(path.get(0));

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        Assertions.assertNotNull(discoveryList);

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponents();
        Assertions.assertEquals(path.size(), discoveredComponents.size());

        List<? extends Class<?>> discoveredTypes = discoveredComponents.stream()
                .map(DiscoveredComponent::node)
                .map(TypePathNode::type)
                .map(TypeView::type)
                .toList();
        int startIndex = discoveredTypes.indexOf(path.get(0));

        for (int i = 0; i < path.size(); i++) {
            Assertions.assertSame(path.get(i), discoveredTypes.get((startIndex + i) % path.size()));
        }
    }

    @ParameterizedTest
    @MethodSource("circularDelayedResolution")
    void testDelayedCircularDependencyPathIsEmpty(List<Class<?>> path) {
        DependencyGraph dependencyGraph = this.buildDependencyGraph(path);
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        Assertions.assertEquals(0, roots.size()); // Cyclic, thus no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        Assertions.assertEquals(path.size(), nodes.size()); // N nodes, no duplicates, but does contain all nodes

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(path.get(0));

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        Assertions.assertNotNull(discoveryList);

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponents();
        Assertions.assertTrue(discoveredComponents.isEmpty());
    }

    private DependencyGraph buildDependencyGraph(List<Class<?>> components) {
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
                    // Fields and methods are always delayed
                    .delayed(dependencyResolver.resolveDependencies(typeView));

            View origin = typeView;
            if (!typeView.isInterface()) {
                List<? extends ConstructorView<?>> constructorViews = typeView.constructors().all().stream()
                        .filter(environment.injectionPointsResolver()::isInjectable)
                        .toList();
                if (!constructorViews.isEmpty()) {
                    Assertions.assertEquals(1, constructorViews.size());
                    ConstructorView<?> constructorView = constructorViews.get(0);
                    origin = constructorView;
                    // Constructors are always immediate
                    Set<ComponentKey<?>> immediateDependencies = dependencyResolver.resolveDependencies(constructorView);
                    dependencyMap.putAll(DependencyResolutionType.IMMEDIATE, immediateDependencies);
                }
            }

            ConfigurableDependencyContext<?> dependencyContext = ConfigurableDependencyContext.builder(componentKey)
                    .dependencies(dependencyMap)
                    .priority(-1)
                    .memberType(ComponentMemberType.STANDALONE)
                    .view(origin)
                    .supplier(PrototypeInstantiationStrategy.empty())
                    .build();
            dependencyContexts.add(dependencyContext);
        }

        SimpleSingleElementContext<InjectionCapableApplication> context = SimpleSingleElementContext.create(applicationContext);
        DependencyResolver resolver = ApplicationDependencyResolver.create(Customizer.useDefaults()).initialize(context);
        DependencyGraphBuilder dependencyGraphBuilder = DependencyGraphBuilder.create(
                resolver,
                this.applicationContext.defaultBinder(),
                this.applicationContext.environment().introspector()
        );
        return Assertions.assertDoesNotThrow(() -> dependencyGraphBuilder.buildDependencyGraph(dependencyContexts));
    }

    @Test
    void testCircularDependencyPathOnBoundTypeCanBeDetermined() {
        // Bindings should be resolved during graph construction.
        this.applicationContext
                .bind(InterfaceCircularDependencyA.class).to(BoundCircularDependencyA.class)
                .bind(InterfaceCircularDependencyB.class).to(BoundCircularDependencyB.class);

        DependencyGraph dependencyGraph = this.buildDependencyGraph(List.of(InterfaceCircularDependencyA.class, InterfaceCircularDependencyB.class));
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        Assertions.assertEquals(0, roots.size()); // Cyclic, so no roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        Assertions.assertEquals(4, nodes.size()); // 4 nodes, 2 interfaces, 2 implementations

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(BoundCircularDependencyA.class);

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());

        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext.environment().introspector());
        List<DiscoveredComponent> discoveredComponentsNonCyclic = discoveryList.discoveredComponents();
        Assertions.assertEquals(2, discoveredComponentsNonCyclic.size());

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponentsCyclic();
        Assertions.assertEquals(3, discoveredComponents.size());

        DiscoveredComponent discoveredComponentA1 = discoveredComponents.get(0);
        Assertions.assertSame(InterfaceCircularDependencyA.class, discoveredComponentA1.node().type().type());
        Assertions.assertSame(BoundCircularDependencyA.class, discoveredComponentA1.actualType().type());

        DiscoveredComponent discoveredComponentB = discoveredComponents.get(1);
        Assertions.assertSame(InterfaceCircularDependencyB.class, discoveredComponentB.node().type().type());
        Assertions.assertSame(BoundCircularDependencyB.class, discoveredComponentB.actualType().type());

        DiscoveredComponent discoveredComponentA2 = discoveredComponents.get(2);
        Assertions.assertSame(InterfaceCircularDependencyA.class, discoveredComponentA2.node().type().type());
        Assertions.assertSame(BoundCircularDependencyA.class, discoveredComponentA2.actualType().type());

        Assertions.assertEquals(discoveredComponentA1, discoveredComponentA2);
    }
}
