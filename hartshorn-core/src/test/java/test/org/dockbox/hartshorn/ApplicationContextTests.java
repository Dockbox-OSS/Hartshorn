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

package test.org.dockbox.hartshorn;

import java.util.List;
import java.util.stream.Stream;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.component.ContextualComponentPopulator;
import org.dockbox.hartshorn.inject.ComponentInitializationException;
import org.dockbox.hartshorn.inject.ComponentDiscoveryList;
import org.dockbox.hartshorn.inject.ComponentDiscoveryList.DiscoveredComponent;
import org.dockbox.hartshorn.inject.CyclicComponentException;
import org.dockbox.hartshorn.inject.ComponentConstructorResolver;
import org.dockbox.hartshorn.inject.processing.UseContextInjection;
import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestBinding;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.boot.EmptyService;
import test.org.dockbox.hartshorn.components.BoundCircularDependencyA;
import test.org.dockbox.hartshorn.components.BoundCircularDependencyB;
import test.org.dockbox.hartshorn.components.CircularConstructorA;
import test.org.dockbox.hartshorn.components.CircularConstructorB;
import test.org.dockbox.hartshorn.components.CircularDependencyA;
import test.org.dockbox.hartshorn.components.CircularDependencyB;
import test.org.dockbox.hartshorn.components.ComponentType;
import test.org.dockbox.hartshorn.components.ContextInjectedType;
import test.org.dockbox.hartshorn.components.ImplicitCircularDependencyA;
import test.org.dockbox.hartshorn.components.ImplicitCircularDependencyB;
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
import test.org.dockbox.hartshorn.components.SampleContext;
import test.org.dockbox.hartshorn.components.SampleField;
import test.org.dockbox.hartshorn.components.SampleFieldImplementation;
import test.org.dockbox.hartshorn.components.SampleImplementation;
import test.org.dockbox.hartshorn.components.SampleInterface;
import test.org.dockbox.hartshorn.components.SampleMetaAnnotatedImplementation;
import test.org.dockbox.hartshorn.components.SampleProviderService;
import test.org.dockbox.hartshorn.components.SampleProviders;
import test.org.dockbox.hartshorn.components.SampleType;
import test.org.dockbox.hartshorn.components.SetterInjectedComponent;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithAbsentBinding;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithNonRequiredAbsentBinding;
import test.org.dockbox.hartshorn.components.TypeWithEnabledInjectField;
import test.org.dockbox.hartshorn.components.TypeWithFailingConstructor;
import test.org.dockbox.hartshorn.components.contextual.ErrorInConstructorObject;

@HartshornTest(includeBasePackages = false)
@UseContextInjection
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

    // TODO: Restore, types were moved to test fixtures, so need new test components
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
    @TestComponents(components = SampleProviders.class)
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
    @TestComponents(components = TypeWithEnabledInjectField.class)
    void testEnabledInjectDoesNotInjectTwice() {
        TypeWithEnabledInjectField instance = this.applicationContext.get(TypeWithEnabledInjectField.class);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.singletonEnableable());
        Assertions.assertEquals(1, instance.singletonEnableable().enabled());
    }

    @Test
    @TestComponents(bindings = @TestBinding(type = SampleInterface.class, implementation = SampleImplementation.class))
    public void testTypesCanBePopulated() {
        PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        new ContextualComponentPopulator(this.applicationContext).populate(populatedType);
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
    @TestComponents(components = {SampleFieldImplementation.class, SampleProviderService.class})
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

    @Test
    void testContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.add(new SampleContext(contextName));
        
        ContextualComponentPopulator populator = new ContextualComponentPopulator(this.applicationContext);
        ContextInjectedType instance = populator.populate(new ContextInjectedType());
        
        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals(contextName, instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        String contextName = "InjectedContext";
        this.applicationContext.add("another", new SampleContext(contextName));
        
        ContextualComponentPopulator populator = new ContextualComponentPopulator(this.applicationContext);
        ContextInjectedType instance = populator.populate(new ContextInjectedType());
        
        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals(contextName, instance.anotherContext().name());
    }

    @Test
    @TestComponents(components = EmptyService.class)
    void servicesAreSingletonsByDefault() {
        Assertions.assertTrue(this.applicationContext.environment().singleton(EmptyService.class));

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

    public static Stream<Arguments> circular() {
        return Stream.of(
                Arguments.of(CircularConstructorA.class, new Class<?>[] {CircularConstructorA.class, CircularConstructorB.class}),
                Arguments.of(CircularConstructorB.class, new Class<?>[] {CircularConstructorB.class, CircularConstructorA.class}),
                Arguments.of(LongCycleA.class, new Class<?>[] {LongCycleA.class, LongCycleB.class, LongCycleC.class, LongCycleD.class}),
                Arguments.of(LongCycleB.class, new Class<?>[] {LongCycleB.class, LongCycleC.class, LongCycleD.class, LongCycleA.class}),
                Arguments.of(LongCycleC.class, new Class<?>[] {LongCycleC.class, LongCycleD.class, LongCycleA.class, LongCycleB.class}),
                Arguments.of(LongCycleD.class, new Class<?>[] {LongCycleD.class, LongCycleA.class, LongCycleB.class, LongCycleC.class})
        );
    }

    @ParameterizedTest
    @MethodSource("circular")
    @TestComponents(components = {
            CircularDependencyA.class,
            CircularDependencyB.class,
    })
    void testCircularDependencyPathCanBeDetermined(Class<?> type, Class<?>... expected) {
        TypeView<?> typeView = this.applicationContext.environment().introspector().introspect(type);
        ComponentDiscoveryList path = ComponentConstructorResolver.create(applicationContext).findCyclicPath(typeView);
        
        Assertions.assertNotNull(path);
        List<DiscoveredComponent> discoveredComponents = path.discoveredComponents();
        Assertions.assertEquals(expected.length, discoveredComponents.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertSame(expected[i], discoveredComponents.get(i).node().type().type());
        }
    }

    @ParameterizedTest
    @MethodSource("circular")
    @TestComponents(components = {
            CircularDependencyA.class,
            CircularDependencyB.class,
    })
    void testExceptionIsThrownOnCyclicProvision(Class<?> type, Class<?>... path) {
        ComponentInitializationException exception = Assertions.assertThrows(ComponentInitializationException.class, () -> this.applicationContext.get(type));
        Assertions.assertTrue(exception.getCause() instanceof CyclicComponentException);
    }

    @Test
    @TestComponents(bindings = {
            @TestBinding(type = InterfaceCircularDependencyA.class, implementation = BoundCircularDependencyA.class),
            @TestBinding(type = InterfaceCircularDependencyB.class, implementation = BoundCircularDependencyB.class)
    })
    void testCircularDependencyPathOnExplicitBoundTypeCanBeDetermined() {
        TypeView<?> typeView = this.applicationContext.environment().introspector().introspect(InterfaceCircularDependencyA.class);
        ComponentDiscoveryList path = ComponentConstructorResolver.create(applicationContext).findCyclicPath(typeView);
        Assertions.assertNotNull(path);

        List<DiscoveredComponent> discoveredComponents = path.discoveredComponents();
        Assertions.assertEquals(3, discoveredComponents.size());
        Assertions.assertSame(InterfaceCircularDependencyA.class, discoveredComponents.get(0).node().type().type());
        Assertions.assertSame(BoundCircularDependencyB.class, discoveredComponents.get(1).node().type().type());
        Assertions.assertSame(BoundCircularDependencyA.class, discoveredComponents.get(2).node().type().type());
    }

    @Test
    @TestComponents(bindings = {
            @TestBinding(type = InterfaceCircularDependencyA.class, implementation = ImplicitCircularDependencyA.class),
            @TestBinding(type = InterfaceCircularDependencyB.class, implementation = ImplicitCircularDependencyB.class)
    })
    void testCircularDependencyPathOnImplicitBoundTypeCanBeDetermined() {
        TypeView<?> typeView = this.applicationContext.environment().introspector().introspect(InterfaceCircularDependencyA.class);
        ComponentDiscoveryList path = ComponentConstructorResolver.create(applicationContext).findCyclicPath(typeView);
        Assertions.assertNotNull(path);

        List<DiscoveredComponent> discoveredComponents = path.discoveredComponents();
        Assertions.assertEquals(2, discoveredComponents.size());
        DiscoveredComponent componentA = discoveredComponents.get(0);
        Assertions.assertSame(InterfaceCircularDependencyA.class, componentA.node().type().type());
        Assertions.assertSame(ImplicitCircularDependencyA.class, componentA.actualType().type());

        DiscoveredComponent componentB = discoveredComponents.get(1);
        Assertions.assertSame(InterfaceCircularDependencyB.class, componentB.node().type().type());
        Assertions.assertSame(ImplicitCircularDependencyB.class, componentB.actualType().type());
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
        Assertions.assertThrows(ComponentRequiredException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
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
        this.applicationContext.add("setter", sampleContext);
        SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }

    @InjectTest
    void loggerCanBeInjected(Logger logger) {
        Assertions.assertNotNull(logger);
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
