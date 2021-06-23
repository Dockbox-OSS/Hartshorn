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
import org.dockbox.hartshorn.api.SimpleMetaProvider;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.domain.MetaProvider;
import org.dockbox.hartshorn.di.binding.Bindings;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.HartshornApplicationContext;
import org.dockbox.hartshorn.di.context.ManagedHartshornContext;
import org.dockbox.hartshorn.di.exceptions.ApplicationException;
import org.dockbox.hartshorn.di.inject.GuiceInjector;
import org.dockbox.hartshorn.di.inject.Injector;
import org.dockbox.hartshorn.di.properties.BindingMetaProperty;
import org.dockbox.hartshorn.di.services.BeanServiceProcessor;
import org.dockbox.hartshorn.di.types.InvalidSampleWiredType;
import org.dockbox.hartshorn.di.types.NameProperty;
import org.dockbox.hartshorn.di.types.PopulatedType;
import org.dockbox.hartshorn.di.types.SampleEnablingType;
import org.dockbox.hartshorn.di.types.SampleField;
import org.dockbox.hartshorn.di.types.SampleFieldImplementation;
import org.dockbox.hartshorn.di.types.SampleImplementation;
import org.dockbox.hartshorn.di.types.SampleInterface;
import org.dockbox.hartshorn.di.types.SampleWiredPopulatedType;
import org.dockbox.hartshorn.di.types.SampleWiredType;
import org.dockbox.hartshorn.di.types.bean.BeanInterface;
import org.dockbox.hartshorn.di.types.meta.SampleMetaAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.multi.SampleMultiAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.scan.SampleAnnotatedImplementation;
import org.dockbox.hartshorn.di.types.wired.SampleWiredAnnotatedImplementation;
import org.dockbox.hartshorn.test.HartshornRunner;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

@ExtendWith(HartshornRunner.class)
public class ApplicationContextTests {

    private static final Field modules;
    private static final Field bindings;
    private static final Field injectionPoints;
    private static final Field serviceModifiers;
    private static final Field serviceProcessors;
    private static final Method internalInjector;

    static {
        try {
            modules = GuiceInjector.class.getDeclaredField("modules");
            modules.setAccessible(true);

            bindings = GuiceInjector.class.getDeclaredField("bindings");
            bindings.setAccessible(true);

            injectionPoints = ManagedHartshornContext.class.getDeclaredField("injectionPoints");
            injectionPoints.setAccessible(true);

            serviceModifiers = ManagedHartshornContext.class.getDeclaredField("injectionModifiers");
            serviceModifiers.setAccessible(true);

            serviceProcessors = ManagedHartshornContext.class.getDeclaredField("serviceProcessors");
            serviceProcessors.setAccessible(true);

            internalInjector = HartshornApplicationContext.class.getDeclaredMethod("internalInjector");
            internalInjector.setAccessible(true);
            
        } catch (NoSuchFieldException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testStaticBindingStoresBinding() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class);
        Exceptional<Class<SampleInterface>> binding = context(false).type(SampleInterface.class);
        Assertions.assertTrue(binding.present());
        Class<SampleInterface> bindingClass = binding.get();
        Assertions.assertEquals(SampleImplementation.class, bindingClass);
    }

    @Test
    public void testStaticBindingCanBeProvided() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class);
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testStaticBindingWithMetaCanBeProvided() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class, Bindings.named("demo"));
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testInstanceBindingStoresBinding() throws ApplicationException {
        context(true).bind(SampleInterface.class, new SampleImplementation());
        Exceptional<Class<SampleInterface>> binding = context(false).type(SampleInterface.class);
        Assertions.assertTrue(binding.present());
        Class<SampleInterface> bindingClass = binding.get();
        Assertions.assertEquals(SampleImplementation.class, bindingClass);
    }

    @Test
    public void testInstanceBindingCanBeProvided() throws ApplicationException {
        context(true).bind(SampleInterface.class, new SampleImplementation());
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testInstanceBindingWithMetaCanBeProvided() throws ApplicationException {
        context(true).bind(SampleInterface.class, new SampleImplementation(), Bindings.named("demo"));
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testProviderBindingStoresBinding() throws ApplicationException {
        context(true).provide(SampleInterface.class, SampleImplementation::new);
        Exceptional<Class<SampleInterface>> binding = context(false).type(SampleInterface.class);
        // Unlike instance and static bindings, providers cannot be evaluated this early
        Assertions.assertTrue(binding.absent());
    }

    @Test
    public void testProviderBindingCanBeProvided() throws ApplicationException {
        context(true).provide(SampleInterface.class, SampleImplementation::new);
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testProviderBindingWithMetaCanBeProvided() throws ApplicationException {
        context(true).provide(SampleInterface.class, SampleImplementation::new, Bindings.named("demo"));
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("demo")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testScannedBindingCanBeProvided() throws ApplicationException {
        // sub-package *.scan was added to prevent scan conflicts
        context(true).bind("org.dockbox.hartshorn.di.types.scan");
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("AnnotatedHartshorn", provided.getName());
    }

    @Test
    public void testScannedMetaBindingsCanBeProvided() throws ApplicationException {
        // sub-package *.meta was added to prevent scan conflicts
        context(true).bind("org.dockbox.hartshorn.di.types.meta");
        Assertions.assertThrows(ProvisionFailure.class, () -> Hartshorn.context().get(SampleInterface.class));

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("meta")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMetaAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MetaAnnotatedHartshorn", provided.getName());
    }

    @Test
    public void testScannedMultiBindingsCanBeProvided() throws ApplicationException {
        // sub-package *.multi was added to prevent scan conflicts
        context(true).bind("org.dockbox.hartshorn.di.types.multi");

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.getName());
    }

    @Test
    public void testScannedMultiMetaBindingsCanBeProvided() throws ApplicationException {
        // sub-package *.multi was added to prevent scan conflicts
        context(true).bind("org.dockbox.hartshorn.di.types.multi");

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, BindingMetaProperty.of(Bindings.named("meta")));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleMultiAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("MultiAnnotatedHartshorn", provided.getName());
    }

    @Test
    public void testConfigBindingCanBeProvided() throws ApplicationException {
        context(true).bind(new SampleConfiguration());
        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleImplementation.class, providedClass);

        Assertions.assertEquals("Hartshorn", provided.getName());
    }

    @Test
    public void testTypesCanBePopulated() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class);
        PopulatedType populatedType = new PopulatedType();
        Assertions.assertNull(populatedType.getSampleInterface());

        context(false).populate(populatedType);
        Assertions.assertNotNull(populatedType.getSampleInterface());
        Assertions.assertEquals("Hartshorn", populatedType.getSampleInterface().getName());
    }

    @Test
    public void unboundTypesCanBeProvided() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class);
        PopulatedType provided = Hartshorn.context().get(PopulatedType.class);
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.getSampleInterface());
    }

    @Test
    public void injectionPointsArePrioritised() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleImplementation.class);
        InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleAnnotatedImplementation());
        Hartshorn.context().add(point);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class);
        Assertions.assertNotNull(provided);
        Assertions.assertEquals(SampleAnnotatedImplementation.class, provided.getClass());
    }

    @Test
    public void invalidWiredTypesCannotBeBound() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                context(true).wire(SampleInterface.class, InvalidSampleWiredType.class)
        );
    }

    @Test
    public void wiredTypesCanBeProvided() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);

        SampleInterface wired = Hartshorn.context().get(TypeFactory.class).create(SampleInterface.class, "WiredHartshorn");
        Assertions.assertNotNull(wired);
        Assertions.assertEquals("WiredHartshorn", wired.getName());
    }

    @Test
    public void injectableTypesAreEnabled() throws ApplicationException {
        context(true).bind(SampleInterface.class, SampleEnablingType.class);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, new NameProperty("Enabled"));
        Assertions.assertNotNull(provided);
        Assertions.assertNotNull(provided.getName());
        Assertions.assertEquals("Enabled", provided.getName());
    }

    @Test
    public void testScannedWiredBindingsCanBeProvided() throws ApplicationException {
        // sub-package *.wired was added to prevent scan conflicts
        context(true).bind("org.dockbox.hartshorn.di.types.wired");
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);

        SampleInterface provided = Hartshorn.context().get(TypeFactory.class).create(SampleInterface.class, "WiredAnnotated");
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredAnnotatedImplementation.class, providedClass);

        Assertions.assertEquals("WiredAnnotated", provided.getName());
    }

    @Test
    public void wiredTypesCanBeProvidedThroughFactoryProperty() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredType.class);
        context(true).wire(SampleInterface.class, SampleWiredType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, TypeFactory.use("FactoryTyped"));
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.getName());
    }

    @Test
    public void providerRedirectsVarargs() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);

        Class<? extends SampleInterface> providedClass = provided.getClass();
        Assertions.assertEquals(SampleWiredType.class, providedClass);

        Assertions.assertEquals("FactoryTyped", provided.getName());
    }

    @Test
    public void varargProvidedTypesAreEnabled() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredPopulatedType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);
        context(false).bind(SampleField.class, SampleFieldImplementation.class);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleWiredPopulatedType);
        Assertions.assertTrue(((SampleWiredPopulatedType) provided).isEnabled());
    }

    @Test
    public void varargProvidedTypesArePopulated() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredPopulatedType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);
        context(false).bind(SampleField.class, SampleFieldImplementation.class);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
        Assertions.assertNotNull(provided);
        Assertions.assertTrue(provided instanceof SampleWiredPopulatedType);
        Assertions.assertNotNull(((SampleWiredPopulatedType) provided).getField());
        Assertions.assertTrue(((SampleWiredPopulatedType) provided).getField() instanceof SampleFieldImplementation);
    }

    @Test
    public void injectionPointsAreAppliedToVarargProviders() throws ApplicationException {
        context(true).wire(SampleInterface.class, SampleWiredType.class);
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);
        context(false).bind(SampleField.class, SampleFieldImplementation.class);

        InjectionPoint<SampleInterface> point = InjectionPoint.of(SampleInterface.class, $ -> new SampleImplementation());
        Hartshorn.context().add(point);

        SampleInterface provided = Hartshorn.context().get(SampleInterface.class, "FactoryTyped");
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
    void testBeansCanSupply(String meta, String name, boolean field, String fieldMeta, boolean singleton) throws ApplicationException {
        context(true).bind("org.dockbox.hartshorn.di.types.bean");

        if (field) {
            if (fieldMeta == null) {context(false).bind(SampleField.class, SampleFieldImplementation.class);}
            else {context(false).bind(SampleField.class, SampleFieldImplementation.class, Bindings.named(fieldMeta));}
        }

        BeanInterface provided;
        if (meta == null) provided = Hartshorn.context().get(BeanInterface.class);
        else provided = Hartshorn.context().get(BeanInterface.class, BindingMetaProperty.of(meta));
        Assertions.assertNotNull(provided);

        String actual = provided.getName();
        Assertions.assertNotNull(name);
        Assertions.assertEquals(name, actual);

        if (singleton) {
            BeanInterface second;
            if (meta == null) second = Hartshorn.context().get(BeanInterface.class);
            else second = Hartshorn.context().get(BeanInterface.class, BindingMetaProperty.of(meta));
            Assertions.assertNotNull(second);
            Assertions.assertSame(provided, second);
        }
    }

    @Test
    void testManualWiredBeanCanSupply() throws ApplicationException {
        context(true).bind("org.dockbox.hartshorn.di.types.bean");
        context(false).bind(TypeFactory.class, SimpleTypeFactory.class);

        BeanInterface provided = Hartshorn.context().get(BeanInterface.class, BindingMetaProperty.of("wired"), TypeFactory.use("WiredBean"));
        Assertions.assertNotNull(provided);
        Assertions.assertEquals("WiredBean", provided.getName());
    }

    private static ApplicationContext context(boolean reset) throws ApplicationException {
        final ApplicationContext context = Hartshorn.context();
        try {
            Injector injector = (Injector) internalInjector.invoke(context);
            if (reset) {
                modules.set(injector, HartshornUtils.emptyConcurrentSet());
                bindings.set(injector, HartshornUtils.emptyConcurrentSet());
                injectionPoints.set(context, HartshornUtils.emptyConcurrentSet());
                serviceModifiers.set(context, HartshornUtils.emptyConcurrentSet());
                serviceProcessors.set(context, HartshornUtils.emptyConcurrentSet());
                injector.reset();
                // Meta provision is required for wiring and vararg types, this should always be present
                context.bind(MetaProvider.class, SimpleMetaProvider.class);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ApplicationException(e);
        }
        // This is added by the InjectableBootstrap by default, so it can be re-added here.
        context.add(new BeanServiceProcessor());
        return context;
    }

}
