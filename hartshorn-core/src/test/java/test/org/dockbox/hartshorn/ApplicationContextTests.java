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

package test.org.dockbox.hartshorn;

import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.DependencyGraph;
import org.dockbox.hartshorn.application.context.validate.CyclicDependencyGraphValidator;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.component.populate.StrategyComponentPopulator;
import org.dockbox.hartshorn.component.processing.ComponentMemberType;
import org.dockbox.hartshorn.inject.ApplicationDependencyResolver;
import org.dockbox.hartshorn.inject.AutoConfiguringDependencyContext;
import org.dockbox.hartshorn.inject.ComponentDiscoveryList;
import org.dockbox.hartshorn.inject.ComponentDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.DependencyContext;
import org.dockbox.hartshorn.inject.DependencyMap;
import org.dockbox.hartshorn.inject.DependencyResolutionType;
import org.dockbox.hartshorn.inject.DependencyResolver;
import org.dockbox.hartshorn.inject.TypePathNode;
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder;
import org.dockbox.hartshorn.inject.strategy.IntrospectionDependencyResolver;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestBinding;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SimpleSingleElementContext;
import org.dockbox.hartshorn.util.graph.GraphNode;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.introspect.view.View;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import test.org.dockbox.hartshorn.boot.EmptyService;
import test.org.dockbox.hartshorn.components.BoundCircularDependencyA;
import test.org.dockbox.hartshorn.components.BoundCircularDependencyB;
import test.org.dockbox.hartshorn.components.CircularConstructorA;
import test.org.dockbox.hartshorn.components.CircularConstructorB;
import test.org.dockbox.hartshorn.components.CircularDependencyA;
import test.org.dockbox.hartshorn.components.CircularDependencyB;
import test.org.dockbox.hartshorn.components.ComponentType;
import test.org.dockbox.hartshorn.components.ContextInjectedType;
import test.org.dockbox.hartshorn.components.InterfaceCircularDependencyA;
import test.org.dockbox.hartshorn.components.InterfaceCircularDependencyB;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleA;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleB;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleC;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleD;
import test.org.dockbox.hartshorn.components.NonComponentType;
import test.org.dockbox.hartshorn.components.NonProcessableType;
import test.org.dockbox.hartshorn.components.NonProcessableTypeProcessor;
import test.org.dockbox.hartshorn.components.NonProxyComponentType;
import test.org.dockbox.hartshorn.components.PopulatedType;
import test.org.dockbox.hartshorn.components.ProvidedInterface;
import test.org.dockbox.hartshorn.components.ProviderService;
import test.org.dockbox.hartshorn.components.SampleConfiguration;
import test.org.dockbox.hartshorn.components.SampleContext;
import test.org.dockbox.hartshorn.components.SampleField;
import test.org.dockbox.hartshorn.components.SampleFieldImplementation;
import test.org.dockbox.hartshorn.components.SampleImplementation;
import test.org.dockbox.hartshorn.components.SampleInterface;
import test.org.dockbox.hartshorn.components.SampleMetaAnnotatedImplementation;
import test.org.dockbox.hartshorn.components.SampleProviderConfiguration;
import test.org.dockbox.hartshorn.components.SampleType;
import test.org.dockbox.hartshorn.components.SetterInjectedComponent;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithAbsentBinding;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithNonRequiredAbsentBinding;
import test.org.dockbox.hartshorn.components.TypeWithFailingConstructor;
import test.org.dockbox.hartshorn.components.TypeWithPostConstructableInjectField;
import test.org.dockbox.hartshorn.components.contextual.ErrorInConstructorObject;

@HartshornTest(includeBasePackages = false)
public class ApplicationContextTests {

    @Inject
    private ApplicationContext applicationContext;

    private static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of(null, "Provision", false, null, false),
                Arguments.of("named", "NamedProvision", false, null, false),
                Arguments.of("parameter", "ParameterProvision", true, null, false),
                Arguments.of("namedParameter", "NamedParameterProvision", true, "named", false),
                Arguments.of("singleton", "SingletonProvision", false, null, true)
        );
    }

    @Test
    void testContextLoads() {
        Assertions.assertNotNull(this.applicationContext);
    }

    // TODO #1003: Restore, types were moved to test fixtures, so need new test components
//    @Test
//    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
//    @TestComponents({AbstractProxy.class, ProxyProviders.class})
//    void testMethodCanDelegateToImplementation() {
//        AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
//        Assertions.assertEquals("concrete", abstractProxy.name());
//    }
//
//    @Test
//    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
//    @TestComponents({AbstractProxy.class, ProxyProviders.class})
//    void testMethodOverrideDoesNotDelegateToImplementation() {
//        AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
//        Assertions.assertEquals(21, abstractProxy.age());
//    }

    @Test
    public void testStaticBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation.class);
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation.class);
        SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    public void testInstanceBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).singleton(new SampleImplementation());
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).singleton(new SampleImplementation());
        SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    public void testProviderBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation::new);
        SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() {
        ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation::new);
        SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleImplementation.class, providedClass);

        Assertions.assertEquals(SampleImplementation.NAME, provided.name());
    }

    @Test
    @TestComponents(components = SampleConfiguration.class)
    public void testScannedMetaBindingsCanBeProvided() {

        // Ensure that the binding is not bound to the default name
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(SampleInterface.class));

        SampleInterface provided = this.applicationContext.get(ComponentKey.of(SampleInterface.class, "meta"));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertSame(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    @TestComponents(components = TypeWithPostConstructableInjectField.class)
    void testPostConstructInjectDoesNotInjectTwice() {
        TypeWithPostConstructableInjectField instance = this.applicationContext.get(TypeWithPostConstructableInjectField.class);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.postConstructableObject());
        Assertions.assertEquals(1, instance.postConstructableObject().getTimesConstructed());
    }

    public static Stream<Arguments> componentPopulators() {
        return Stream.of(
            Arguments.of((Function<ApplicationContext, ComponentPopulator>) context -> {
                return StrategyComponentPopulator.create(Customizer.useDefaults()).initialize(SimpleSingleElementContext.create(context));
            })
        );
    }

    @ParameterizedTest
    @MethodSource("componentPopulators")
    @TestComponents(bindings = @TestBinding(type = SampleInterface.class, implementation = SampleImplementation.class))
    public void testTypesCanBePopulated(Function<ApplicationContext, ComponentPopulator> populatorFactory) {
        PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        populatorFactory.apply(this.applicationContext).populate(populatedType);
        Assertions.assertNotNull(populatedType.sampleInterface());
        Assertions.assertEquals(SampleImplementation.NAME, populatedType.sampleInterface().name());
    }

    @Test
    @TestComponents(
            components = PopulatedType.class,
            bindings = @TestBinding(type = SampleInterface.class, implementation = SampleImplementation.class)
    )
    public void unboundTypesCanBeProvided() {
        PopulatedType provided = this.applicationContext.get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.sampleInterface());
    }

    @ParameterizedTest
    @MethodSource("providers")
    @TestComponents(components = {SampleFieldImplementation.class, SampleProviderConfiguration.class})
    void testProvidersCanApply(String meta, String name, boolean field, String fieldMeta, boolean singleton) {
        if (field) {
            if (fieldMeta == null) {this.applicationContext.bind(SampleField.class).to(SampleFieldImplementation.class);}
            else {
                this.applicationContext.bind(ComponentKey.of(SampleField.class, fieldMeta)).to(SampleFieldImplementation.class);
            }
        }

        ProvidedInterface provided;
        if (meta == null) {
            provided = this.applicationContext.get(ProvidedInterface.class);
        }
        else {
            provided = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
        }
        Assertions.assertNotNull(provided);

        String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            ProvidedInterface second;
            if (meta == null) {
                second = this.applicationContext.get(ProvidedInterface.class);
            }
            else {
                second = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
            }
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @ParameterizedTest
    @MethodSource("componentPopulators")
    void testContextFieldsAreInjected(Function<ApplicationContext, ComponentPopulator> populatorFactory) {
        String contextName = "InjectedContext";
        this.applicationContext.addContext(new SampleContext(contextName));

        ComponentPopulator populator = populatorFactory.apply(this.applicationContext);
        ContextInjectedType instance = populator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals(contextName, instance.context().name());
    }

    @ParameterizedTest
    @MethodSource("componentPopulators")
    void testNamedContextFieldsAreInjected(Function<ApplicationContext, ComponentPopulator> populatorFactory) {
        String contextName = "InjectedContext";
        this.applicationContext.addContext("another", new SampleContext(contextName));

        ComponentPopulator populator = populatorFactory.apply(this.applicationContext);
        ContextInjectedType instance = populator.populate(new ContextInjectedType());

        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals(contextName, instance.anotherContext().name());
    }

    @Test
    @TestComponents(components = EmptyService.class)
    void servicesAreSingletonsByDefault() {
        EmptyService emptyService = this.applicationContext.get(EmptyService.class);
        EmptyService emptyService2 = this.applicationContext.get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonComponentType.class));
    }

    @Test
    @TestComponents(components = ComponentType.class)
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        ComponentType instance = this.applicationContext.get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext.environment().proxyOrchestrator().isProxy(instance));
    }

    @Test
    @TestComponents(components = NonProxyComponentType.class)
    void testNonPermittedComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonProxyComponentType.class));
    }

    @Test
    void testFailingConstructorIsRethrown() {
        ComponentInitializationException exception = Assertions.assertThrows(ComponentInitializationException.class, () -> this.applicationContext.get(TypeWithFailingConstructor.class));
        Assertions.assertTrue(exception.getCause() instanceof ApplicationException);

        ApplicationException applicationException = (ApplicationException) exception.getCause();
        Assertions.assertTrue(applicationException.getCause() instanceof IllegalStateException);

        IllegalStateException illegalStateException = (IllegalStateException) applicationException.getCause();
        Assertions.assertEquals(TypeWithFailingConstructor.ERROR_MESSAGE, illegalStateException.getMessage());
    }

    @Test
    @TestComponents(components = {CircularDependencyA.class, CircularDependencyB.class})
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
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext);
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
        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext);
        Assertions.assertNotNull(discoveryList);

        List<DiscoveredComponent> discoveredComponents = discoveryList.discoveredComponents();
        Assertions.assertTrue(discoveredComponents.isEmpty());
    }

    private DependencyGraph buildDependencyGraph(List<Class<?>> components) {
        Set<DependencyContext<?>> dependencyContexts = new HashSet<>();
        IntrospectionDependencyResolver dependencyResolver = new IntrospectionDependencyResolver(this.applicationContext.environment());
        for(Class<?> component : components) {
            ComponentKey<?> componentKey = ComponentKey.of(component);
            TypeView<?> typeView = this.applicationContext.environment().introspector().introspect(component);

            DependencyMap dependencyMap = DependencyMap.create()
                    // Fields and methods are always delayed
                    .delayed(dependencyResolver.resolveDependencies(typeView));

            View origin = typeView;
            if (!typeView.isInterface()) {
                List<? extends ConstructorView<?>> constructorViews = typeView.constructors().all().stream()
                        .filter(this.applicationContext.environment().injectionPointsResolver()::isInjectable)
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

            AutoConfiguringDependencyContext<?> dependencyContext = AutoConfiguringDependencyContext.builder(componentKey)
                .dependencies(dependencyMap)
                .scope(ApplicationContext.APPLICATION_SCOPE)
                .priority(-1)
                .memberType(ComponentMemberType.STANDALONE)
                .view(origin)
                .supplier(requestContext -> null)
                .build();
            dependencyContexts.add(dependencyContext);
        }

        SimpleSingleElementContext<ApplicationContext> context = SimpleSingleElementContext.create(this.applicationContext);
        DependencyResolver resolver = ApplicationDependencyResolver.create(Customizer.useDefaults()).initialize(context);
        DependencyGraphBuilder dependencyGraphBuilder = DependencyGraphBuilder.create(resolver);
        return Assertions.assertDoesNotThrow(() -> dependencyGraphBuilder.buildDependencyGraph(dependencyContexts));
    }

    @Test
    @Ignore("TODO: Review after changes to singleton caching and circular dependencies")
    void testCircularDependencyPathOnBoundTypeCanBeDetermined() {
        // Bindings should be resolved during graph construction.
        this.applicationContext
                .bind(InterfaceCircularDependencyA.class).to(BoundCircularDependencyA.class)
                .bind(InterfaceCircularDependencyB.class).to(BoundCircularDependencyB.class);

        DependencyGraph dependencyGraph = this.buildDependencyGraph(List.of(InterfaceCircularDependencyA.class, InterfaceCircularDependencyB.class));
        CyclicDependencyGraphValidator validator = new CyclicDependencyGraphValidator();

        Set<GraphNode<DependencyContext<?>>> roots = dependencyGraph.roots();
        Assertions.assertEquals(0, roots.size()); // Cyclic, but two singleton roots

        Set<GraphNode<DependencyContext<?>>> nodes = dependencyGraph.nodes();
        Assertions.assertEquals(4, nodes.size()); // 4 nodes, 2 interfaces, 2 implementations

        Map<? extends Class<?>, GraphNode<DependencyContext<?>>> nodesByType = nodes.stream()
                .collect(Collectors.toMap(node -> node.value().componentKey().type(), Function.identity()));
        GraphNode<DependencyContext<?>> firstNode = nodesByType.get(BoundCircularDependencyA.class);

        List<GraphNode<DependencyContext<?>>> recursivePath = validator.checkNodeNotCyclicRecursive(firstNode, new ArrayList<>());

        ComponentDiscoveryList discoveryList = validator.createDiscoveryList(recursivePath, this.applicationContext);
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

    @Test
    @TestComponents(components = {SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithRegularComponent() {
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithAbsentBinding.class)
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    @TestComponents(components = SetterInjectedComponentWithNonRequiredAbsentBinding.class)
    void testSetterInjectionWithAbsentComponent() {
        var component = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
    @TestComponents(components = {SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithContext() {
        SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext.addContext("setter", sampleContext);
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }

    @Inject
    private Logger loggerField;

    @InjectTest
    void loggerCanBeInjected(Logger loggerParameter) {
        Assertions.assertNotNull(loggerParameter);
        // Name should match the consuming class' name, and not the name of the configuration that uses it
        Assertions.assertEquals(loggerParameter.getName(), ApplicationContextTests.class.getName());

        Assertions.assertNotNull(this.loggerField);
        Assertions.assertEquals(this.loggerField.getName(), ApplicationContextTests.class.getName());
    }

    @Test
    void testStringProvision() {
        ComponentKey<String> key = ComponentKey.of(String.class, "license");
        this.applicationContext.bind(key).singleton("MIT");
        String license = this.applicationContext.get(key);
        Assertions.assertEquals("MIT", license);
    }

    @Test
    @HartshornTest(includeBasePackages = false, processors = NonProcessableTypeProcessor.class)
    @TestComponents(components = NonProcessableType.class)
    void testNonProcessableComponent() {
        NonProcessableType nonProcessableType = this.applicationContext.get(NonProcessableType.class);
        Assertions.assertNotNull(nonProcessableType);
        Assertions.assertNull(nonProcessableType.nonNullIfProcessed());
    }

    @Test
    void testPrioritySingletonBinding() {
        this.applicationContext.bind(String.class).singleton("Hello world!");
        this.applicationContext.bind(String.class).priority(0).singleton("Hello modified world!");

        String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).singleton("Hello low priority world!");
        String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }

    @Test
    void testPrioritySupplierBinding() {
        this.applicationContext.bind(String.class).to(() -> "Hello world!");
        this.applicationContext.bind(String.class).priority(0).to(() -> "Hello modified world!");

        String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).to(() -> "Hello low priority world!");
        String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }

    @Test
    void testFailureInComponentConstructorYieldsInitializationException() {
        ComponentInitializationException exception = Assertions.assertThrows(ComponentInitializationException.class, () -> this.applicationContext.get(ErrorInConstructorObject.class));
        Throwable cause = exception.getCause();
        Assertions.assertNotNull(cause);
        Assertions.assertTrue(cause instanceof ApplicationException);

        ApplicationException applicationException = (ApplicationException) cause;
        Assertions.assertEquals("Failed to create instance of type " + ErrorInConstructorObject.class.getName(), applicationException.getMessage());
    }

    @Test
    @TestComponents(components = ProviderService.class)
    void testProviderService() {
        ProviderService service = this.applicationContext.get(ProviderService.class);
        Assertions.assertNotNull(service);
        Assertions.assertTrue(service instanceof Proxy);
        SampleType type = service.get();
        Assertions.assertNotNull(type);
    }
}
