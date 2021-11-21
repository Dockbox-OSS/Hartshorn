package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.service.ServiceActivator;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.TypeConversionException;
import org.dockbox.hartshorn.core.types.BoundUserImpl;
import org.dockbox.hartshorn.core.types.TestEnumType;
import org.dockbox.hartshorn.core.types.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

public class ElementContextTests {

    public static Stream<Arguments> primitives() {
        return Stream.of(
                Arguments.of(boolean.class),
                Arguments.of(byte.class),
                Arguments.of(char.class),
                Arguments.of(double.class),
                Arguments.of(float.class),
                Arguments.of(int.class),
                Arguments.of(long.class),
                Arguments.of(short.class)
        );
    }

    public static Stream<Arguments> wrappers() {
        return Stream.of(
                Arguments.of(Boolean.class),
                Arguments.of(Byte.class),
                Arguments.of(Character.class),
                Arguments.of(Double.class),
                Arguments.of(Float.class),
                Arguments.of(Integer.class),
                Arguments.of(Long.class),
                Arguments.of(Short.class)
        );
    }

    public static Stream<Arguments> primitiveDefaults() {
        return Stream.of(
                Arguments.of(boolean.class, false),
                Arguments.of(byte.class, 0),
                Arguments.of(char.class, '\u0000'),
                Arguments.of(double.class, 0.0d),
                Arguments.of(float.class, 0.0f),
                Arguments.of(int.class, 0),
                Arguments.of(long.class, 0L),
                Arguments.of(short.class, 0)
        );
    }

    public static Stream<Arguments> wrapperDefaults() {
        return Stream.of(
                Arguments.of(Boolean.class, false),
                Arguments.of(Byte.class, 0),
                Arguments.of(Character.class, '\u0000'),
                Arguments.of(Double.class, 0.0d),
                Arguments.of(Float.class, 0.0f),
                Arguments.of(Integer.class, 0),
                Arguments.of(Long.class, 0L),
                Arguments.of(Short.class, 0)
        );
    }

    public static Stream<Arguments> primitiveStrings() {
        return Stream.of(
                Arguments.of(Boolean.class, "true", true),
                Arguments.of(Byte.class, "0", (byte) 0),
                Arguments.of(Character.class, "\u0000", '\u0000'),
                Arguments.of(Double.class, "1.0d", 1.0d),
                Arguments.of(Float.class, "1.0f", 1.0f),
                Arguments.of(Integer.class, "1", 1),
                Arguments.of(Long.class, "0", 0L),
                Arguments.of(Short.class, "0", (short) 0)
        );
    }

    @Test
    void testTypeContextsAreReused() {
        final TypeContext<ElementContextTests> tc1 = TypeContext.of(ElementContextTests.class);
        final TypeContext<ElementContextTests> tc2 = TypeContext.of(ElementContextTests.class);
        Assertions.assertSame(tc1, tc2);
    }

    @Test
    void testTypeContextsAreNotReusedForDifferentTypes() {
        final TypeContext<ElementContextTests> tc1 = TypeContext.of(ElementContextTests.class);
        final TypeContext<Object> tc2 = TypeContext.of(Object.class);
        Assertions.assertNotSame(tc1, tc2);
    }

    @ParameterizedTest
    @MethodSource("primitives")
    public void testIsPrimitiveAcceptsPrimitives(final Class<?> primitive) {
        Assertions.assertTrue(TypeContext.of(primitive).isPrimitive());
    }

    @ParameterizedTest
    @MethodSource("wrappers")
    public void testIsPrimitiveRejectsPrimitiveWrappers(final Class<?> wrapper) {
        Assertions.assertFalse(TypeContext.of(wrapper).isPrimitive());
    }

    @Test
    public void testIsVoidAcceptsPrimitiveAndWrapper() {
        Assertions.assertTrue(TypeContext.of(void.class).isVoid());
        Assertions.assertTrue(TypeContext.of(Void.class).isVoid());
        Assertions.assertTrue(TypeContext.VOID.isVoid());
    }

    @Test
    public void testIsVoidRejectsNonPrimitiveAndNonWrapper() {
        Assertions.assertFalse(TypeContext.of(String.class).isVoid());
        Assertions.assertFalse(TypeContext.of(Object.class).isVoid());
    }

    @Test
    public void testIsPrimitiveRejectsNonPrimitiveAndNonWrapper() {
        Assertions.assertFalse(TypeContext.of(String.class).isPrimitive());
        Assertions.assertFalse(TypeContext.of(Object.class).isPrimitive());
    }

    @Test
    public void testIsPrimitiveRejectsVoid() {
        Assertions.assertFalse(TypeContext.VOID.isPrimitive());
    }

    @Test
    public void testIsPrimitiveRejectsVoidWrapper() {
        Assertions.assertFalse(TypeContext.of(Void.class).isPrimitive());
    }

    @Test
    public void testIsPrimitiveAcceptsVoidPrimitive() {
        Assertions.assertTrue(TypeContext.of(void.class).isPrimitive());
    }

    @Test
    public void testAnonymousTypesAreAnonymous() {
        final TypeContext<Object> anonymous = TypeContext.of(new Object() {
        });
        Assertions.assertTrue(anonymous.isAnonymous());
    }

    @Test
    public void testNonAnonymousTypesAreNotAnonymous() {
        final TypeContext<Object> anonymous = TypeContext.of(Object.class);
        Assertions.assertFalse(anonymous.isAnonymous());
    }

    @Test
    void testAnonymousWrappersReturnCorrectType() {
        final TypeContext<Object> anonymous = TypeContext.of(new Object() {
        });
        Assertions.assertNotEquals(Object.class, anonymous.type());
    }

    @Test
    public void testEnumsAreEnum() {
        Assertions.assertTrue(TypeContext.of(TestEnumType.class).isEnum());
    }

    @Test
    public void testNonEnumsAreNotEnum() {
        Assertions.assertFalse(TypeContext.of(Object.class).isEnum());
    }

    @Test
    public void testEnumsAreNotAnonymous() {
        Assertions.assertFalse(TypeContext.of(TestEnumType.class).isAnonymous());
    }

    @Test
    public void enumConstantsCanBeObtained() {
        final TypeContext<TestEnumType> enumContext = TypeContext.of(TestEnumType.class);
        Assertions.assertEquals(TestEnumType.values().length, enumContext.enumConstants().size());
    }

    @Test
    public void testAnnotationsAreAnnotations() {
        Assertions.assertTrue(TypeContext.of(ServiceActivator.class).isAnnotation());
    }

    @Test
    public void testNonAnnotationsAreNotAnnotations() {
        Assertions.assertFalse(TypeContext.of(Object.class).isAnnotation());
    }

    @Test
    public void testAnnotationsAreNotAnonymous() {
        Assertions.assertFalse(TypeContext.of(Annotation.class).isAnonymous());
    }

    @Test
    public void testAnnotationsAreNotEnum() {
        Assertions.assertFalse(TypeContext.of(Annotation.class).isEnum());
    }

    @Test
    public void testAnnotationsAreNotPrimitive() {
        Assertions.assertFalse(TypeContext.of(Annotation.class).isPrimitive());
    }

    @Test
    public void testAnnotationsAreNotVoid() {
        Assertions.assertFalse(TypeContext.of(Annotation.class).isVoid());
    }

    @Test
    public void testAnnotationsAreNotArray() {
        Assertions.assertFalse(TypeContext.of(Annotation.class).isArray());
    }

    @Test
    void testArraysAreArrays() {
        Assertions.assertTrue(TypeContext.of(Object[].class).isArray());
    }

    @Test
    void testArraysAreNotAnonymous() {
        Assertions.assertFalse(TypeContext.of(Object[].class).isAnonymous());
    }

    @Test
    void testArraysAreNotEnum() {
        Assertions.assertFalse(TypeContext.of(Object[].class).isEnum());
    }

    @Test
    void testArraysAreNotPrimitive() {
        Assertions.assertFalse(TypeContext.of(Object[].class).isPrimitive());
    }

    @Test
    void testArraysAreNotVoid() {
        Assertions.assertFalse(TypeContext.of(Object[].class).isVoid());
    }

    @Test
    void testArraysAreNotAnnotation() {
        Assertions.assertFalse(TypeContext.of(Object[].class).isAnnotation());
    }

    @ParameterizedTest
    @MethodSource("primitiveDefaults")
    void testPrimitiveDefaults(final Class<?> primitive, final Object defaultValue) {
        Assertions.assertEquals(defaultValue, TypeContext.of(primitive).defaultOrNull());
    }

    @Test
    void testVoidDefaultsToNull() {
        Assertions.assertNull(TypeContext.VOID.defaultOrNull());
    }

    @Test
    void testObjectDefaultsToNull() {
        Assertions.assertNull(TypeContext.of(Object.class).defaultOrNull());
    }

    @Test
    void testAnnotationDefaultsToNull() {
        Assertions.assertNull(TypeContext.of(ServiceActivator.class).defaultOrNull());
    }

    @Test
    void testEnumDefaultsToNull() {
        Assertions.assertNull(TypeContext.of(TestEnumType.class).defaultOrNull());
    }

    @Test
    void testArrayDefaultsToNull() {
        Assertions.assertNull(TypeContext.of(Object[].class).defaultOrNull());
    }

    @ParameterizedTest
    @MethodSource("wrapperDefaults")
    void testWrapperDefaults(final Class<?> wrapper, final Object defaultValue) {
        Assertions.assertEquals(defaultValue, TypeContext.of(wrapper).defaultOrNull());
    }

    @Test
    void testInterfacesAreObtainable() {
        Assertions.assertEquals(1, TypeContext.of(BoundUserImpl.class).interfaces().size());
        Assertions.assertEquals(TypeContext.of(User.class), TypeContext.of(BoundUserImpl.class).interfaces().get(0));
    }

    @ParameterizedTest
    @MethodSource("primitiveStrings")
    void testPrimitivesFromString(final Class<?> primitive, final String value, final Object real) throws TypeConversionException {
        final Object out = TypeContext.toPrimitive(TypeContext.of(primitive), value);
        Assertions.assertEquals(real, out);
    }
}
