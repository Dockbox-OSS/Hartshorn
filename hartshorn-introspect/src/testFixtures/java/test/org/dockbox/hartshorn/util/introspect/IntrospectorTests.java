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

import org.dockbox.hartshorn.util.TypeConversionException;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
    void testFieldValueReturnsValue(final String field) {
        final ReflectTestType instance = new ReflectTestType();
        final TypeView<ReflectTestType> type = this.introspector().introspect(instance);
        final Option<?> value = type.fields().named(field).get().get(instance);
        Assertions.assertTrue(value.present());
        Assertions.assertEquals(field, value.get());
    }

    @ParameterizedTest
    @MethodSource("methods")
    void testRunMethodReturnsValue(final String method) {
        final ReflectTestType instance = new ReflectTestType();
        final TypeView<ReflectTestType> type = this.introspector().introspect(instance);
        final Option<?> value = type.methods().named(method, List.of(String.class)).get().invoke(instance, "value");
        Assertions.assertTrue(value.present());
        Assertions.assertEquals("VALUE", value.get());
    }

    @ParameterizedTest
    @MethodSource("assignablePrimitives")
    void testAssignableFromPrimitives(final Class<?> primitive, final Class<?> wrapper) {
        final TypeView<?> pt = this.introspector().introspect(primitive);
        final TypeView<?> wt = this.introspector().introspect(wrapper);
        Assertions.assertTrue(pt.isChildOf(wt.type()));
        Assertions.assertTrue(wt.isChildOf(pt.type()));
    }

    @Test
    void testAssignableFromSuper() {
        Assertions.assertTrue(this.introspector().introspect(ReflectTestType.class).isChildOf(ParentTestType.class));
    }

    @Test
    void testAssignableFromSame() {
        Assertions.assertTrue(this.introspector().introspect(ReflectTestType.class).isChildOf(ReflectTestType.class));
    }

    @Test
    void testAssignableFromChild() {
        Assertions.assertFalse(this.introspector().introspect(ParentTestType.class).isChildOf(ReflectTestType.class));
    }

    @Test
    void testAnnotatedMethodsReturnsAllModifiers() {
        final TypeView<ReflectTestType> type = this.introspector().introspect(ReflectTestType.class);
        final List<MethodView<ReflectTestType, ?>> methods = type.methods().annotatedWith(Demo.class);
        Assertions.assertEquals(3, methods.size());

        final List<String> names = methods.stream().map(MethodView::name).toList();
        Assertions.assertTrue(names.contains("publicAnnotatedMethod"));
        Assertions.assertTrue(names.contains("privateAnnotatedMethod"));
    }

    @Test
    void testStaticFieldsReturnsAllModifiers() {
        final List<FieldView<ReflectTestType, ?>> fields = this.introspector().introspect(ReflectTestType.class).fields().all().stream()
                .filter(field -> field.modifiers().isStatic())
                .toList();
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    void testHasAnnotationOnMethod() {
        final Option<MethodView<ReflectTestType, ?>> method = this.introspector().introspect(ReflectTestType.class)
                .methods()
                .named("publicAnnotatedMethod");
        Assertions.assertTrue(method.present());
        Assertions.assertTrue(method.get().annotations().has(Demo.class));
    }

    @Test
    void testSuperTypesReturnsAllSuperTypesWithoutObject() {
        final TypeView<?> parent = this.introspector().introspect(ReflectTestType.class).superClass();
        Assertions.assertFalse(parent.isVoid());
        Assertions.assertSame(ParentTestType.class, parent.type());
    }

    @Test
    void testMethodsReturnsAllDeclaredAndParentMethods() {
        final TypeView<ReflectTestType> type = this.introspector().introspect(ReflectTestType.class);
        final List<MethodView<ReflectTestType, ?>> methods = type.methods().all();
        boolean fail = true;
        for (final MethodView<ReflectTestType, ?> method : methods) {
            if ("parentMethod".equals(method.name())) fail = false;
        }
        if (fail) Assertions.fail("Parent types were not included");
    }

    @Test
    void testTypeContextMethodsDoNotIncludeBridgeMethods() {
        final TypeView<BridgeImpl> bridge = this.introspector().introspect(BridgeImpl.class);
        final List<MethodView<BridgeImpl, ?>> methods = bridge.methods().all();
        for (final MethodView<BridgeImpl, ?> method : methods) {
            final Option<Method> nativeMethod = method.method();
            Assertions.assertTrue(nativeMethod.present());
            Assertions.assertFalse(nativeMethod.get().isBridge());
        }
    }

    @Test
    void testTypeContextBridgeMethodsCanBeObtained() {
        final TypeView<BridgeImpl> bridge = this.introspector().introspect(BridgeImpl.class);
        final List<MethodView<BridgeImpl, ?>> methods = bridge.methods().bridges();
        Assertions.assertEquals(1, methods.size());
        Assertions.assertSame(Object.class, methods.get(0).returnType().type());
        Assertions.assertTrue(methods.get(0).method().present());
        Assertions.assertTrue(methods.get(0).method().get().isBridge());
    }

    @Test
    void testLookupReturnsClassIfPresent() {
        final TypeView<?> lookup = this.introspector().introspect(ReflectTestType.class.getName());
        Assertions.assertNotNull(lookup);
        Assertions.assertSame(ReflectTestType.class, lookup.type());
    }

    @Test
    void testLookupReturnsVoidIfAbsent() {
        final TypeView<?> lookup = this.introspector().introspect("org.dockbox.hartshorn.util.AnotherClass");
        Assertions.assertTrue(lookup.isVoid());
    }

    @Test
    void testHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(this.introspector().introspect(ReflectTestType.class)
                .methods()
                .named("publicMethod", String.class)
                .present());
    }

    @Test
    void testHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(this.introspector().introspect(ReflectTestType.class)
                .methods()
                .named("otherMethod")
                .present());
    }

    @Test
    void testInstanceHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(this.introspector().introspect(new ReflectTestType())
                .methods()
                .named("publicMethod", String.class)
                .present());
    }

    @Test
    void testInstanceHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(this.introspector().introspect(new ReflectTestType())
                .methods()
                .named("otherMethod")
                .present());
    }

    @ParameterizedTest
    @MethodSource("nonVoidTypes")
    void testVoidIsFalseIfTypeIsNotVoid(final Class<?> type) {
        Assertions.assertFalse(this.introspector().introspect(type).isVoid());
    }

    @Test
    void testVoidIsTrueIfTypeIsVoid() {
        Assertions.assertTrue(this.introspector().introspect(Void.class).isVoid());
    }

    @Test
    void testVoidIsTrueIfTypeIsVoidPrimitive() {
        Assertions.assertTrue(this.introspector().introspect(void.class).isVoid());
    }

    @ParameterizedTest
    @MethodSource("fields")
    void testHasFieldReturnsTrue(final String field) {
        Assertions.assertTrue(this.introspector().introspect(ReflectTestType.class)
                .fields()
                .named(field)
                .present());
    }

    @ParameterizedTest
    @MethodSource("fields")
    void testFieldsConsumesAllFields(final String field) {
        boolean activated = false;
        final TypeView<ReflectTestType> type = this.introspector().introspect(ReflectTestType.class);
        for (final FieldView<ReflectTestType, ?> fieldView : type.fields().all()) {
            if (fieldView.name().equals(field)) activated = true;
        }
        Assertions.assertTrue(activated);
    }

    @Test
    void testSetFieldUpdatesAccessorField() throws NoSuchFieldException {
        final Field fieldRef = ReflectTestType.class.getDeclaredField("accessorField");
        final FieldView<?, ?> field = this.introspector().introspect(fieldRef);
        final ReflectTestType instance = new ReflectTestType();
        field.set(instance, "newValue");

        Assertions.assertTrue(instance.activatedSetter());
    }

    @Test
    void testSetFieldUpdatesNormalField() throws NoSuchFieldException {
        final Field fieldRef = ReflectTestType.class.getDeclaredField("publicField");
        final FieldView<?, ?> field = this.introspector().introspect(fieldRef);
        final ReflectTestType instance = new ReflectTestType();
        field.set(instance, "newValue");

        Assertions.assertEquals("newValue", instance.publicField);
    }

    @Test
    void testAnnotatedFieldsIncludesStatic() {
        final List<FieldView<ReflectTestType, ?>> fields = this.introspector().introspect(ReflectTestType.class).fields().annotatedWith(Demo.class);
        Assertions.assertEquals(2, fields.size());
        int statics = 0;
        for (final FieldView<ReflectTestType, ?> field : fields) {
            if (field.modifiers().isStatic()) statics++;
        }
        Assertions.assertEquals(1, statics);
    }

    @Test
    void testAnnotatedConstructors() {
        final TypeView<ReflectTestType> type = this.introspector().introspect(ReflectTestType.class);
        final List<ConstructorView<ReflectTestType>> constructors = type.constructors().annotatedWith(Demo.class);
        Assertions.assertEquals(1, constructors.size());
    }

    @ParameterizedTest
    @MethodSource("primitiveValues")
    void testStringToPrimitive(final Class<?> type, final String value, final Object expected) throws TypeConversionException {
        final Object o = TypeUtils.toPrimitive(type, value);
        Assertions.assertNotNull(o);
        Assertions.assertEquals(expected, o);
    }

    @Test
    void testRedefinedAnnotationsTakePriority() {
        final TypeView<AnnotatedImpl> typeContext = this.introspector().introspect(AnnotatedImpl.class);
        final Option<Base> annotation = typeContext.annotations().get(Base.class);
        Assertions.assertTrue(annotation.present());
        Assertions.assertEquals("impl", annotation.get().value());
    }

    @Test
    public void genericTypeTests() {
        final ParameterView<?> parameter = this.introspector().introspect(this)
                .methods()
                .named("genericTestMethod", List.class)
                .get()
                .parameters()
                .at(0)
                .orNull();

        final TypeView<?> first = parameter.genericType();

        Assertions.assertTrue(first.is(List.class));
        Assertions.assertEquals(1, first.typeParameters().count());

        final TypeView<?> second = first.typeParameters().at(0).orNull();
        Assertions.assertNotNull(second);
        Assertions.assertTrue(second.is(List.class));
        Assertions.assertEquals(1, second.typeParameters().count());

        final TypeView<?> third = second.typeParameters().at(0).orNull();
        Assertions.assertNotNull(third);
        Assertions.assertTrue(third.is(String.class));
        Assertions.assertEquals(0, third.typeParameters().count());
    }

    @SuppressWarnings("unused") // Used by genericTypeTests
    public void genericTestMethod(final List<List<String>> nestedGeneric) { }

    @SuppressWarnings("unused") // Used by testWildcardsWithUpperBounds
    public void methodWithWildcardUpperbounds(final List<? extends String> list) { }

    @Test
    void testWildcardsWithUpperBounds() {
        final ParameterView<?> parameter = this.introspector().introspect(this)
                .methods()
                .named("methodWithWildcardUpperbounds", List.class)
                .get()
                .parameters()
                .at(0)
                .orNull();

        Assertions.assertNotNull(parameter);
        final TypeView<?> first = parameter.genericType();

        Assertions.assertTrue(first.is(List.class));
        Assertions.assertEquals(1, first.typeParameters().count());

        final TypeView<?> second = first.typeParameters().at(0).orNull();
        Assertions.assertNotNull(second);
        Assertions.assertTrue(second.is(String.class));
        Assertions.assertFalse(second.isWildcard());
    }

    @Test
    void concreteClassIsCorrectlyIdentified() {
        final TypeView<ConcreteClass> type = this.introspector().introspect(ConcreteClass.class);
        Assertions.assertFalse(type.modifiers().isAbstract());
        Assertions.assertFalse(type.isInterface());
        Assertions.assertFalse(type.isRecord());
        Assertions.assertFalse(type.isEnum());
        Assertions.assertFalse(type.isAnnotation());
    }

    @Test
    void abstractClassIsCorrectlyIdentified() {
        final TypeView<AbstractClass> type = this.introspector().introspect(AbstractClass.class);
        Assertions.assertTrue(type.modifiers().isAbstract());
        Assertions.assertFalse(type.isInterface());
        Assertions.assertFalse(type.isRecord());
        Assertions.assertFalse(type.isEnum());
        Assertions.assertFalse(type.isAnnotation());
    }

    @Test
    void interfaceIsCorrectlyIdentified() {
        final TypeView<Interface> type = this.introspector().introspect(Interface.class);
        Assertions.assertTrue(type.modifiers().isAbstract());
        Assertions.assertTrue(type.isInterface());
        Assertions.assertFalse(type.isRecord());
        Assertions.assertFalse(type.isEnum());
        Assertions.assertFalse(type.isAnnotation());
    }

    @Test
    void recordIsCorrectlyIdentified() {
        final TypeView<RecordType> type = this.introspector().introspect(RecordType.class);
        Assertions.assertFalse(type.modifiers().isAbstract());
        Assertions.assertFalse(type.isInterface());
        Assertions.assertTrue(type.isRecord());
        Assertions.assertFalse(type.isEnum());
        Assertions.assertFalse(type.isAnnotation());
    }

    @Test
    void enumIsCorrectlyIdentified() {
        final TypeView<EnumType> type = this.introspector().introspect(EnumType.class);
        Assertions.assertFalse(type.modifiers().isAbstract());
        Assertions.assertFalse(type.isInterface());
        Assertions.assertFalse(type.isRecord());
        Assertions.assertTrue(type.isEnum());
        Assertions.assertFalse(type.isAnnotation());
    }

    @Test
    void annotationIsCorrectlyIdentified() {
        final TypeView<AnnotationType> type = this.introspector().introspect(AnnotationType.class);
        Assertions.assertTrue(type.modifiers().isAbstract());
        Assertions.assertTrue(type.isInterface());
        Assertions.assertFalse(type.isRecord());
        Assertions.assertFalse(type.isEnum());
        Assertions.assertTrue(type.isAnnotation());
    }

    private static class ConcreteClass {}
    private abstract static class AbstractClass {}
    private interface Interface {}
    private record RecordType() {}
    private enum EnumType {}
    private @interface AnnotationType {}

    @Test
    void testGenericTypeWithWildcardUsesUpperbounds() {
        final Option<FieldView<IntrospectorTests, ?>> field = this.introspector().introspect(this).fields().named("genericType");
        Assertions.assertTrue(field.present());

        final TypeParametersIntrospector parametersIntrospector = field.get().genericType().typeParameters();
        Assertions.assertEquals(1, parametersIntrospector.count());

        final TypeView<?> parameter = parametersIntrospector.at(0).get();
        Assertions.assertFalse(parameter.isWildcard());
        Assertions.assertTrue(parameter.is(Object.class));
    }

    @SuppressWarnings("unused") // Used by testGenericTypeWithWildcardUsesUpperbounds
    private final List<?> genericType = new ArrayList<>();
}
