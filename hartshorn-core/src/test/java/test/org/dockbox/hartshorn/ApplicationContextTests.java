/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentRequiredException;
import org.dockbox.hartshorn.component.ComponentResolutionException;
import org.dockbox.hartshorn.inject.CyclicComponentException;
import org.dockbox.hartshorn.inject.CyclingConstructorAnalyzer;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import java.util.List;
import java.util.stream.Stream;

import jakarta.inject.Inject;
import test.org.dockbox.hartshorn.boot.EmptyService;
import test.org.dockbox.hartshorn.components.CircularConstructorA;
import test.org.dockbox.hartshorn.components.CircularConstructorB;
import test.org.dockbox.hartshorn.components.CircularDependencyA;
import test.org.dockbox.hartshorn.components.CircularDependencyB;
import test.org.dockbox.hartshorn.components.ComponentType;
import test.org.dockbox.hartshorn.components.ContextInjectedType;
import test.org.dockbox.hartshorn.components.FieldProviderService;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleA;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleB;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleC;
import test.org.dockbox.hartshorn.components.LongCycles.LongCycleD;
import test.org.dockbox.hartshorn.components.NonComponentType;
import test.org.dockbox.hartshorn.components.NonProcessableType;
import test.org.dockbox.hartshorn.components.NonProcessableTypeProcessor;
import test.org.dockbox.hartshorn.components.NonProxyComponentType;
import test.org.dockbox.hartshorn.components.PassThroughFactory;
import test.org.dockbox.hartshorn.components.Person;
import test.org.dockbox.hartshorn.components.PersonProviders;
import test.org.dockbox.hartshorn.components.PopulatedType;
import test.org.dockbox.hartshorn.components.ProvidedInterface;
import test.org.dockbox.hartshorn.components.SampleContext;
import test.org.dockbox.hartshorn.components.SampleFactoryService;
import test.org.dockbox.hartshorn.components.SampleField;
import test.org.dockbox.hartshorn.components.SampleFieldImplementation;
import test.org.dockbox.hartshorn.components.SampleImplementation;
import test.org.dockbox.hartshorn.components.SampleInterface;
import test.org.dockbox.hartshorn.components.SampleMetaAnnotatedImplementation;
import test.org.dockbox.hartshorn.components.SampleProviderService;
import test.org.dockbox.hartshorn.components.SampleProviders;
import test.org.dockbox.hartshorn.components.SetterInjectedComponent;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithAbsentBinding;
import test.org.dockbox.hartshorn.components.SetterInjectedComponentWithNonRequiredAbsentBinding;
import test.org.dockbox.hartshorn.components.TypeWithEnabledInjectField;
import test.org.dockbox.hartshorn.components.TypeWithFailingConstructor;
import test.org.dockbox.hartshorn.components.User;
import test.org.dockbox.hartshorn.proxy.AbstractProxy;
import test.org.dockbox.hartshorn.proxy.DemoProxyDelegationPostProcessor;
import test.org.dockbox.hartshorn.proxy.ProxyProviders;

@HartshornTest(includeBasePackages = false)
@UseServiceProvision
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

    @Test
    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
    @TestComponents({AbstractProxy.class, ProxyProviders.class})
    void testMethodCanDelegateToImplementation() {
        final AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
        Assertions.assertEquals("concrete", abstractProxy.name());
    }

    @Test
    @HartshornTest(includeBasePackages = false, processors = DemoProxyDelegationPostProcessor.class)
    @TestComponents({AbstractProxy.class, ProxyProviders.class})
    void testMethodOverrideDoesNotDelegateToImplementation() {
        final AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
        Assertions.assertEquals(21, abstractProxy.age());
    }

    @Test
    public void testStaticBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation.class);
        final SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() {
        final ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation.class);
        final SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).singleton(new SampleImplementation());
        final SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() {
        final ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).singleton(new SampleImplementation());
        final SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation::new);
        final SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() {
        final ComponentKey<SampleInterface> key = ComponentKey.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation::new);
        final SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    @TestComponents(SampleProviders.class)
    public void testScannedMetaBindingsCanBeProvided() {

        // Ensure that the binding is not bound to the default name
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(SampleInterface.class));

        final SampleInterface provided = this.applicationContext.get(ComponentKey.of(SampleInterface.class, "meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    @TestComponents(TypeWithEnabledInjectField.class)
    void testEnabledInjectDoesNotInjectTwice() {
        final TypeWithEnabledInjectField instance = this.applicationContext.get(TypeWithEnabledInjectField.class);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.singletonEnableable());
        Assertions.assertEquals(1, instance.singletonEnableable().enabled());
    }

    @Test
    public void testTypesCanBePopulated() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation.class);
        final PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        this.applicationContext.get(ComponentPopulator.class).populate(populatedType);
        Assertions.assertNotNull(populatedType.sampleInterface());
        Assertions.assertEquals("Hartshorn", populatedType.sampleInterface().name());
    }

    @Test
    @TestComponents(PopulatedType.class)
    public void unboundTypesCanBeProvided() {
        this.applicationContext.bind(SampleInterface.class).to(SampleImplementation.class);
        final PopulatedType provided = this.applicationContext.get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.sampleInterface());
    }

    @ParameterizedTest
    @MethodSource("providers")
    @TestComponents({SampleFieldImplementation.class, SampleProviderService.class})
    void testProvidersCanApply(final String meta, final String name, final boolean field, final String fieldMeta, final boolean singleton) {
        if (field) {
            if (fieldMeta == null) {this.applicationContext.bind(SampleField.class).to(SampleFieldImplementation.class);}
            else this.applicationContext.bind(ComponentKey.of(SampleField.class, fieldMeta)).to(SampleFieldImplementation.class);
        }

        final ProvidedInterface provided;
        if (meta == null) provided = this.applicationContext.get(ProvidedInterface.class);
        else provided = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
        Assertions.assertNotNull(provided);

        final String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            final ProvidedInterface second;
            if (meta == null) second = this.applicationContext.get(ProvidedInterface.class);
            else second = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    @TestComponents(FieldProviderService.class)
    void testFieldProviders() {
        final ProvidedInterface field = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, "field"));
        Assertions.assertNotNull(field);
        Assertions.assertEquals("Field", field.name());
    }

    @Test
    @TestComponents(FieldProviderService.class)
    void testSingletonFieldProviders() {
        final ProvidedInterface field = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, "singletonField"));
        Assertions.assertNotNull(field);

        final ProvidedInterface field2 = this.applicationContext.get(ComponentKey.of(ProvidedInterface.class, "singletonField"));
        Assertions.assertNotNull(field2);

        Assertions.assertSame(field, field2);
    }

    @Test
    void testContextFieldsAreInjected() {
        this.applicationContext.add(new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.applicationContext.get(ComponentPopulator.class).populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals("InjectedContext", instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        this.applicationContext.add("another", new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.applicationContext.get(ComponentPopulator.class).populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals("InjectedContext", instance.anotherContext().name());
    }

    @Test
    @TestComponents({SampleFactoryService.class, PersonProviders.class})
    void testFactoryProviderCanProvide() {
        final User sample = this.applicationContext.get(SampleFactoryService.class).user("Factory");
        Assertions.assertNotNull(sample);
        Assertions.assertNotNull(sample.name());
        Assertions.assertEquals("Factory", sample.name());
    }

    @Test
    @TestComponents({PassThroughFactory.class, PersonProviders.class})
    void testFactoryAllowsPassThroughDefaults() {
        final PassThroughFactory factoryDemo = this.applicationContext.get(PassThroughFactory.class);
        final Person person = factoryDemo.create("Bob");
        Assertions.assertNotNull(person);
    }

    @Test
    @TestComponents(EmptyService.class)
    void servicesAreSingletonsByDefault() {
        Assertions.assertTrue(this.applicationContext.environment().singleton(EmptyService.class));

        final EmptyService emptyService = this.applicationContext.get(EmptyService.class);
        final EmptyService emptyService2 = this.applicationContext.get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonComponentType.class));
    }

    @Test
    @TestComponents(ComponentType.class)
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        final ComponentType instance = this.applicationContext.get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext.environment().isProxy(instance));
    }

    @Test
    @TestComponents(NonProxyComponentType.class)
    void testNonPermittedComponentsAreNotProxied() {
        Assertions.assertThrows(ComponentResolutionException.class, () -> this.applicationContext.get(NonProxyComponentType.class));
    }

    @Test
    void testFailingConstructorIsRethrown() {
        Assertions.assertThrows(IllegalStateException.class, () -> this.applicationContext.get(TypeWithFailingConstructor.class));
    }

    @Test
    @TestComponents({CircularDependencyA.class, CircularDependencyB.class})
    void testCircularDependenciesAreCorrectOnFieldInject() {
        final CircularDependencyA a = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyA.class));
        final CircularDependencyB b = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyB.class));

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);

        Assertions.assertSame(a, b.a());
        Assertions.assertSame(b, a.b());
    }

    public static Stream<Arguments> circular() {
        return Stream.of(
                Arguments.of(CircularConstructorA.class, new Class<?>[] {CircularConstructorA.class, CircularConstructorB.class, CircularConstructorA.class}),
                Arguments.of(CircularConstructorB.class, new Class<?>[] {CircularConstructorB.class, CircularConstructorA.class, CircularConstructorB.class}),
                Arguments.of(LongCycleA.class, new Class<?>[] {LongCycleA.class, LongCycleB.class, LongCycleC.class, LongCycleD.class, LongCycleA.class}),
                Arguments.of(LongCycleB.class, new Class<?>[] {LongCycleB.class, LongCycleC.class, LongCycleD.class, LongCycleA.class, LongCycleB.class}),
                Arguments.of(LongCycleC.class, new Class<?>[] {LongCycleC.class, LongCycleD.class, LongCycleA.class, LongCycleB.class, LongCycleC.class}),
                Arguments.of(LongCycleD.class, new Class<?>[] {LongCycleD.class, LongCycleA.class, LongCycleB.class, LongCycleC.class, LongCycleD.class})
        );
    }

    @ParameterizedTest
    @MethodSource("circular")
    @TestComponents({
            CircularDependencyA.class,
            CircularDependencyB.class,
    })
    void testCircularDependencyPathCanBeDetermined(final Class<?> type, final Class<?>... expected) {
        final TypeView<?> typeView = this.applicationContext.environment().introspect(type);
        final List<TypeView<?>> path = CyclingConstructorAnalyzer.findCyclicPath(typeView);
        
        Assertions.assertNotNull(path);
        Assertions.assertEquals(expected.length, path.size());
        for (int i = 0; i < expected.length; i++) {
            Assertions.assertSame(expected[i], path.get(i).type());
        }
    }

    @ParameterizedTest
    @MethodSource("circular")
    @TestComponents({
            CircularDependencyA.class,
            CircularDependencyB.class,
    })
    void testExceptionIsThrownOnCyclicProvision(final Class<?> type, final Class<?>... path) {
        Assertions.assertThrows(CyclicComponentException.class, () -> this.applicationContext.get(type));
    }

    @Test
    @TestComponents({SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithRegularComponent() {
        final SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    @TestComponents(SetterInjectedComponentWithAbsentBinding.class)
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ComponentRequiredException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    @TestComponents(SetterInjectedComponentWithNonRequiredAbsentBinding.class)
    void testSetterInjectionWithAbsentComponent() {
        final var component = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
    @TestComponents({SetterInjectedComponent.class, ComponentType.class})
    void testSetterInjectionWithContext() {
        final SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext.add("setter", sampleContext);
        final SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }

    @InjectTest
    void loggerCanBeInjected(final Logger logger) {
        Assertions.assertNotNull(logger);
    }

    @Test
    void testStringProvision() {
        final ComponentKey<String> key = ComponentKey.of(String.class, "license");
        this.applicationContext.bind(key).singleton("MIT");
        final String license = this.applicationContext.get(key);
        Assertions.assertEquals("MIT", license);
    }

    @Test
    @HartshornTest(includeBasePackages = false, processors = NonProcessableTypeProcessor.class)
    @TestComponents(NonProcessableType.class)
    void testNonProcessableComponent() {
        final NonProcessableType nonProcessableType = this.applicationContext.get(NonProcessableType.class);
        Assertions.assertNotNull(nonProcessableType);
        Assertions.assertNull(nonProcessableType.nonNullIfProcessed());
    }

    @Test
    void testPrioritySingletonBinding() {
        this.applicationContext.bind(String.class).singleton("Hello world!");
        this.applicationContext.bind(String.class).priority(0).singleton("Hello modified world!");

        final String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).singleton("Hello low priority world!");
        final String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }

    @Test
    void testPrioritySupplierBinding() {
        this.applicationContext.bind(String.class).to(() -> "Hello world!");
        this.applicationContext.bind(String.class).priority(0).to(() -> "Hello modified world!");

        final String binding = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding);

        this.applicationContext.bind(String.class).priority(-2).to(() -> "Hello low priority world!");
        final String binding2 = this.applicationContext.get(String.class);
        Assertions.assertEquals("Hello modified world!", binding2);
    }
}
