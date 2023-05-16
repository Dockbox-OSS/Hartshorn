/*
 * Copyright 2019-2023 the original author or authors.
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

package test.org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Stream;

@HartshornTest(includeBasePackages = false)
public abstract class ElementContextTests {

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
    
    protected abstract Introspector introspector();

    @Test
    void testTypesAreCached() {
        final TypeView<ElementContextTests> tc1 = this.introspector().introspect(ElementContextTests.class);
        final TypeView<ElementContextTests> tc2 = this.introspector().introspect(ElementContextTests.class);
        Assertions.assertSame(tc1, tc2);
    }

    @Test
    void testCachedItemsAreNotReusedForDifferentTypes() {
        final TypeView<ElementContextTests> tc1 = this.introspector().introspect(ElementContextTests.class);
        final TypeView<Object> tc2 = this.introspector().introspect(Object.class);
        Assertions.assertNotSame(tc1, tc2);
    }

    @ParameterizedTest
    @MethodSource("primitives")
    public void testIsPrimitiveAcceptsPrimitives(final Class<?> primitive) {
        Assertions.assertTrue(this.introspector().introspect(primitive).isPrimitive());
    }

    @ParameterizedTest
    @MethodSource("wrappers")
    public void testIsPrimitiveRejectsPrimitiveWrappers(final Class<?> wrapper) {
        Assertions.assertFalse(this.introspector().introspect(wrapper).isPrimitive());
    }

    @Test
    public void testIsVoidAcceptsPrimitiveAndWrapper() {
        Assertions.assertTrue(this.introspector().introspect(void.class).isVoid());
        Assertions.assertTrue(this.introspector().introspect(Void.class).isVoid());
    }

    @Test
    public void testIsVoidRejectsNonPrimitiveAndNonWrapper() {
        Assertions.assertFalse(this.introspector().introspect(String.class).isVoid());
        Assertions.assertFalse(this.introspector().introspect(Object.class).isVoid());
    }

    @Test
    public void testIsPrimitiveRejectsNonPrimitiveAndNonWrapper() {
        Assertions.assertFalse(this.introspector().introspect(String.class).isPrimitive());
        Assertions.assertFalse(this.introspector().introspect(Object.class).isPrimitive());
    }

    @Test
    public void testIsPrimitiveRejectsVoidWrapper() {
        Assertions.assertFalse(this.introspector().introspect(Void.class).isPrimitive());
    }

    @Test
    public void testIsPrimitiveAcceptsVoidPrimitive() {
        Assertions.assertTrue(this.introspector().introspect(void.class).isPrimitive());
    }

    @Test
    public void testAnonymousTypesAreAnonymous() {
        final TypeView<Object> anonymous = this.introspector().introspect(new Object() {
        });
        Assertions.assertTrue(anonymous.isAnonymous());
    }

    @Test
    public void testNonAnonymousTypesAreNotAnonymous() {
        final TypeView<Object> anonymous = this.introspector().introspect(Object.class);
        Assertions.assertFalse(anonymous.isAnonymous());
    }

    @Test
    void testAnonymousWrappersReturnCorrectType() {
        final TypeView<Object> anonymous = this.introspector().introspect(new Object() {
        });
        Assertions.assertNotEquals(Object.class, anonymous.type());
    }

    @Test
    public void testEnumsAreEnum() {
        Assertions.assertTrue(this.introspector().introspect(TestEnumType.class).isEnum());
    }

    @Test
    public void testNonEnumsAreNotEnum() {
        Assertions.assertFalse(this.introspector().introspect(Object.class).isEnum());
    }

    @Test
    public void testEnumsAreNotAnonymous() {
        Assertions.assertFalse(this.introspector().introspect(TestEnumType.class).isAnonymous());
    }

    @Test
    public void enumConstantsCanBeObtained() {
        final TypeView<TestEnumType> enumContext = this.introspector().introspect(TestEnumType.class);
        Assertions.assertEquals(TestEnumType.VALUES.length, enumContext.enumConstants().size());
    }

    @Test
    public void testAnnotationsAreAnnotations() {
        Assertions.assertTrue(this.introspector().introspect(SampleAnnotation.class).isAnnotation());
    }

    @Test
    public void testNonAnnotationsAreNotAnnotations() {
        Assertions.assertFalse(this.introspector().introspect(Object.class).isAnnotation());
    }

    @Test
    public void testAnnotationsAreNotAnonymous() {
        Assertions.assertFalse(this.introspector().introspect(Annotation.class).isAnonymous());
    }

    @Test
    public void testAnnotationsAreNotEnum() {
        Assertions.assertFalse(this.introspector().introspect(Annotation.class).isEnum());
    }

    @Test
    public void testAnnotationsAreNotPrimitive() {
        Assertions.assertFalse(this.introspector().introspect(Annotation.class).isPrimitive());
    }

    @Test
    public void testAnnotationsAreNotVoid() {
        Assertions.assertFalse(this.introspector().introspect(Annotation.class).isVoid());
    }

    @Test
    public void testAnnotationsAreNotArray() {
        Assertions.assertFalse(this.introspector().introspect(Annotation.class).isArray());
    }

    @Test
    void testArraysAreArrays() {
        Assertions.assertTrue(this.introspector().introspect(Object[].class).isArray());
    }

    @Test
    void testArraysAreNotAnonymous() {
        Assertions.assertFalse(this.introspector().introspect(Object[].class).isAnonymous());
    }

    @Test
    void testArraysAreNotEnum() {
        Assertions.assertFalse(this.introspector().introspect(Object[].class).isEnum());
    }

    @Test
    void testArraysAreNotPrimitive() {
        Assertions.assertFalse(this.introspector().introspect(Object[].class).isPrimitive());
    }

    @Test
    void testArraysAreNotVoid() {
        Assertions.assertFalse(this.introspector().introspect(Object[].class).isVoid());
    }

    @Test
    void testArraysAreNotAnnotation() {
        Assertions.assertFalse(this.introspector().introspect(Object[].class).isAnnotation());
    }

    @ParameterizedTest
    @MethodSource("primitiveDefaults")
    void testPrimitiveDefaults(final Class<?> primitive, final Object defaultValue) {
        Assertions.assertEquals(defaultValue, this.introspector().introspect(primitive).defaultOrNull());
    }

    @Test
    void testObjectDefaultsToNull() {
        Assertions.assertNull(this.introspector().introspect(Object.class).defaultOrNull());
    }

    @Test
    void testAnnotationDefaultsToNull() {
        Assertions.assertNull(this.introspector().introspect(SampleAnnotation.class).defaultOrNull());
    }

    @Test
    void testEnumDefaultsToNull() {
        Assertions.assertNull(this.introspector().introspect(TestEnumType.class).defaultOrNull());
    }

    @Test
    void testArrayDefaultsToNull() {
        Assertions.assertNull(this.introspector().introspect(Object[].class).defaultOrNull());
    }

    @ParameterizedTest
    @MethodSource("wrapperDefaults")
    void testWrapperDefaults(final Class<?> wrapper, final Object defaultValue) {
        Assertions.assertEquals(defaultValue, this.introspector().introspect(wrapper).defaultOrNull());
    }

    @Test
    void testInterfacesAreObtainable() {
        Assertions.assertEquals(1, this.introspector().introspect(ImplementationWithTP.class).interfaces().size());
        Assertions.assertEquals(
                this.introspector().introspect(InterfaceWithTP.class),
                this.introspector().introspect(ImplementationWithTP.class).interfaces().get(0)
        );
    }

    @Test
    void testTypeParametersWithoutSourceAreFromSuperclass() {
        final TypeView<ImplementationWithTP> type = this.introspector().introspect(ImplementationWithTP.class);
        final TypeParametersIntrospector typeParameters = type.typeParameters();
        Assertions.assertEquals(1, typeParameters.count());
        Assertions.assertSame(Integer.class, typeParameters.at(0).get().type());
    }

    @Test
    void testTypeParametersThrowsIllegalArgumentOnNonInterface() {
        final TypeView<ImplementationWithTP> type = this.introspector().introspect(ImplementationWithTP.class);
        Assertions.assertThrows(IllegalArgumentException.class, () -> type.typeParameters().from(Object.class));
    }

    @Test
    void testTypeParametersWithSourceAreFromGivenSource() {
        final TypeView<ImplementationWithTP> type = this.introspector().introspect(ImplementationWithTP.class);
        final List<TypeView<?>> typeParameters = type.typeParameters().from(InterfaceWithTP.class);
        Assertions.assertEquals(1, typeParameters.size());
        Assertions.assertSame(String.class, typeParameters.get(0).type());
    }

    @Test
    void testAnnotatedTypeHasAnnotations() {
        final TypeView<AnnotatedElement> type = this.introspector().introspect(AnnotatedElement.class);
        Assertions.assertEquals(1, type.annotations().count());
        Assertions.assertSame(Sample.class, type.annotations().all().iterator().next().annotationType());
    }

    @Test
    void testAnnotatedTypeCanGetAnnotationFromAnnotation() {
        Assertions.assertTrue(this.introspector().introspect(AnnotatedElement.class)
                .annotations()
                .has(Sample.class));
    }

    @Test
    void testTypeViewCanReflect() {
        Assertions.assertDoesNotThrow(() -> this.introspector().introspect(TypeView.class));
    }

    @Test
    void testStaticMethodCanInvokeStatic() {
        final Option<MethodView<ElementContextTests, ?>> test = this.introspector().introspect(this)
                .methods()
                .named("testStatic");
        Assertions.assertTrue(test.present());
        final MethodView<ElementContextTests, ?> methodContext = test.get();
        Assertions.assertTrue(methodContext.modifiers().isStatic());
        final Attempt<?, ?> result = methodContext.invokeStatic();
        Assertions.assertTrue(result.errorAbsent());
    }

    public static void testStatic() {}

    @Test
    void testNonStaticMethodCannotInvokeStatic() {
        final Option<MethodView<ElementContextTests, ?>> test = this.introspector().introspect(this)
                .methods()
                .named("testNonStatic");
        Assertions.assertTrue(test.present());
        final MethodView<ElementContextTests, ?> methodContext = test.get();
        Assertions.assertFalse(methodContext.modifiers().isStatic());
        Assertions.assertThrows(IllegalIntrospectionException.class, () -> methodContext.invokeStatic());
    }

    public void testNonStatic() {}
}
