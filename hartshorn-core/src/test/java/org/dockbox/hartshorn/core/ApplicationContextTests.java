/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.boot.EmptyService;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.HartshornApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.exceptions.CyclicComponentException;
import org.dockbox.hartshorn.core.proxy.ExtendedProxy;
import org.dockbox.hartshorn.core.types.CircularConstructorA;
import org.dockbox.hartshorn.core.types.CircularConstructorB;
import org.dockbox.hartshorn.core.types.CircularDependencyA;
import org.dockbox.hartshorn.core.types.CircularDependencyB;
import org.dockbox.hartshorn.core.types.ComponentType;
import org.dockbox.hartshorn.core.types.ContextInjectedType;
import org.dockbox.hartshorn.core.types.NonComponentType;
import org.dockbox.hartshorn.core.types.NonProxyComponentType;
import org.dockbox.hartshorn.core.types.Person;
import org.dockbox.hartshorn.core.types.SampleContext;
import org.dockbox.hartshorn.core.types.SetterInjectedComponent;
import org.dockbox.hartshorn.core.types.SetterInjectedComponentWithAbsentBinding;
import org.dockbox.hartshorn.core.types.SetterInjectedComponentWithNonRequiredAbsentBinding;
import org.dockbox.hartshorn.core.types.TypeWithEnabledInjectField;
import org.dockbox.hartshorn.core.types.TypeWithFailingConstructor;
import org.dockbox.hartshorn.core.types.User;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.inject.Inject;

import lombok.Getter;
import test.types.PopulatedType;
import test.types.SampleField;
import test.types.SampleFieldImplementation;
import test.types.SampleImplementation;
import test.types.SampleInterface;
import test.types.meta.SampleMetaAnnotatedImplementation;
import test.types.multi.SampleMultiAnnotatedImplementation;
import test.types.provision.ProvidedInterface;
import test.types.scan.SampleAnnotatedImplementation;

@HartshornTest
@UseServiceProvision
public class ApplicationContextTests {

    @Inject
    @Getter
    private ApplicationContext applicationContext;

    private static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of(null, "Provision", false, null, false),
                Arguments.of("named", "NamedProvision", false, null, false),
                Arguments.of("field", "FieldProvision", true, null, false),
                Arguments.of("namedField", "NamedFieldProvision", true, "named", false),
                Arguments.of("singleton", "SingletonProvision", false, null, true)
        );
    }

    @Test
    void testContextLoads() {
        Assertions.assertNotNull(this.applicationContext);
    }

    @Test
    void testMethodCanDelegateToImplementation() {
        final ExtendedProxy extendedProxy = this.applicationContext().get(ExtendedProxy.class);
        Assertions.assertEquals("concrete", extendedProxy.name());
    }

    @Test
    void testMethodOverrideDoesNotDelegateToImplementation() {
        final ExtendedProxy extendedProxy = this.applicationContext().get(ExtendedProxy.class);
        Assertions.assertEquals(21, extendedProxy.age());
    }

    @Test
    public void testStaticBindingCanBeProvided() {
        this.applicationContext().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() {
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
        this.applicationContext().bind(key, SampleImplementation.class);
        final SampleInterface provided = this.applicationContext().get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingCanBeProvided() {
        this.applicationContext().bind(Key.of(SampleInterface.class), new SampleImplementation());
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() {
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
        this.applicationContext().bind(key, new SampleImplementation());
        final SampleInterface provided = this.applicationContext().get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingCanBeProvided() {
        this.applicationContext().bind(Key.of(SampleInterface.class), (Supplier<SampleInterface>) SampleImplementation::new);
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() {
        final Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
        this.applicationContext().bind(key, (Supplier<SampleInterface>) SampleImplementation::new);
        final SampleInterface provided = this.applicationContext().get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testScannedBindingCanBeProvided() {
        // This is a bit of a hack, but we need to ensure that the prefix binding is present and processed. Usually
        // you'd do this through a service activator.
        this.applicationContext().bind("test.types.scan");
        ((HartshornApplicationContext) this.applicationContext()).process();

        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() {
        // This is a bit of a hack, but we need to ensure that the prefix binding is present and processed. Usually
        // you'd do this through a service activator.
        this.applicationContext().bind("test.types.meta");
        ((HartshornApplicationContext) this.applicationContext()).process();

        // Ensure that the binding is not bound to the default name
        final SampleInterface sample = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNull(sample); // Non-component, so null

        final SampleInterface provided = this.applicationContext().get(Key.of(SampleInterface.class, "meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    void testEnabledInjectDoesNotInjectTwice() {
        final TypeWithEnabledInjectField instance = this.applicationContext().get(TypeWithEnabledInjectField.class);
        Assertions.assertNotNull(instance);
        Assertions.assertNotNull(instance.singletonEnableable());
        Assertions.assertEquals(1, instance.singletonEnableable().enabled());
    }

    @Test
    public void testConfigBindingCanBeProvided() {
        this.applicationContext().bind(new SampleConfiguration());
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testTypesCanBePopulated() {
        this.applicationContext().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        this.applicationContext().populate(populatedType);
        Assertions.assertNotNull(populatedType.sampleInterface());
        Assertions.assertEquals("Hartshorn", populatedType.sampleInterface().name());
    }

    @Test
    public void unboundTypesCanBeProvided() {
        this.applicationContext().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType provided = this.applicationContext().get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.sampleInterface());
    }

    @ParameterizedTest
    @MethodSource("providers")
    void testProvidersCanApply(final String meta, final String name, final boolean field, final String fieldMeta, final boolean singleton) {
        // This is a bit of a hack, but we need to ensure that the prefix binding is present and processed. Usually
        // you'd do this through a service activator.
        this.applicationContext().bind("test.types.provision");
        ((HartshornApplicationContext) this.applicationContext()).process();

        if (field) {
            if (fieldMeta == null) {this.applicationContext().bind(Key.of(SampleField.class), SampleFieldImplementation.class);}
            else this.applicationContext().bind(Key.of(SampleField.class, fieldMeta), SampleFieldImplementation.class);
        }

        final ProvidedInterface provided;
        if (meta == null) provided = this.applicationContext().get(ProvidedInterface.class);
        else provided = this.applicationContext().get(Key.of(ProvidedInterface.class, meta));
        Assertions.assertNotNull(provided);

        final String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            final ProvidedInterface second;
            if (meta == null) second = this.applicationContext().get(ProvidedInterface.class);
            else second = this.applicationContext().get(Key.of(ProvidedInterface.class, meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    void testContextFieldsAreInjected() {
        this.applicationContext().add(new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.applicationContext().populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals("InjectedContext", instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        this.applicationContext().add("another", new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.applicationContext().populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals("InjectedContext", instance.anotherContext().name());
    }

    @Test
    void testFactoryProviderCanProvide() {
        final User sample = this.applicationContext().get(SampleFactoryService.class).user("Factory");
        Assertions.assertNotNull(sample);
        Assertions.assertNotNull(sample.name());
        Assertions.assertEquals("Factory", sample.name());
    }

    @Test
    void testFactoryAllowsPassThroughDefaults() {
        final PassThroughFactory factoryDemo = this.applicationContext().get(PassThroughFactory.class);
        final Person person = factoryDemo.create("Bob");
        Assertions.assertNotNull(person);
    }

    @Test
    void servicesAreSingletonsByDefault() {
        Assertions.assertTrue(this.applicationContext().meta().singleton(TypeContext.of(EmptyService.class)));

        final EmptyService emptyService = this.applicationContext().get(EmptyService.class);
        final EmptyService emptyService2 = this.applicationContext().get(EmptyService.class);
        Assertions.assertSame(emptyService, emptyService2);
    }

    @Test
    void testNonComponentsAreNotProxied() {
        final NonComponentType instance = this.applicationContext().get(NonComponentType.class);
        Assertions.assertNull(instance);
    }

    @Test
    void testPermittedComponentsAreProxiedWhenRegularProvisionFails() {
        final ComponentType instance = this.applicationContext().get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext().environment().manager().isProxy(instance));
    }

    @Test
    void testNonPermittedComponentsAreNotProxied() {
        final NonProxyComponentType instance = this.applicationContext().get(NonProxyComponentType.class);
        Assertions.assertNull(instance);
    }

    @Test
    void testFailingConstructorIsRethrown() {
        Assertions.assertThrows(IllegalStateException.class, () -> this.applicationContext().get(TypeWithFailingConstructor.class));
    }

    @Test
    void testCircularDependenciesAreCorrectOnFieldInject() {
        final CircularDependencyA a = Assertions.assertDoesNotThrow(() -> this.applicationContext().get(CircularDependencyA.class));
        final CircularDependencyB b = Assertions.assertDoesNotThrow(() -> this.applicationContext().get(CircularDependencyB.class));

        Assertions.assertNotNull(a);
        Assertions.assertNotNull(b);

        Assertions.assertSame(a, b.a());
        Assertions.assertSame(b, a.b());
    }

    @Test
    void testCircularDependenciesYieldExceptionOnConstructorInject() {
        Assertions.assertThrows(CyclicComponentException.class, () -> this.applicationContext().get(CircularConstructorA.class));
        Assertions.assertThrows(CyclicComponentException.class, () -> this.applicationContext().get(CircularConstructorB.class));
    }

    @Test
    void testSetterInjectionWithRegularComponent() {
        final SetterInjectedComponent component = this.applicationContext().get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.component());
    }

    @Test
    void testSetterInjectionWithAbsentRequiredComponent() {
        Assertions.assertThrows(ApplicationException.class, () -> this.applicationContext().get(SetterInjectedComponentWithAbsentBinding.class));
    }

    @Test
    void testSetterInjectionWithAbsentComponent() {
        final var component = Assertions.assertDoesNotThrow(() -> this.applicationContext().get(SetterInjectedComponentWithNonRequiredAbsentBinding.class));
        Assertions.assertNotNull(component);
        Assertions.assertNull(component.object());
    }

    @Test
    void testSetterInjectionWithContext() {
        final SampleContext sampleContext = new SampleContext("setter");
        this.applicationContext().add("setter", sampleContext);
        final SetterInjectedComponent component = this.applicationContext().get(SetterInjectedComponent.class);
        Assertions.assertNotNull(component);
        Assertions.assertNotNull(component.context());
        Assertions.assertSame(sampleContext, component.context());
    }
}
