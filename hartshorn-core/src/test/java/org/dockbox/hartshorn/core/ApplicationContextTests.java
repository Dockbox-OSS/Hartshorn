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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.core.boot.EmptyService;
import org.dockbox.hartshorn.core.proxy.AbstractProxy;
import org.dockbox.hartshorn.core.proxy.DemoProxyDelegationPostProcessor;
import org.dockbox.hartshorn.core.types.CircularConstructorA;
import org.dockbox.hartshorn.core.types.CircularConstructorB;
import org.dockbox.hartshorn.core.types.CircularDependencyA;
import org.dockbox.hartshorn.core.types.CircularDependencyB;
import org.dockbox.hartshorn.core.types.ComponentType;
import org.dockbox.hartshorn.core.types.ContextInjectedType;
import org.dockbox.hartshorn.core.types.NonComponentType;
import org.dockbox.hartshorn.core.types.NonProcessableType;
import org.dockbox.hartshorn.core.types.NonProcessableTypeProcessor;
import org.dockbox.hartshorn.core.types.NonProxyComponentType;
import org.dockbox.hartshorn.core.types.Person;
import org.dockbox.hartshorn.core.types.SampleContext;
import org.dockbox.hartshorn.core.types.SetterInjectedComponent;
import org.dockbox.hartshorn.core.types.SetterInjectedComponentWithAbsentBinding;
import org.dockbox.hartshorn.core.types.SetterInjectedComponentWithNonRequiredAbsentBinding;
import org.dockbox.hartshorn.core.types.TypeWithEnabledInjectField;
import org.dockbox.hartshorn.core.types.TypeWithFailingConstructor;
import org.dockbox.hartshorn.core.types.User;
import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.inject.processing.UseServiceProvision;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.dockbox.hartshorn.testsuite.TestComponents;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.reflect.CyclicComponentException;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;

import java.util.stream.Stream;

import javax.inject.Inject;

import test.types.FieldProviderService;
import test.types.PopulatedType;
import test.types.ProvidedInterface;
import test.types.SampleAnnotatedImplementation;
import test.types.SampleField;
import test.types.SampleFieldImplementation;
import test.types.SampleImplementation;
import test.types.SampleInterface;
import test.types.SampleMetaAnnotatedImplementation;
import test.types.SampleProviderService;

@HartshornTest
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
    @HartshornTest(processors = DemoProxyDelegationPostProcessor.class)
    void testMethodCanDelegateToImplementation() {
        final AbstractProxy abstractProxy = this.applicationContext.get(AbstractProxy.class);
        Assertions.assertEquals("concrete", abstractProxy.name());
    }

    @Test
    @HartshornTest(processors = DemoProxyDelegationPostProcessor.class)
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
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
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
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
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
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
        this.applicationContext.bind(key).to(SampleImplementation::new);
        final SampleInterface provided = this.applicationContext.get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    @TestComponents(SampleAnnotatedImplementation.class)
    public void testScannedBindingCanBeProvided() {
        final SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.name());
    }

    @Test
    @TestComponents(SampleMetaAnnotatedImplementation.class)
    public void testScannedMetaBindingsCanBeProvided() {

        // Ensure that the binding is not bound to the default name
        final SampleInterface sample = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNull(sample); // Non-component, so null

        final SampleInterface provided = this.applicationContext.get(Key.of(SampleInterface.class, "meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    void testEnabledInjectDoesNotInjectTwice() {
        final TypeWithEnabledInjectField instance = this.applicationContext.get(TypeWithEnabledInjectField.class);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.singletonEnableable());
        Assertions.assertEquals(1, instance.singletonEnableable().enabled());
    }

    @Test
    public void testConfigBindingCanBeProvided() {
        this.applicationContext.bind(new SampleConfiguration());
        final SampleInterface provided = this.applicationContext.get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
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
            else this.applicationContext.bind(Key.of(SampleField.class, fieldMeta)).to(SampleFieldImplementation.class);
        }

        final ProvidedInterface provided;
        if (meta == null) provided = this.applicationContext.get(ProvidedInterface.class);
        else provided = this.applicationContext.get(Key.of(ProvidedInterface.class, meta));
        Assertions.assertNotNull(provided);

        final String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            final ProvidedInterface second;
            if (meta == null) second = this.applicationContext.get(ProvidedInterface.class);
            else second = this.applicationContext.get(Key.of(ProvidedInterface.class, meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    @TestComponents(FieldProviderService.class)
    void testFieldProviders() {
        final ProvidedInterface field = this.applicationContext.get(Key.of(ProvidedInterface.class, "field"));
        Assertions.assertNotNull(field);
        Assertions.assertEquals("Field", field.name());
    }

    @Test
    @TestComponents(FieldProviderService.class)
    void testSingletonFieldProviders() {
        final ProvidedInterface field = this.applicationContext.get(Key.of(ProvidedInterface.class, "singletonField"));
        Assertions.assertNotNull(field);

        final ProvidedInterface field2 = this.applicationContext.get(Key.of(ProvidedInterface.class, "singletonField"));
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
    void testFactoryProviderCanProvide() {
        final User sample = this.applicationContext.get(SampleFactoryService.class).user("Factory");
        Assertions.assertNotNull(sample);
        Assertions.assertNotNull(sample.name());
        Assertions.assertEquals("Factory", sample.name());
    }

    @Test
    void testFactoryAllowsPassThroughDefaults() {
        final PassThroughFactory factoryDemo = this.applicationContext.get(PassThroughFactory.class);
        final Person person = factoryDemo.create("Bob");
        Assertions.assertNotNull(person);
    }

    @Test
    void servicesAreSingletonsByDefault() {
        Assertions.assertTrue(this.applicationContext.meta().singleton(TypeContext.of(EmptyService.class)));

        final EmptyService emptyService = this.applicationContext.get(EmptyService.class);
        final EmptyService emptyService2 = this.applicationContext.get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        final NonComponentType instance = this.applicationContext.get(NonComponentType.class);
        Assertions.assertNull(instance);
    }

    @Test
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        final ComponentType instance = this.applicationContext.get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext.environment().manager().isProxy(instance));
    }

    @Test
    void testNonPermittedComponentsAreNotProxied() {
        final NonProxyComponentType instance = this.applicationContext.get(NonProxyComponentType.class);
        Assertions.assertNull(instance);
    }

    @Test
    void testFailingConstructorIsRethrown() {
        Assertions.assertThrows(IllegalStateException.class, () -> this.applicationContext.get(TypeWithFailingConstructor.class));
    }

    @Test
    void testCircularDependenciesAreCorrectOnFieldInject() {
        final CircularDependencyA a = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyA.class));
        final CircularDependencyB b = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(CircularDependencyB.class));

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);

        Assertions.assertSame(a, b.a());
        Assertions.assertSame(b, a.b());
    }

    @Test
    void testCircularDependenciesYieldExceptionOnConstructorInject() {
        Assertions.assertThrows(CyclicComponentException.class, () -> this.applicationContext.get(CircularConstructorA.class));
        Assertions.assertThrows(CyclicComponentException.class, () -> this.applicationContext.get(CircularConstructorB.class));
    }

    @Test
    void testSetterInjectionWithRegularComponent() {
        final SetterInjectedComponent component = this.applicationContext.get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ApplicationException.class, () -> this.applicationContext.get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    void testSetterInjectionWithAbsentComponent() {
        final var component = Assertions.assertDoesNotThrow(() -> this.applicationContext.get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
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
        final Key<String> key = Key.of(String.class, "license");
        this.applicationContext.bind(key).singleton("MIT");
        final String license = this.applicationContext.get(key);
        Assertions.assertEquals("MIT", license);
    }

    @Test
    @HartshornTest(processors = NonProcessableTypeProcessor.class)
    void testNonProcessableComponent() {
        final NonProcessableType nonProcessableType = this.applicationContext.get(NonProcessableType.class);
        Assertions.assertNotNull(nonProcessableType);
        Assertions.assertNull(nonProcessableType.nonNullIfProcessed());
    }
}
