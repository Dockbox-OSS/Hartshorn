/*
 * Copyright 2019-2025 the original author or authors.
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

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import org.dockbox.hartshorn.util.introspect.IllegalIntrospectionException;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeParameterList;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.util.introspect.support.annotated.AnnotatedElement;
import test.org.dockbox.hartshorn.util.introspect.support.annotations.TypeOnlyAnnotation;
import test.org.dockbox.hartshorn.util.introspect.support.basic.TestEnumType;
import test.org.dockbox.hartshorn.util.introspect.support.typeparameters.AbstractTypeWithTypeParameter;
import test.org.dockbox.hartshorn.util.introspect.support.typeparameters.ImplementationWithTypeParameter;
import test.org.dockbox.hartshorn.util.introspect.support.typeparameters.InterfaceWithTypeParameter;

/**
 * Tests to verify {@link Introspector} implementations correctly expose basic type information.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class TypeIntrospectionTests {

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
    void typesAreCached() {
        TypeView<TypeIntrospectionTests> tc1 = this.introspector().introspect(TypeIntrospectionTests.class);
        TypeView<TypeIntrospectionTests> tc2 = this.introspector().introspect(TypeIntrospectionTests.class);
        assertThat(tc2).isSameAs(tc1);
    }

    @Test
    void cachedItemsAreNotReusedForDifferentTypes() {
        TypeView<TypeIntrospectionTests> tc1 = this.introspector().introspect(TypeIntrospectionTests.class);
        TypeView<Object> tc2 = this.introspector().introspect(Object.class);
        assertThat(tc2).isNotSameAs(tc1);
    }

    @ParameterizedTest
    @MethodSource("primitives")
    public void isPrimitiveAcceptsPrimitives(Class<?> primitive) {
        assertThat(this.introspector().introspect(primitive).isPrimitive()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("wrappers")
    public void isPrimitiveRejectsPrimitiveWrappers(Class<?> wrapper) {
        assertThat(this.introspector().introspect(wrapper).isPrimitive()).isFalse();
    }

    @Test
    public void isVoidAcceptsPrimitiveAndWrapper() {
        assertThat(this.introspector().introspect(void.class).isVoid()).isTrue();
        assertThat(this.introspector().introspect(Void.class).isVoid()).isTrue();
    }

    @Test
    public void isVoidRejectsNonPrimitiveAndNonWrapper() {
        assertThat(this.introspector().introspect(String.class).isVoid()).isFalse();
        assertThat(this.introspector().introspect(Object.class).isVoid()).isFalse();
    }

    @Test
    public void isPrimitiveRejectsNonPrimitiveAndNonWrapper() {
        assertThat(this.introspector().introspect(String.class).isPrimitive()).isFalse();
        assertThat(this.introspector().introspect(Object.class).isPrimitive()).isFalse();
    }

    @Test
    public void isPrimitiveRejectsVoidWrapper() {
        assertThat(this.introspector().introspect(Void.class).isPrimitive()).isFalse();
    }

    @Test
    public void isPrimitiveAcceptsVoidPrimitive() {
        assertThat(this.introspector().introspect(void.class).isPrimitive()).isTrue();
    }

    @Test
    public void anonymousTypesAreAnonymous() {
        TypeView<Object> anonymous = this.introspector().introspect(new Object() {
        });
        assertThat(anonymous.isAnonymous()).isTrue();
    }

    @Test
    public void nonAnonymousTypesAreNotAnonymous() {
        TypeView<Object> anonymous = this.introspector().introspect(Object.class);
        assertThat(anonymous.isAnonymous()).isFalse();
    }

    @Test
    void anonymousWrappersReturnCorrectType() {
        TypeView<Object> anonymous = this.introspector().introspect(new Object() {
        });
        assertThat(anonymous.type()).isNotEqualTo(Object.class);
    }

    @Test
    public void enumsAreEnum() {
        assertThat(this.introspector().introspect(TestEnumType.class).isEnum()).isTrue();
    }

    @Test
    public void nonEnumsAreNotEnum() {
        assertThat(this.introspector().introspect(Object.class).isEnum()).isFalse();
    }

    @Test
    public void enumsAreNotAnonymous() {
        assertThat(this.introspector().introspect(TestEnumType.class).isAnonymous()).isFalse();
    }

    @Test
    public void enumConstantsCanBeObtained() {
        TypeView<TestEnumType> enumContext = this.introspector().introspect(TestEnumType.class);
        assertThat(enumContext.enumConstants()).hasSameSizeAs(TestEnumType.values());
    }

    @Test
    public void annotationsAreAnnotations() {
        assertThat(this.introspector().introspect(TypeOnlyAnnotation.class).isAnnotation()).isTrue();
    }

    @Test
    public void nonAnnotationsAreNotAnnotations() {
        assertThat(this.introspector().introspect(Object.class).isAnnotation()).isFalse();
    }

    @Test
    public void annotationsAreNotAnonymous() {
        assertThat(this.introspector().introspect(Annotation.class).isAnonymous()).isFalse();
    }

    @Test
    public void annotationsAreNotEnum() {
        assertThat(this.introspector().introspect(Annotation.class).isEnum()).isFalse();
    }

    @Test
    public void annotationsAreNotPrimitive() {
        assertThat(this.introspector().introspect(Annotation.class).isPrimitive()).isFalse();
    }

    @Test
    public void annotationsAreNotVoid() {
        assertThat(this.introspector().introspect(Annotation.class).isVoid()).isFalse();
    }

    @Test
    public void annotationsAreNotArray() {
        assertThat(this.introspector().introspect(Annotation.class).isArray()).isFalse();
    }

    @Test
    void arraysAreArrays() {
        assertThat(this.introspector().introspect(Object[].class).isArray()).isTrue();
    }

    @Test
    void arraysAreNotAnonymous() {
        assertThat(this.introspector().introspect(Object[].class).isAnonymous()).isFalse();
    }

    @Test
    void arraysAreNotEnum() {
        assertThat(this.introspector().introspect(Object[].class).isEnum()).isFalse();
    }

    @Test
    void arraysAreNotPrimitive() {
        assertThat(this.introspector().introspect(Object[].class).isPrimitive()).isFalse();
    }

    @Test
    void arraysAreNotVoid() {
        assertThat(this.introspector().introspect(Object[].class).isVoid()).isFalse();
    }

    @Test
    void arraysAreNotAnnotation() {
        assertThat(this.introspector().introspect(Object[].class).isAnnotation()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("primitiveDefaults")
    void primitiveDefaults(Class<?> primitive, Object defaultValue) {
        assertThat(this.introspector().introspect(primitive).defaultOrNull()).isEqualTo(defaultValue);
    }

    @Test
    void objectDefaultsToNull() {
        assertThat(this.introspector().introspect(Object.class).defaultOrNull()).isNull();
    }

    @Test
    void annotationDefaultsToNull() {
        assertThat(this.introspector().introspect(TypeOnlyAnnotation.class).defaultOrNull()).isNull();
    }

    @Test
    void enumDefaultsToNull() {
        assertThat(this.introspector().introspect(TestEnumType.class).defaultOrNull()).isNull();
    }

    @Test
    void arrayDefaultsToNull() {
        assertThat(this.introspector().introspect(Object[].class).defaultOrNull()).isNull();
    }

    @ParameterizedTest
    @MethodSource("wrapperDefaults")
    void wrapperDefaults(Class<?> wrapper, Object defaultValue) {
        assertThat(this.introspector().introspect(wrapper).defaultOrNull()).isEqualTo(defaultValue);
    }

    @Test
    void interfacesAreObtainable() {
        assertThat(this.introspector().introspect(ImplementationWithTypeParameter.class).interfaces()).hasSize(1);
        assertThat(this.introspector().introspect(ImplementationWithTypeParameter.class).interfaces().get(0)).isEqualTo(this.introspector().introspect(InterfaceWithTypeParameter.class));
    }

    @Test
    void typeParametersWithoutSourceAreFromSuperclass() {
        TypeView<ImplementationWithTypeParameter> type = this.introspector().introspect(ImplementationWithTypeParameter.class);
        assertTypeParameterForType(type, AbstractTypeWithTypeParameter.class, Integer.class);
        assertTypeParameterForType(type, InterfaceWithTypeParameter.class, String.class);
    }

    private static void assertTypeParameterForType(TypeView<?> type, Class<?> forClass, Class<?> expectedClass) {
        TypeParameterList typeParameters = type.typeParameters().outputFor(forClass);
        assertThat(typeParameters.count()).isOne();

        TypeParameterView typeParameterView = typeParameters.atIndex(0).get();
        Option<TypeView<?>> upperBound = typeParameterView.resolvedType();
        assertThat(upperBound.present()).isTrue();
        assertThat(upperBound.get().type()).isSameAs(expectedClass);
    }

    @Test
    void annotatedTypeHasAnnotations() {
        TypeView<AnnotatedElement> type = this.introspector().introspect(AnnotatedElement.class);
        assertThat(type.annotations().count()).isOne();
        assertThat(CollectionUtilities.first(type.annotations().all()).annotationType()).isSameAs(TypeOnlyAnnotation.class);
    }

    @Test
    void annotatedTypeCanGetAnnotationFromAnnotation() {
        assertThat(this.introspector().introspect(AnnotatedElement.class)
                .annotations()
                .has(TypeOnlyAnnotation.class)).isTrue();
    }

    @Test
    void typeViewCanReflect() {
        assertThatCode(() -> this.introspector().introspect(TypeView.class)).doesNotThrowAnyException();
    }

    @Test
    void staticMethodCanInvokeStatic() throws Throwable {
        Option<MethodView<TypeIntrospectionTests, ?>> test = this.introspector().introspect(this)
                .methods()
                .named("testStatic");
        assertThat(test.present()).isTrue();
        MethodView<TypeIntrospectionTests, ?> methodContext = test.get();
        assertThat(methodContext.modifiers().isStatic()).isTrue();
        Option<?> result = methodContext.invokeStatic();
        // Void methods return empty option
        assertThat(result.absent()).isTrue();
    }

    public static void testStatic() {}

    @Test
    void nonStaticMethodCannotInvokeStatic() {
        Option<MethodView<TypeIntrospectionTests, ?>> test = this.introspector().introspect(this)
                .methods()
                .named("testNonStatic");
        assertThat(test.present()).isTrue();
        MethodView<TypeIntrospectionTests, ?> methodContext = test.get();
        assertThat(methodContext.modifiers().isStatic()).isFalse();
        assertThatExceptionOfType(IllegalIntrospectionException.class).isThrownBy(() -> methodContext.invokeStatic());
    }

    public void testNonStatic() {}
}
