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

package org.dockbox.hartshorn.di;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.di.types.InvalidSampleBoundType;
import org.dockbox.hartshorn.di.types.PopulatedType;
import org.dockbox.hartshorn.di.types.SampleBoundPopulatedType;
import org.dockbox.hartshorn.di.types.SampleBoundType;
import org.dockbox.hartshorn.di.types.SampleField;
import org.dockbox.hartshorn.di.types.SampleFieldImplementation;
import org.dockbox.hartshorn.di.types.SampleImplementation;
import org.dockbox.hartshorn.di.types.SampleInterface;
import org.dockbox.hartshorn.di.types.bound.SampleBoundAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.meta.SampleMetaAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.multi.SampleMultiAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.provision.ProvidedInterface;
import org.dockbox.hartshorn.di.types.scan.SampleAnnotatedImplementation;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.Reflect;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class ApplicationContextTests {

    @RegisterExtension
    static HartshornRunner hartshorn = HartshornRunner.builder()
            .resetEach(true)
            .build();

    @Test
    public void testStaticBindingCanBeProvided() {
        Hartshorn.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() {
        Hartshorn.context().bind(Key.of(SampleInterface.class, Bindings.named("demo")), SampleImplementation.class);
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingCanBeProvided() {
        Hartshorn.context().bind(Key.of(SampleInterface.class), new SampleImplementation());
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() {
        Hartshorn.context().bind(Key.of(SampleInterface.class, Bindings.named("demo")), new SampleImplementation());
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingCanBeProvided() {
        Hartshorn.context().provide(Key.of(SampleInterface.class), SampleImplementation::new);
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() {
        Hartshorn.context().provide(Key.of(SampleInterface.class, Bindings.named("demo")), SampleImplementation::new);
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testScannedBindingCanBeProvided() {
        // sub-package *.scan was added to prevent scan conflicts
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.scan");
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() {
        // sub-package *.meta was added to prevent scan conflicts
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.meta");
        final SampleInterface sample = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertTrue(Reflect.isProxy(sample));
        Assertions.assertThrows(AbstractMethodError.class, sample::name);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, Bindings.named("meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMultiBindingsCanBeProvided() {
        // sub-package *.multi was added to prevent scan conflicts
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.multi");

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMultiMetaBindingsCanBeProvided() {
        // sub-package *.multi was added to prevent scan conflicts
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.multi");

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, Bindings.named("meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testConfigBindingCanBeProvided() {
        Hartshorn.context().bind(new SampleConfiguration());
        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testTypesCanBePopulated() {
        Hartshorn.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        Hartshorn.context().populate(populatedType);
        Assertions.assertNotNull(populatedType.sampleInterface());
        Assertions.assertEquals("Hartshorn", populatedType.sampleInterface().name());
    }

    @Test
    public void unboundTypesCanBeProvided() {
        Hartshorn.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType provided = Hartshorn.context().get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.sampleInterface());
    }

    @Test
    public void injectionPointsArePrioritised() {
        Hartshorn.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleAnnotatedImplementation());
        Hartshorn.context().add(point);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);
        Assertions.assertEquals(SampleAnnotatedImplementation.class, provided.getClass());
    }

    @Test
    public void invalidBoundTypesCannotBeBound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Hartshorn.context().manual(Key.of(SampleInterface.class), InvalidSampleBoundType.class)
        );
    }

    @Test
    public void boundTypesCanBeProvided() {
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundType.class);
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        final SampleInterface wired = Hartshorn.context().get(TypeFactory.class).create(SampleInterface.class, "BoundHartshorn");
        Assertions.assertNotNull(wired);
        Assertions.assertEquals("BoundHartshorn", wired.name());
    }

    @Test
    public void testScannedBoundBindingsCanBeProvided() {
        // sub-package *.bound was added to prevent scan conflicts
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.bound");
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        final SampleInterface provided = Hartshorn.context().get(TypeFactory.class).create(SampleInterface.class, "BoundAnnotated");
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("BoundAnnotated", provided.name());
    }

    @Test
    public void boundTypesCanBeProvidedThroughFactoryProperty() {
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundType.class);
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundType.class);
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, TypeFactory.use("FactoryTyped"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.name());
    }

    @Test
    public void providerRedirectsVarargs() {
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundType.class);
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.name());
    }

    @Test
    public void varargProvidedTypesArePopulated() {
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundPopulatedType.class);
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);
        Hartshorn.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleBoundPopulatedType);
        Assertions.assertNotNull(((SampleBoundPopulatedType) provided).field());
        Assertions.assertTrue(((SampleBoundPopulatedType) provided).field() instanceof SampleFieldImplementation);
    }

    @Test
    public void injectionPointsAreAppliedToVarargProviders() {
        Hartshorn.context().manual(Key.of(SampleInterface.class), SampleBoundType.class);
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);
        Hartshorn.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);

        final InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleImplementation());
        Hartshorn.context().add(point);

        final SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertFalse(provided instanceof SampleBoundType);
        Assertions.assertTrue(provided instanceof SampleImplementation);
    }

    private static Stream<Arguments> providers() {
        return Stream.of(
                Arguments.of(null, "Provision", false, null, false),
                Arguments.of("named", "NamedProvision", false, null, false),
                Arguments.of("field", "FieldProvision", true, null, false),
                Arguments.of("namedField", "NamedFieldProvision", true, "named", false),
                Arguments.of("singleton", "SingletonProvision", false, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("providers")
    void testProvidersCanApply(final String meta, final String name, final boolean field, final String fieldMeta, final boolean singleton) {
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.provision");

        if (field) {
            if (fieldMeta == null) {Hartshorn.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);}
            else Hartshorn.context().bind(Key.of(SampleField.class, Bindings.named(fieldMeta)), SampleFieldImplementation.class);
        }

        final ProvidedInterface provided;
        if (meta == null) provided = Hartshorn.context().get(ProvidedInterface.class);
        else provided = Hartshorn.context().get(ProvidedInterface.class, Bindings.named(meta));
        Assertions.assertNotNull(provided);

        final String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            final ProvidedInterface second;
            if (meta == null) second = Hartshorn.context().get(ProvidedInterface.class);
            else second = Hartshorn.context().get(ProvidedInterface.class, Bindings.named(meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    void testBoundProviderCanSupply() {
        Hartshorn.context().bind("org.dockbox.hartshorn.di.types.provision");
        Hartshorn.context().bind(Key.of(TypeFactory.class), SimpleTypeFactory.class);

        final ProvidedInterface provided = Hartshorn.context().get(ProvidedInterface.class, BindingMetaAttribute.of("bound"), TypeFactory.use("BoundProvision"));
        Assertions.assertNotNull(provided);
        Assertions.assertEquals("BoundProvision", provided.name());
    }
}
