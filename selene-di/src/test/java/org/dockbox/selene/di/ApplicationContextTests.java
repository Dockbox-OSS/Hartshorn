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

package org.dockbox.selene.di;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.context.ManagedSeleneContext;
import org.dockbox.selene.di.inject.GuiceInjector;
import org.dockbox.selene.di.inject.Injector;
import org.dockbox.selene.di.properties.BindingMetaProperty;
import org.dockbox.selene.di.types.InvalidSampleWiredType;
import org.dockbox.selene.di.types.NameProperty;
import org.dockbox.selene.di.types.PopulatedType;
import org.dockbox.selene.di.types.SampleEnablingType;
import org.dockbox.selene.di.types.SampleField;
import org.dockbox.selene.di.types.SampleFieldImplementation;
import org.dockbox.selene.di.types.SampleImplementation;
import org.dockbox.selene.di.types.SampleInterface;
import org.dockbox.selene.di.types.SamplePreloads;
import org.dockbox.selene.di.types.SampleWiredPopulatedType;
import org.dockbox.selene.di.types.SampleWiredType;
import org.dockbox.selene.di.types.bean.BeanInterface;
import org.dockbox.selene.di.types.meta.SampleMetaAnnotatedImplementation;
import org.dockbox.selene.di.types.multi.SampleMultiAnnotatedImplementation;
import org.dockbox.selene.di.types.scan.SampleAnnotatedImplementation;
import org.dockbox.selene.di.types.wired.SampleWiredAnnotatedImplementation;
import org.dockbox.selene.test.SeleneJUnit5Runner;
import org.dockbox.selene.util.SeleneUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.stream.Stream;

@ExtendWith(SeleneJUnit5Runner.class)
public class ApplicationContextTests {

    private static final Field modules;
    private static final Field bindings;
    private static final Field injectionPoints;

    static {
        try {
            modules = GuiceInjector.class.getDeclaredField("modules");
            modules.setAccessible(true);

            bindings = GuiceInjector.class.getDeclaredField("bindings");
            bindings.setAccessible(true);

            injectionPoints = ManagedSeleneContext.class.getDeclaredField("injectionPoints");
            injectionPoints.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticBindingStoresBinding() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class);
        Exceptional<Class<SampleInterface>> binding = injector(false).getStaticBinding(SampleInterface.class);
        Assertions.assertTrue(binding.present());
        Class<SampleInterface> bindingClass = binding.get();
        Assertions.assertEquals(SampleImplementation.class, bindingClass);
    }

    @Test
    public void testStaticBindingCanBeProvided() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class);
        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class, Bindings.named("demo"));
        SampleInterface provided = Selene.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testInstanceBindingStoresBinding() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, new SampleImplementation());
        Exceptional<Class<SampleInterface>> binding = injector(false).getStaticBinding(SampleInterface.class);
        Assertions.assertTrue(binding.present());
        Class<SampleInterface> bindingClass = binding.get();
        Assertions.assertEquals(SampleImplementation.class, bindingClass);
    }

    @Test
    public void testInstanceBindingCanBeProvided() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, new SampleImplementation());
        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, new SampleImplementation(), Bindings.named("demo"));
        SampleInterface provided = Selene.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testProviderBindingStoresBinding() throws IllegalAccessException {
        injector(true).provide(SampleInterface.class, SampleImplementation::new);
        Exceptional<Class<SampleInterface>> binding = injector(false).getStaticBinding(SampleInterface.class);
        // Unlike instance and static bindings, providers cannot be evaluated this early
        Assertions.assertTrue(binding.absent());
    }

    @Test
    public void testProviderBindingCanBeProvided() throws IllegalAccessException {
        injector(true).provide(SampleInterface.class, SampleImplementation::new);
        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() throws IllegalAccessException {
        injector(true).provide(SampleInterface.class, SampleImplementation::new, Bindings.named("demo"));
        SampleInterface provided = Selene.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testScannedBindingCanBeProvided() throws IllegalAccessException {
        // sub-package *.scan was added to prevent scan conflicts
        injector(true).bind("org.dockbox.selene.di.types.scan");
        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedSelene", provided.getName());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() throws IllegalAccessException {
        // sub-package *.meta was added to prevent scan conflicts
        injector(true).bind("org.dockbox.selene.di.types.meta");
        Assertions.assertThrows(ProvisionFailure.class, () -> Selene.context().get(SampleInterface.class));

        SampleInterface provided = Selene.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("meta")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedSelene", provided.getName());
    }

    @Test
    public void testScannedMultiBindingsCanBeProvided() throws IllegalAccessException {
        // sub-package *.multi was added to prevent scan conflicts
        injector(true).bind("org.dockbox.selene.di.types.multi");

        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedSelene", provided.getName());
    }

    @Test
    public void testScannedMultiMetaBindingsCanBeProvided() throws IllegalAccessException {
        // sub-package *.multi was added to prevent scan conflicts
        injector(true).bind("org.dockbox.selene.di.types.multi");

        SampleInterface provided = Selene.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("meta")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedSelene", provided.getName());
    }

    @Test
    public void testConfigBindingCanBeProvided() throws IllegalAccessException {
        injector(true).bind(new SampleConfiguration());
        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Selene", provided.getName());
    }

    @Test
    public void testTypesCanBePopulated() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class);
        PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.getSampleInterface());

        injector(false).populate(populatedType);
        Assertions.assertNotNull(populatedType.getSampleInterface());
        Assertions.assertEquals("Selene", populatedType.getSampleInterface().getName());
    }

    @Test
    public void unboundTypesCanBeProvided() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class);
        PopulatedType provided = Selene.context().get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.getSampleInterface());
    }

    @Test
    public void injectionPointsArePrioritised() throws IllegalArgumentException, IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleImplementation.class);
        InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleAnnotatedImplementation());
        Selene.context().add(point);

        SampleInterface provided = Selene.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);
        Assertions.assertEquals(SampleAnnotatedImplementation.class, provided.getClass());
    }

    @Test
    public void preConstructPreloadsAreApplied() {
        Assertions.assertTrue(SamplePreloads.PreConstructPreload.isApplied());
    }

    @Test
    public void constructPreloadsAreApplied() {
        Assertions.assertTrue(SamplePreloads.ConstructPreload.isApplied());
    }

    @Test
    public void preInitPreloadsAreApplied() {
        Assertions.assertTrue(SamplePreloads.PreInitPreload.isApplied());
    }

    @Test
    public void initPreloadsAreApplied() {
        Assertions.assertTrue(SamplePreloads.InitPreload.isApplied());
    }

    @Test
    public void invalidWiredTypesCannotBeBound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                injector(true).wire(SampleInterface.class, InvalidSampleWiredType.class)
        );
    }

    @Test
    public void wiredTypesCanBeProvided() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);

        SampleInterface wired = Selene.context().get(SeleneFactory.class).create(SampleInterface.class, "WiredSelene");
        Assertions.assertNotNull(wired);
        Assertions.assertEquals("WiredSelene", wired.getName());
    }

    @Test
    public void injectableTypesAreEnabled() throws IllegalAccessException {
        injector(true).bind(SampleInterface.class, SampleEnablingType.class);

        SampleInterface provided = Selene.context().get(SampleInterface.class, new NameProperty("Enabled"));
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.getName());
        Assertions.assertEquals("Enabled", provided.getName());
    }

    @Test
    public void testScannedWiredBindingsCanBeProvided() throws IllegalAccessException {
        // sub-package *.wired was added to prevent scan conflicts
        injector(true).bind("org.dockbox.selene.di.types.wired");
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);

        SampleInterface provided = Selene.context().get(SeleneFactory.class).create(SampleInterface.class, "WiredAnnotated");
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("WiredAnnotated", provided.getName());
    }

    @Test
    public void wiredTypesCanBeProvidedThroughFactoryProperty() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredType.class);
        injector(true).wire(SampleInterface.class, SampleWiredType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);

        SampleInterface provided = Selene.context().get(SampleInterface.class, SeleneFactory.use("FactoryTyped"));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.getName());
    }

    @Test
    public void providerRedirectsVarargs() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);

        SampleInterface provided = Selene.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.getName());
    }

    @Test
    public void varargProvidedTypesAreEnabled() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredPopulatedType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);
        injector(false).bind(SampleField.class, SampleFieldImplementation.class);

        SampleInterface provided = Selene.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleWiredPopulatedType);
        Assertions.assertTrue(((SampleWiredPopulatedType) provided).isEnabled());
    }

    @Test
    public void varargProvidedTypesArePopulated() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredPopulatedType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);
        injector(false).bind(SampleField.class, SampleFieldImplementation.class);

        SampleInterface provided = Selene.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleWiredPopulatedType);
        Assertions.assertNotNull(((SampleWiredPopulatedType) provided).getField());
        Assertions.assertTrue(((SampleWiredPopulatedType) provided).getField() instanceof SampleFieldImplementation);
    }

    @Test
    public void injectionPointsAreAppliedToVarargProviders() throws IllegalAccessException {
        injector(true).wire(SampleInterface.class, SampleWiredType.class);
        injector(false).bind(SeleneFactory.class, SimpleSeleneFactory.class);
        injector(false).bind(SampleField.class, SampleFieldImplementation.class);

        InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleImplementation());
        Selene.context().add(point);

        SampleInterface provided = Selene.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertFalse(provided instanceof SampleWiredType);
        Assertions.assertTrue(provided instanceof SampleImplementation);
    }

    private static Stream<Arguments> getBeans() {
        return Stream.of(
                Arguments.of(null, "Bean", false, null, false),
                Arguments.of("named", "NamedBean", false, null, false),
                Arguments.of("field", "FieldBean", true, null, false),
                Arguments.of("namedField", "NamedFieldBean", true, "named", false),
                Arguments.of("singleton", "SingletonBean", false, null, true)
        );
    }

    @ParameterizedTest
    @MethodSource("getBeans")
    void testBeansCanSupply(String meta, String name, boolean field, String fieldMeta, boolean singleton) throws IllegalAccessException {
        injector(true).bind("org.dockbox.selene.di.types.bean");

        if (field) {
            if (fieldMeta == null) {injector(false).bind(SampleField.class, SampleFieldImplementation.class);}
            else {injector(false).bind(SampleField.class, SampleFieldImplementation.class, Bindings.named(fieldMeta));}
        }

        BeanInterface provided;
        if (meta == null) provided = Selene.context().get(BeanInterface.class);
        else provided = Selene.context().get(BeanInterface.class, BindingMetaProperty.of(meta));
        Assertions.assertNotNull(provided);

        String actual = provided.getName();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            BeanInterface second;
            if (meta == null) second = Selene.context().get(BeanInterface.class);
            else second = Selene.context().get(BeanInterface.class, BindingMetaProperty.of(meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    private static Injector injector(boolean reset) throws IllegalAccessException {
        Injector injector = Selene.context().injector();
        if (reset) {
            modules.set(injector, SeleneUtils.emptyConcurrentSet());
            bindings.set(injector, SeleneUtils.emptyConcurrentSet());
            injectionPoints.set(Selene.context(), SeleneUtils.emptyConcurrentSet());
            injector.reset();
        }
        return injector;
    }

}
