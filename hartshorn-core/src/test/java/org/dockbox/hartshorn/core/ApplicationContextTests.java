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
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.BeanProvisionException;
import org.dockbox.hartshorn.core.proxy.ExtendedProxy;
import org.dockbox.hartshorn.core.types.ComponentType;
import org.dockbox.hartshorn.core.types.ContextInjectedType;
import org.dockbox.hartshorn.core.types.NonComponentType;
import org.dockbox.hartshorn.core.types.SampleContext;
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
        Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
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
        Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
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
        Key<SampleInterface> key = Key.of(SampleInterface.class, "demo");
        this.applicationContext().bind(key, (Supplier<SampleInterface>) SampleImplementation::new);
        final SampleInterface provided = this.applicationContext().get(key);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testScannedBindingCanBeProvided() {
        this.applicationContext().bind("test.types.scan");
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() {
        this.applicationContext().bind("test.types.meta");
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
    public void testScannedMultiBindingsCanBeProvided() {
        this.applicationContext().bind("test.types.multi");
        final SampleInterface provided = this.applicationContext().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMultiMetaBindingsCanBeProvided() {
        this.applicationContext().bind("test.types.multi");
        final SampleInterface provided = this.applicationContext().get(Key.of(SampleInterface.class, "meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
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
        this.applicationContext().bind("test.types.provision");
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
    void testComponentsAreProxiedWhenRegularProvisionFails() {
        final ComponentType instance = this.applicationContext().get(ComponentType.class);
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(this.applicationContext().environment().manager().isProxy(instance));
    }

    @Test
    void testFailingConstructorIsRethrown() {
        final BeanProvisionException exception = Assertions.assertThrows(BeanProvisionException.class, () -> this.applicationContext().get(TypeWithFailingConstructor.class));
        Assertions.assertTrue(exception.getCause() instanceof IllegalStateException);
        Assertions.assertEquals("This type cannot be instantiated", exception.getCause().getMessage());
    }
}
