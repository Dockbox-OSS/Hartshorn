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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.dockbox.hartshorn.util.collections.CollectionUtilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import org.dockbox.hartshorn.util.types.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import test.org.dockbox.hartshorn.util.introspect.support.annotated.AnnotatedObject;
import test.org.dockbox.hartshorn.util.introspect.support.annotations.AnyElementAnnotation;
import test.org.dockbox.hartshorn.util.introspect.support.annotations.MultipleElementAnnotation;
import test.org.dockbox.hartshorn.util.introspect.support.basic.ConcreteTestType;
import test.org.dockbox.hartshorn.util.introspect.support.basic.ParentTestType;
import test.org.dockbox.hartshorn.util.introspect.support.basic.TestEnumType;
import test.org.dockbox.hartshorn.util.introspect.support.bridge.BridgeElement;

/**
 * Tests for the {@link Introspector} interface. Unlike {@link TypeIntrospectionTests}, this class
 * focuses more on the complex implementations of the {@link Introspector} interface, rather than
 * basic type information.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class IntrospectorTests {

    private static Stream<Arguments> fields() {
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

    private static Stream<Arguments> methods() {
        return Stream.of(
            Arguments.of("publicMethod"),
            Arguments.of("privateMethod")
        );
    }

    private static Stream<Arguments> assignablePrimitives() {
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

    private static Stream<Arguments> nonVoidTypes() {
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

    private static Stream<Arguments> primitiveValues() {
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

    protected abstract Introspector introspector();

    @ParameterizedTest
    @MethodSource("fields")
    void fieldValueReturnsValue(String field) throws Throwable {
        ConcreteTestType instance = new ConcreteTestType();
        TypeView<ConcreteTestType> type = this.introspector().introspect(instance);
        Option<?> value = type.fields().named(field).get().get(instance);
        assertThat(value.present()).isTrue();
        assertThat(value.get()).isEqualTo(field);
    }

    @ParameterizedTest
    @MethodSource("methods")
    void runMethodReturnsValue(String method) throws Throwable {
        ConcreteTestType instance = new ConcreteTestType();
        TypeView<ConcreteTestType> type = this.introspector().introspect(instance);
        Option<?> value =
            type.methods().named(method, List.of(String.class)).get().invoke(instance, "value");
        assertThat(value.present()).isTrue();
        assertThat(value.get()).isEqualTo("VALUE");
    }

    @ParameterizedTest
    @MethodSource("assignablePrimitives")
    void assignableFromPrimitives(Class<?> primitive, Class<?> wrapper) {
        TypeView<?> pt = this.introspector().introspect(primitive);
        TypeView<?> wt = this.introspector().introspect(wrapper);
        assertThat(pt.isChildOf(wt.type())).isTrue();
        assertThat(wt.isChildOf(pt.type())).isTrue();
    }

    @Test
    void assignableFromSuper() {
        assertThat(this.introspector()
            .introspect(ConcreteTestType.class)
            .isChildOf(ParentTestType.class)).isTrue();
    }

    @Test
    void assignableFromSame() {
        assertThat(this.introspector()
            .introspect(ConcreteTestType.class)
            .isChildOf(ConcreteTestType.class)).isTrue();
    }

    @Test
    void assignableFromChild() {
        assertThat(this.introspector()
            .introspect(ParentTestType.class)
            .isChildOf(ConcreteTestType.class)).isFalse();
    }

    @Test
    void annotatedMethodsReturnsAllModifiers() {
        TypeView<ConcreteTestType> type = this.introspector().introspect(ConcreteTestType.class);
        List<MethodView<ConcreteTestType, ?>> methods =
            type.methods().annotatedWith(MultipleElementAnnotation.class);
        assertThat(methods).hasSize(3);

        List<String> names = methods.stream().map(MethodView::name).toList();
        assertThat(names)
                .contains("publicAnnotatedMethod")
                .contains("privateAnnotatedMethod");
    }

    @Test
    void staticFieldsReturnsAllModifiers() {
        List<FieldView<ConcreteTestType, ?>> fields =
            this.introspector().introspect(ConcreteTestType.class).fields().all().stream()
                .filter(field -> field.modifiers().isStatic())
                .toList();
        assertThat(fields).hasSize(2);
    }

    @Test
    void hasAnnotationOnMethod() {
        Option<MethodView<ConcreteTestType, ?>> method =
            this.introspector().introspect(ConcreteTestType.class)
                .methods()
                .named("publicAnnotatedMethod");
        assertThat(method.present()).isTrue();
        assertThat(method.get().annotations().has(MultipleElementAnnotation.class)).isTrue();
    }

    @Test
    void superTypesReturnsAllSuperTypesWithoutObject() {
        TypeView<?> parent = this.introspector().introspect(ConcreteTestType.class).superClass();
        assertThat(parent.isVoid()).isFalse();
        assertThat(parent.type()).isSameAs(ParentTestType.class);
    }

    @Test
    void methodsReturnsAllDeclaredAndParentMethods() {
        TypeView<ConcreteTestType> type = this.introspector().introspect(ConcreteTestType.class);
        List<MethodView<ConcreteTestType, ?>> methods = type.methods().all();
        boolean fail = true;
        for (MethodView<ConcreteTestType, ?> method : methods) {
            if ("parentMethod".equals(method.name())) {
                fail = false;
            }
        }
        if (fail) {
            org.assertj.core.api.Assertions.fail("Parent types were not included");
        }
    }

    @Test
    void typeContextMethodsDoNotIncludeBridgeMethods() {
        TypeView<BridgeElement> bridge = this.introspector().introspect(BridgeElement.class);
        List<MethodView<BridgeElement, ?>> methods = bridge.methods().all();
        for (MethodView<BridgeElement, ?> method : methods) {
            Option<Method> nativeMethod = method.method();
            assertThat(nativeMethod.present()).isTrue();
            assertThat(nativeMethod.get().isBridge()).isFalse();
        }
    }

    @Test
    void typeContextBridgeMethodsCanBeObtained() {
        TypeView<BridgeElement> bridge = this.introspector().introspect(BridgeElement.class);
        List<MethodView<BridgeElement, ?>> methods = bridge.methods().bridges();
        assertThat(methods).hasSize(1);
        assertThat(methods.get(0).returnType().type()).isSameAs(Object.class);
        assertThat(methods.get(0).method().present()).isTrue();
        assertThat(methods.get(0).method().get().isBridge()).isTrue();
    }

    @Test
    void lookupReturnsClassIfPresent() {
        TypeView<?> lookup = this.introspector().introspect(ConcreteTestType.class.getName());
        assertThat(lookup).isNotNull();
        assertThat(lookup.type()).isSameAs(ConcreteTestType.class);
    }

    @Test
    void lookupReturnsVoidIfAbsent() {
        TypeView<?> lookup =
            this.introspector().introspect("org.dockbox.hartshorn.util.AnotherClass");
        assertThat(lookup.isVoid()).isTrue();
    }

    @Test
    void hasMethodIsTrueIfMethodExists() {
        assertThat(this.introspector().introspect(ConcreteTestType.class)
            .methods()
            .named("publicMethod", String.class)
            .present()).isTrue();
    }

    @Test
    void hasMethodIsFalseIfMethodDoesNotExist() {
        assertThat(this.introspector().introspect(ConcreteTestType.class)
            .methods()
            .named("otherMethod")
            .present()).isFalse();
    }

    @Test
    void instanceHasMethodIsTrueIfMethodExists() {
        assertThat(this.introspector().introspect(new ConcreteTestType())
            .methods()
            .named("publicMethod", String.class)
            .present()).isTrue();
    }

    @Test
    void instanceHasMethodIsFalseIfMethodDoesNotExist() {
        assertThat(this.introspector().introspect(new ConcreteTestType())
            .methods()
            .named("otherMethod")
            .present()).isFalse();
    }

    @ParameterizedTest
    @MethodSource("nonVoidTypes")
    void voidIsFalseIfTypeIsNotVoid(Class<?> type) {
        assertThat(this.introspector().introspect(type).isVoid()).isFalse();
    }

    @Test
    void voidIsTrueIfTypeIsVoid() {
        assertThat(this.introspector().introspect(Void.class).isVoid()).isTrue();
    }

    @Test
    void voidIsTrueIfTypeIsVoidPrimitive() {
        assertThat(this.introspector().introspect(void.class).isVoid()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("fields")
    void hasFieldReturnsTrue(String field) {
        assertThat(this.introspector().introspect(ConcreteTestType.class)
            .fields()
            .named(field)
            .present()).isTrue();
    }

    @ParameterizedTest
    @MethodSource("fields")
    void fieldsConsumesAllFields(String field) {
        boolean activated = false;
        TypeView<ConcreteTestType> type = this.introspector().introspect(ConcreteTestType.class);
        for (FieldView<ConcreteTestType, ?> fieldView : type.fields().all()) {
            if (fieldView.name().equals(field)) {
                activated = true;
            }
        }
        assertThat(activated).isTrue();
    }

    @Test
    void setFieldUpdatesAccessorField() throws Throwable {
        Field fieldRef = ConcreteTestType.class.getDeclaredField("accessorField");
        FieldView<?, ?> field = this.introspector().introspect(fieldRef);
        ConcreteTestType instance = new ConcreteTestType();
        field.set(instance, "newValue");

        assertThat(instance.activatedSetter()).isTrue();
    }

    @Test
    void setFieldUpdatesNormalField() throws Throwable {
        Field fieldRef = ConcreteTestType.class.getDeclaredField("publicField");
        FieldView<?, ?> field = this.introspector().introspect(fieldRef);
        ConcreteTestType instance = new ConcreteTestType();
        field.set(instance, "newValue");

        assertThat(instance.publicField).isEqualTo("newValue");
    }

    @Test
    void annotatedFieldsIncludesStatic() {
        List<FieldView<ConcreteTestType, ?>> fields =
            this.introspector().introspect(ConcreteTestType.class).fields().annotatedWith(
                MultipleElementAnnotation.class);
        assertThat(fields).hasSize(2);
        int statics = 0;
        for (FieldView<ConcreteTestType, ?> field : fields) {
            if (field.modifiers().isStatic()) {
                statics++;
            }
        }
        assertThat(statics).isOne();
    }

    @Test
    void annotatedConstructors() {
        TypeView<ConcreteTestType> type = this.introspector().introspect(ConcreteTestType.class);
        List<ConstructorView<ConcreteTestType>> constructors =
            type.constructors().annotatedWith(MultipleElementAnnotation.class);
        assertThat(constructors).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("primitiveValues")
    void stringToPrimitive(Class<?> type, String value, Object expected)
        throws Exception {
        Object o = TypeUtils.toPrimitive(type, value);
        assertThat(o)
                .isNotNull()
                .isEqualTo(expected);
    }

    @Test
    void redefinedAnnotationsTakePriority() {
        TypeView<AnnotatedObject> typeContext =
            this.introspector().introspect(AnnotatedObject.class);
        Option<AnyElementAnnotation> annotation =
            typeContext.annotations().get(AnyElementAnnotation.class);
        assertThat(annotation.present()).isTrue();
        assertThat(annotation.get().value()).isEqualTo("impl");
    }

    @Test
    public void genericTypeTests() {
        ParameterView<?> parameter = this.introspector().introspect(this)
            .methods()
            .named("genericTestMethod", List.class)
            .get()
            .parameters()
            .at(0)
            .orNull();

        TypeView<?> first = parameter.genericType();

        assertThat(first.is(List.class)).isTrue();
        assertThat(first.typeParameters().allInput().count()).isOne();

        TypeView<?> second = first.typeParameters().atIndex(0)
            .orElseGet(Assertions::fail)
            .resolvedType().orNull();
        assertThat(second).isNotNull();
        assertThat(second.is(List.class)).isTrue();
        assertThat(second.typeParameters().allInput().count()).isOne();

        TypeView<?> third = second.typeParameters().atIndex(0)
            .orElseGet(Assertions::fail)
            .resolvedType().orNull();
        assertThat(third).isNotNull();
        assertThat(third.is(String.class)).isTrue();
        assertThat(third.typeParameters().allInput().count()).isZero();
    }

    @SuppressWarnings("unused") // Used by genericTypeTests
    public void genericTestMethod(List<List<String>> nestedGeneric) {
    }

    @SuppressWarnings("unused") // Used by testWildcardsWithUpperBounds
    public void methodWithWildcardUpperbounds(List<String> list) {
    }

    @Test
    void wildcardsWithUpperBounds() {
        ParameterView<?> parameter = this.introspector().introspect(this)
            .methods()
            .named("methodWithWildcardUpperbounds", List.class)
            .get()
            .parameters()
            .at(0)
            .orNull();

        assertThat(parameter).isNotNull();
        TypeView<?> first = parameter.genericType();

        assertThat(first.is(List.class)).isTrue();
        assertThat(first.typeParameters().allInput().count()).isOne();

        TypeView<?> second = first.typeParameters().atIndex(0)
            .orElseGet(Assertions::fail)
            .resolvedType().orNull();
        assertThat(second).isNotNull();
        assertThat(second.is(String.class)).isTrue();
        assertThat(second.isWildcard()).isFalse();
    }

    @Test
    void concreteClassIsCorrectlyIdentified() {
        TypeView<ConcreteClass> type = this.introspector().introspect(ConcreteClass.class);
        assertThat(type.modifiers().isAbstract()).isFalse();
        assertThat(type.isInterface()).isFalse();
        assertThat(type.isRecord()).isFalse();
        assertThat(type.isEnum()).isFalse();
        assertThat(type.isAnnotation()).isFalse();
    }

    @Test
    void abstractClassIsCorrectlyIdentified() {
        TypeView<AbstractClass> type = this.introspector().introspect(AbstractClass.class);
        assertThat(type.modifiers().isAbstract()).isTrue();
        assertThat(type.isInterface()).isFalse();
        assertThat(type.isRecord()).isFalse();
        assertThat(type.isEnum()).isFalse();
        assertThat(type.isAnnotation()).isFalse();
    }

    @Test
    void interfaceIsCorrectlyIdentified() {
        TypeView<Interface> type = this.introspector().introspect(Interface.class);
        assertThat(type.modifiers().isAbstract()).isTrue();
        assertThat(type.isInterface()).isTrue();
        assertThat(type.isRecord()).isFalse();
        assertThat(type.isEnum()).isFalse();
        assertThat(type.isAnnotation()).isFalse();
    }

    @Test
    void recordIsCorrectlyIdentified() {
        TypeView<RecordType> type = this.introspector().introspect(RecordType.class);
        assertThat(type.modifiers().isAbstract()).isFalse();
        assertThat(type.isInterface()).isFalse();
        assertThat(type.isRecord()).isTrue();
        assertThat(type.isEnum()).isFalse();
        assertThat(type.isAnnotation()).isFalse();
    }

    @Test
    void enumIsCorrectlyIdentified() {
        TypeView<EnumType> type = this.introspector().introspect(EnumType.class);
        assertThat(type.modifiers().isAbstract()).isFalse();
        assertThat(type.isInterface()).isFalse();
        assertThat(type.isRecord()).isFalse();
        assertThat(type.isEnum()).isTrue();
        assertThat(type.isAnnotation()).isFalse();
    }

    @Test
    void annotationIsCorrectlyIdentified() {
        TypeView<AnnotationType> type = this.introspector().introspect(AnnotationType.class);
        assertThat(type.modifiers().isAbstract()).isTrue();
        assertThat(type.isInterface()).isTrue();
        assertThat(type.isRecord()).isFalse();
        assertThat(type.isEnum()).isFalse();
        assertThat(type.isAnnotation()).isTrue();
    }

    private static class ConcreteClass {
    }

    private abstract static class AbstractClass {
    }

    private interface Interface {
    }

    private record RecordType() {
    }

    private enum EnumType {}

    private @interface AnnotationType {
    }

    @Test
    void genericTypeWithWildcardUsesUpperbounds() {
        Option<FieldView<IntrospectorTests, ?>> field =
            this.introspector().introspect(this).fields().named("genericType");
        assertThat(field.present()).isTrue();

        TypeParametersIntrospector parametersIntrospector =
            field.get().genericType().typeParameters();
        assertThat(parametersIntrospector.allInput().count()).isOne();

        TypeParameterView typeParameter = parametersIntrospector.atIndex(0)
            .orElseGet(Assertions::fail);

        TypeView<?> parameter = typeParameter
            .resolvedType().orNull();
        assertThat(parameter.isWildcard()).isTrue();

        Set<TypeView<?>> upperBounds = typeParameter.upperBounds();
        assertThat(upperBounds).hasSize(1);
        assertThat(CollectionUtilities.first(upperBounds).type()).isSameAs(Object.class);
    }

    @SuppressWarnings("unused") // Used by testGenericTypeWithWildcardUsesUpperbounds
    private final List<?> genericType = new ArrayList<>();

    @Test
    void parameterizableTypeCanBeIntrospected() {
        ParameterizableType argumentType = ParameterizableType.create(String.class);
        ParameterizableType collectionType = ParameterizableType.builder(List.class)
            .parameters(argumentType)
            .build();

        TypeView<?> typeView = this.introspector().introspect(collectionType);
        assertThat(typeView.is(List.class)).isTrue();
        assertThat(typeView.typeParameters().allInput().count()).isOne();

        TypeParameterView typeParameterView = typeView.typeParameters().atIndex(0)
            .orElseGet(Assertions::fail);

        TypeView<?> argumentView = typeParameterView.resolvedType()
            .orElseGet(Assertions::fail);

        assertThat(argumentView.is(String.class)).isTrue();
    }
}
