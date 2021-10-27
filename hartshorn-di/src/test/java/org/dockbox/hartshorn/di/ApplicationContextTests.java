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

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.di.binding.BindingHierarchy;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.binding.BoundFactoryProvider;
import org.dockbox.hartshorn.di.binding.ContextDrivenProvider;
import org.dockbox.hartshorn.di.binding.Provider;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.BindingMetaAttribute;
import org.dockbox.hartshorn.di.properties.UseFactory;
import org.dockbox.hartshorn.di.types.ContextInjectedType;
import org.dockbox.hartshorn.di.types.SampleContext;
import org.dockbox.hartshorn.test.ApplicationAwareTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.function.Supplier;
import java.util.stream.Stream;

import test.types.PopulatedType;
import test.types.SampleBoundPopulatedType;
import test.types.SampleBoundType;
import test.types.SampleField;
import test.types.SampleFieldImplementation;
import test.types.SampleImplementation;
import test.types.SampleInterface;
import test.types.bound.SampleBoundAnnotatedImplementation;
import test.types.dual.DualConstructableType;
import test.types.meta.SampleMetaAnnotatedImplementation;
import test.types.multi.SampleMultiAnnotatedImplementation;
import test.types.provision.ProvidedInterface;
import test.types.scan.SampleAnnotatedImplementation;

@UseServiceProvision
public class ApplicationContextTests extends ApplicationAwareTest {

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
    public void testStaticBindingCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class, Bindings.named("demo")), SampleImplementation.class);
        final SampleInterface provided = this.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class), new SampleImplementation());
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class, Bindings.named("demo")), new SampleImplementation());
        final SampleInterface provided = this.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class), (Supplier<SampleInterface>) SampleImplementation::new);
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class, Bindings.named("demo")), (Supplier<SampleInterface>) SampleImplementation::new);
        final SampleInterface provided = this.context().get(SampleInterface.class, Bindings.named("demo"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testScannedBindingCanBeProvided() {
        this.context().bind("test.types.scan");
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() {
        this.context().bind("test.types.meta");
        final SampleInterface sample = this.context().get(SampleInterface.class);
        Assertions.assertTrue(TypeContext.of(sample).isProxy());
        Assertions.assertThrows(AbstractMethodError.class, sample::name);

        final SampleInterface provided = this.context().get(SampleInterface.class, Bindings.named("meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMultiBindingsCanBeProvided() {
        this.context().bind("test.types.multi");
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testScannedMultiMetaBindingsCanBeProvided() {
        this.context().bind("test.types.multi");
        final SampleInterface provided = this.context().get(SampleInterface.class, Bindings.named("meta"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.name());
    }

    @Test
    public void testConfigBindingCanBeProvided() {
        this.context().bind(new SampleConfiguration());
        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.name());
    }

    @Test
    public void testTypesCanBePopulated() {
        this.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.sampleInterface());

        this.context().populate(populatedType);
        Assertions.assertNotNull(populatedType.sampleInterface());
        Assertions.assertEquals("Hartshorn", populatedType.sampleInterface().name());
    }

    @Test
    public void unboundTypesCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final PopulatedType provided = this.context().get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.sampleInterface());
    }

    @Test
    public void injectionPointsArePrioritised() {
        this.context().bind(Key.of(SampleInterface.class), SampleImplementation.class);
        final InjectionPoint<SampleInterface> point = InjectionPoint.of(TypeContext.of(SampleInterface.class), $ -> new SampleAnnotatedImplementation());
        this.context().add(point);

        final SampleInterface provided = this.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);
        Assertions.assertEquals(SampleAnnotatedImplementation.class, provided.getClass());
    }

    @Test
    public void boundTypesCanBeProvided() {
        this.context().bind(Key.of(SampleInterface.class), SampleBoundType.class);

        final SampleInterface wired = this.context().get(SampleInterface.class, "BoundHartshorn");
        Assertions.assertNotNull(wired);
        Assertions.assertEquals("BoundHartshorn", wired.name());
    }

    @Test
    public void testScannedBoundBindingsCanBeProvided() {
        this.context().bind("test.types.bound");
        final SampleInterface provided = this.context().get(SampleInterface.class, "BoundAnnotated");
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("BoundAnnotated", provided.name());
    }

    @Test
    public void boundTypesCanBeProvidedThroughFactoryProperty() {
        this.context().bind(Key.of(SampleInterface.class), SampleBoundType.class);
        this.context().bind(Key.of(SampleInterface.class), SampleBoundType.class);

        final SampleInterface provided = this.context().get(SampleInterface.class, new UseFactory("FactoryTyped"));
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.name());
    }

    @Test
    public void providerRedirectsVarargs() {
        this.context().bind(Key.of(SampleInterface.class), SampleBoundType.class);

        final SampleInterface provided = this.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);

        final Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleBoundType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.name());
    }

    @Test
    public void varargProvidedTypesArePopulated() {
        this.context().bind(Key.of(SampleInterface.class), SampleBoundPopulatedType.class);
        this.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);

        final SampleInterface provided = this.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleBoundPopulatedType);
        Assertions.assertNotNull(((SampleBoundPopulatedType) provided).field());
        Assertions.assertTrue(((SampleBoundPopulatedType) provided).field() instanceof SampleFieldImplementation);
    }

    @Test
    public void injectionPointsAreAppliedToVarargProviders() {
        this.context().bind(Key.of(SampleInterface.class), SampleBoundType.class);
        this.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);

        final InjectionPoint<SampleInterface> point = InjectionPoint.of(TypeContext.of(SampleInterface.class), $ -> new SampleImplementation());
        this.context().add(point);

        final SampleInterface provided = this.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertFalse(provided instanceof SampleBoundType);
        Assertions.assertTrue(provided instanceof SampleImplementation);
    }

    @ParameterizedTest
    @MethodSource("providers")
    void testProvidersCanApply(final String meta, final String name, final boolean field, final String fieldMeta, final boolean singleton) {
        this.context().bind("test.types.provision");
        if (field) {
            if (fieldMeta == null) {this.context().bind(Key.of(SampleField.class), SampleFieldImplementation.class);}
            else this.context().bind(Key.of(SampleField.class, Bindings.named(fieldMeta)), SampleFieldImplementation.class);
        }

        final ProvidedInterface provided;
        if (meta == null) provided = this.context().get(ProvidedInterface.class);
        else provided = this.context().get(ProvidedInterface.class, Bindings.named(meta));
        Assertions.assertNotNull(provided);

        final String actual = provided.name();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            final ProvidedInterface second;
            if (meta == null) second = this.context().get(ProvidedInterface.class);
            else second = this.context().get(ProvidedInterface.class, Bindings.named(meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    void testBoundProviderCanSupply() {
        this.context().bind("test.types.provision");
        final ProvidedInterface provided = this.context().get(ProvidedInterface.class, BindingMetaAttribute.of("bound"), new UseFactory("BoundProvision"));
        Assertions.assertNotNull(provided);
        Assertions.assertEquals("BoundProvision", provided.name());
    }

    @Test
    void testDualConstructableTypeCanBind() {
        final Key<SampleInterface> key = Key.of(SampleInterface.class);
        this.context().bind(key, DualConstructableType.class);
        final BindingHierarchy<SampleInterface> hierarchy = this.context().hierarchy(key);

        Assertions.assertEquals(2, hierarchy.size());

        final Exceptional<Provider<SampleInterface>> provider = hierarchy.get(-1);
        Assertions.assertTrue(provider.present());
        Assertions.assertTrue(provider.get() instanceof ContextDrivenProvider);
        Assertions.assertTrue(((ContextDrivenProvider<SampleInterface>) provider.get()).context().is(DualConstructableType.class));

        final Exceptional<Provider<SampleInterface>> boundProvider = hierarchy.get(0);
        Assertions.assertTrue(boundProvider.present());
        Assertions.assertTrue(boundProvider.get() instanceof BoundFactoryProvider);
        Assertions.assertTrue(((BoundFactoryProvider<SampleInterface>) boundProvider.get()).context().is(DualConstructableType.class));
    }

    @Test
    void testContextFieldsAreInjected() {
        this.context().add(new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.context().populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.context());
        Assertions.assertEquals("InjectedContext", instance.context().name());
    }

    @Test
    void testNamedContextFieldsAreInjected() {
        this.context().add("another", new SampleContext("InjectedContext"));
        final ContextInjectedType instance = this.context().populate(new ContextInjectedType());
        Assertions.assertNotNull(instance.anotherContext());
        Assertions.assertEquals("InjectedContext", instance.anotherContext().name());
    }
}
