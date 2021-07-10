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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.context.ReflectionContext;
import org.dockbox.hartshorn.util.annotations.Demo;
import org.dockbox.hartshorn.util.exceptions.TypeConversionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javassist.util.proxy.ProxyFactory;

@SuppressWarnings("ALL")
public class ReflectTests {

    private static Stream<Arguments> getFieldTargets() {
        return Stream.of(
                Arguments.of("privateField"),
                Arguments.of("publicField"),
                Arguments.of("finalPrivateField"),
                Arguments.of("finalPublicField"),
                Arguments.of("publicStaticField"),
                Arguments.of("privateStaticField"),
                Arguments.of("accessorField")
        );
    }

    @ParameterizedTest
    @MethodSource("getFieldTargets")
    void testFieldValueReturnsValue(String field) {
        Exceptional<String> value = Reflect.field(new ReflectTestType(), field);
        Assertions.assertTrue(value.present());
        Assertions.assertEquals(field, value.get());
    }

    private static Stream<Arguments> getMethodTargets() {
        return Stream.of(
                Arguments.of("publicMethod"),
                Arguments.of("privateMethod")
        );
    }

    @ParameterizedTest
    @MethodSource("getMethodTargets")
    void testRunMethodReturnsValue(String method) {
        Exceptional<String> value = Reflect.run(new ReflectTestType(), method, "value");
        Assertions.assertTrue(value.present());
        Assertions.assertEquals("VALUE", value.get());
    }

    private static Stream<Arguments> getGenericInstances() {
        return Stream.of(
                Arguments.of(new ReflectTestType(), ReflectTestType.class, true),
                Arguments.of(new ReflectTestType(), ParentTestType.class, true),
                Arguments.of(null, ReflectTestType.class, false),
                Arguments.of(new ReflectTestType(), String.class, false)
        );
    }

    private static Stream<Arguments> getAssignablePrimitives() {
        return Stream.of(
                Arguments.of(boolean.class, Boolean.class),
                Arguments.of(byte.class, Byte.class),
                Arguments.of(char.class, Character.class),
                Arguments.of(double.class, Double.class),
                Arguments.of(float.class, Float.class),
                Arguments.of(int.class, Integer.class),
                Arguments.of(long.class, Long.class),
                Arguments.of(short.class, Short.class)
        );
    }

    @ParameterizedTest
    @MethodSource("getAssignablePrimitives")
    void testAssignableFromPrimitives(Class<?> primitive, Class<?> wrapper) {
        Assertions.assertTrue(Reflect.primitive(wrapper, primitive));
        Assertions.assertTrue(Reflect.assigns(wrapper, primitive));
        Assertions.assertTrue(Reflect.assigns(primitive, wrapper));
    }

    @Test
    void testAssignableFromSuper() {
        Assertions.assertTrue(Reflect.assigns(ParentTestType.class, ReflectTestType.class));
    }

    @Test
    void testAssignableFromSame() {
        Assertions.assertTrue(Reflect.assigns(ReflectTestType.class, ReflectTestType.class));
    }

    @Test
    void testAnnotatedMethodsReturnsAllModifiers() {
        Collection<Method> methods = Reflect.methods(ReflectTestType.class, Demo.class);
        Assertions.assertEquals(2, methods.size());

        List<String> names = methods.stream().map(Method::getName).toList();
        Assertions.assertTrue(names.contains("publicAnnotatedMethod"));
        Assertions.assertTrue(names.contains("privateAnnotatedMethod"));
    }

    @Test
    void testAnnotatedMethodsWithRuleApplies() {
        final boolean[] activated = { false };
        Collection<Method> methods = Reflect.methods(ReflectTestType.class, Demo.class, type -> {
            activated[0] = true;
            return true;
        });
        Assertions.assertEquals(2, methods.size());
        Assertions.assertTrue(activated[0]);

        methods = Reflect.methods(ReflectTestType.class, Demo.class, type -> false);
        Assertions.assertEquals(0, methods.size());
    }

    @Test
    void testAnnotatedTypesReturnsAllInPrefix() {
        final PrefixContext context = new ReflectionContext("org.dockbox.hartshorn.util");
        Collection<Class<?>> types = context.types(Demo.class);
        Assertions.assertEquals(1, types.size());
        Assertions.assertEquals(ReflectTestType.class, types.iterator().next());
    }

    @Test
    void testSubTypesReturnsAllSubTypes() {
        final PrefixContext context = new ReflectionContext("org.dockbox.hartshorn.util");
        Collection<Class<? extends ParentTestType>> types = context.children(ParentTestType.class);
        Assertions.assertEquals(1, types.size());
        Assertions.assertEquals(ReflectTestType.class, types.iterator().next());
    }

    @Test
    void testStaticFieldsReturnsAllModifiers() {
        Collection<Field> fields = Reflect.staticFields(ReflectTestType.class);
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    void testHasAnnotationOnMethod() throws NoSuchMethodException {
        Method method = ReflectTestType.class.getDeclaredMethod("publicAnnotatedMethod");
        Assertions.assertTrue(Reflect.has(method, Demo.class));
    }

    @Test
    void testSuperTypesReturnsAllSuperTypesWithoutObject() {
        Collection<Class<?>> types = Reflect.parents(ReflectTestType.class);
        Assertions.assertEquals(1, types.size());
        Assertions.assertEquals(ParentTestType.class, types.iterator().next());
    }

    @Test
    void testMethodsReturnsAllDeclaredAndParentMethods() throws NoSuchMethodException {
        List<Method> methods = Reflect.methods(ReflectTestType.class);
        boolean fail = true;
        for (Method method : methods) {
            if (method.getName().equals("parentMethod")) fail = false;
        }
        if (fail) Assertions.fail("Parent types were not included");
    }

    @Test
    void testLookupReturnsClassIfPresent() {
        Class<?> lookup = Reflect.lookup("org.dockbox.hartshorn.util.ReflectTestType");
        Assertions.assertNotNull(lookup);
        Assertions.assertEquals(ReflectTestType.class, lookup);
    }

    @Test
    void testLookupReturnsNullIfAbsent() {
        Class<?> lookup = Reflect.lookup("org.dockbox.hartshorn.util.AnotherClass");
        Assertions.assertNull(lookup);
    }

    @Test
    void testHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(Reflect.has(ReflectTestType.class, "publicMethod"));
    }

    @Test
    void testHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(Reflect.has(ReflectTestType.class, "otherMethod"));
    }

    @Test
    void testInstanceHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(Reflect.has(new ReflectTestType(), "publicMethod"));
    }

    @Test
    void testInstanceHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(Reflect.has(new ReflectTestType(), "otherMethod"));
    }

    private static Stream<Arguments> getNonVoidTypes() {
        return Stream.of(
                Arguments.of(boolean.class),
                Arguments.of(Boolean.class),
                Arguments.of(byte.class),
                Arguments.of(Byte.class),
                Arguments.of(char.class),
                Arguments.of(Character.class),
                Arguments.of(double.class),
                Arguments.of(Double.class),
                Arguments.of(float.class),
                Arguments.of(Float.class),
                Arguments.of(int.class),
                Arguments.of(Integer.class),
                Arguments.of(long.class),
                Arguments.of(Long.class),
                Arguments.of(short.class),
                Arguments.of(Short.class),
                Arguments.of(String.class)
        );
    }

    @ParameterizedTest
    @MethodSource("getNonVoidTypes")
    void testIsNotVoidIsTrueIfTypeIsNotVoid(Class<?> type) {
        Assertions.assertTrue(Reflect.notVoid(type));
    }

    @Test
    void testIsNotVoidIsFalseIfTypeIsVoid() {
        Assertions.assertFalse(Reflect.notVoid(Void.class));
    }

    @Test
    void testIsNotVoidIsFalseIfTypeIsVoidPrimitive() {
        Assertions.assertFalse(Reflect.notVoid(void.class));
    }

    @ParameterizedTest
    @MethodSource("getFieldTargets")
    void testHasFieldReturnsTrue(String field) {
        Assertions.assertTrue(Reflect.hasField(ReflectTestType.class, field));
    }

    @ParameterizedTest
    @MethodSource("getFieldTargets")
    void testFieldsConsumesAllFields(String field) {
        final boolean[] activated = { false };
        Reflect.fields(ReflectTestType.class, (t, f) -> {
            if (f.getName().equals(field)) {
                activated[0] = true;
            }
        });
        Assertions.assertTrue(activated[0]);
    }

    @Test
    void testSetFieldUpdatesAccessorField() throws NoSuchFieldException, IllegalAccessException {
        Field fieldRef = ReflectTestType.class.getDeclaredField("accessorField");
        ReflectTestType instance = new ReflectTestType();
        Reflect.set(fieldRef, instance, "newValue");

        Assertions.assertTrue(instance.isActivatedSetter());
    }

    @Test
    void testSetFieldUpdatesNormalField() throws NoSuchFieldException, IllegalAccessException {
        Field fieldRef = ReflectTestType.class.getDeclaredField("publicField");
        ReflectTestType instance = new ReflectTestType();
        Reflect.set(fieldRef, instance, "newValue");

        Assertions.assertEquals("newValue", instance.publicField);
    }

    @Test
    void testAnnotatedFieldsIncludesStatic() {
        Collection<Field> fields = Reflect.fields(ReflectTestType.class, Demo.class);
        Assertions.assertEquals(2, fields.size());
        int statics = 0;
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) statics++;
        }
        Assertions.assertEquals(1, statics);
    }

    @Test
    void testAnnotatedConstructors() {
        Collection<Constructor<ReflectTestType>> constructors = Reflect.constructors(ReflectTestType.class, Demo.class);
        Assertions.assertEquals(1, constructors.size());
    }

    @Test
    void testIsProxyIsTrueIfTypeIsProxy() throws InstantiationException, IllegalAccessException {
        Class<?> type = new ProxyFactory().createClass();
        Assertions.assertTrue(Reflect.isProxy(type.newInstance()));
    }

    @Test
    void testIsProxyIsFalseIfTypeIsNormal() {
        Assertions.assertFalse(Reflect.isProxy(new ReflectTestType()));
    }

    private static Stream<Arguments> getPrimitiveValues() {
        return Stream.of(
                Arguments.of(boolean.class, "true", true),
                Arguments.of(byte.class, "0", (byte) 0),
                Arguments.of(char.class, "a", 'a'),
                Arguments.of(double.class, "10.5", 10.5D),
                Arguments.of(float.class, "10.5", 10.5F),
                Arguments.of(int.class, "10", 10),
                Arguments.of(long.class, "10", 10L),
                Arguments.of(short.class, "10", (short) 10),
                Arguments.of(TestEnumType.class, "A", TestEnumType.A)
        );
    }

    @ParameterizedTest
    @MethodSource("getPrimitiveValues")
    void testStringToPrimitive(Class<?> type, String value, Object expected) throws TypeConversionException {
        byte b = 0x0;
        Object o = Reflect.toPrimitive(type, value);
        Assertions.assertNotNull(o);
        Assertions.assertEquals(expected, o);
    }
}
