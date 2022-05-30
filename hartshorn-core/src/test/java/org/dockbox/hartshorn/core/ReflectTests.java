/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.Base;
import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.core.bridge.BridgeImpl;
import org.dockbox.hartshorn.application.scan.PrefixContext;
import org.dockbox.hartshorn.application.scan.ReflectionsPrefixContext;
import org.dockbox.hartshorn.util.reflect.AnnotatedElementModifier;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.MethodModifier;
import org.dockbox.hartshorn.util.reflect.ParameterContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.reflect.WildcardTypeContext;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.TypeConversionException;
import org.dockbox.hartshorn.core.types.AnnotatedImpl;
import org.dockbox.hartshorn.core.types.ParentTestType;
import org.dockbox.hartshorn.core.types.ReflectTestType;
import org.dockbox.hartshorn.core.types.TestEnumType;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.testsuite.InjectTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javassist.util.proxy.ProxyFactory;

@HartshornTest
public class ReflectTests {

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

    private static Stream<Arguments> genericInstances() {
        return Stream.of(
                Arguments.of(new ReflectTestType(), ReflectTestType.class, true),
                Arguments.of(new ReflectTestType(), ParentTestType.class, true),
                Arguments.of(null, ReflectTestType.class, false),
                Arguments.of(new ReflectTestType(), String.class, false)
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

    @ParameterizedTest
    @MethodSource("fields")
    void testFieldValueReturnsValue(final String field) {
        final ReflectTestType instance = new ReflectTestType();
        final TypeContext<ReflectTestType> type = TypeContext.of(instance);
        final Result<?> value = type.field(field).get().get(instance);
        Assertions.assertTrue(value.present());
        Assertions.assertEquals(field, value.get());
    }

    @ParameterizedTest
    @MethodSource("methods")
    void testRunMethodReturnsValue(final String method) {
        final ReflectTestType instance = new ReflectTestType();
        final TypeContext<ReflectTestType> type = TypeContext.of(instance);
        final Result<?> value = type.method(method, List.of(TypeContext.of(String.class))).get().invoke(instance, "value");
        Assertions.assertTrue(value.present());
        Assertions.assertEquals("VALUE", value.get());
    }

    @ParameterizedTest
    @MethodSource("assignablePrimitives")
    void testAssignableFromPrimitives(final Class<?> primitive, final Class<?> wrapper) {
        final TypeContext<?> pt = TypeContext.of(primitive);
        final TypeContext<?> wt = TypeContext.of(wrapper);
        Assertions.assertTrue(pt.childOf(wt));
        Assertions.assertTrue(wt.childOf(pt));
    }

    @Test
    void testAssignableFromSuper() {
        Assertions.assertTrue(TypeContext.of(ReflectTestType.class).childOf(ParentTestType.class));
    }

    @Test
    void testAssignableFromSame() {
        Assertions.assertTrue(TypeContext.of(ReflectTestType.class).childOf(ReflectTestType.class));
    }

    @Test
    void testAnnotatedMethodsReturnsAllModifiers() {
        final TypeContext<ReflectTestType> type = TypeContext.of(ReflectTestType.class);
        final List<MethodContext<?, ReflectTestType>> methods = type.methods(Demo.class);
        Assertions.assertEquals(3, methods.size());

        final List<String> names = methods.stream().map(MethodContext::name).toList();
        Assertions.assertTrue(names.contains("publicAnnotatedMethod"));
        Assertions.assertTrue(names.contains("privateAnnotatedMethod"));
    }

    @InjectTest
    void testAnnotatedTypesReturnsAllInPrefix(final ApplicationManager manager) {
        final PrefixContext context = new ReflectionsPrefixContext(manager);
        context.prefix("org.dockbox.hartshorn.core.types");
        final Collection<TypeContext<?>> types = context.types(Demo.class);
        Assertions.assertEquals(1, types.size());
        Assertions.assertEquals(ReflectTestType.class, types.iterator().next().type());
    }

    @InjectTest
    void testSubTypesReturnsAllSubTypes(final ApplicationManager manager) {
        final PrefixContext context = new ReflectionsPrefixContext(manager);
        context.prefix("org.dockbox.hartshorn.core.types");
        final Collection<TypeContext<? extends ParentTestType>> types = context.children(ParentTestType.class);
        Assertions.assertEquals(1, types.size());
        Assertions.assertEquals(ReflectTestType.class, types.iterator().next().type());
    }

    @Test
    void testStaticFieldsReturnsAllModifiers() {
        final List<FieldContext<?>> fields = TypeContext.of(ReflectTestType.class).fields().stream()
                .filter(FieldContext::isStatic)
                .toList();
        Assertions.assertEquals(2, fields.size());
    }

    @Test
    void testHasAnnotationOnMethod() {
        final Result<MethodContext<?, ReflectTestType>> method = TypeContext.of(ReflectTestType.class).method("publicAnnotatedMethod");
        Assertions.assertTrue(method.present());
        Assertions.assertTrue(method.get().annotation(Demo.class).present());
    }

    @Test
    void testSuperTypesReturnsAllSuperTypesWithoutObject() {
        final TypeContext<?> parent = TypeContext.of(ReflectTestType.class).parent();
        Assertions.assertFalse(parent.isVoid());
        Assertions.assertEquals(ParentTestType.class, parent.type());
    }

    @Test
    void testMethodsReturnsAllDeclaredAndParentMethods() {
        final TypeContext<ReflectTestType> type = TypeContext.of(ReflectTestType.class);
        final List<MethodContext<?, ReflectTestType>> methods = type.methods();
        boolean fail = true;
        for (final MethodContext<?, ReflectTestType> method : methods) {
            if ("parentMethod".equals(method.name())) fail = false;
        }
        if (fail) Assertions.fail("Parent types were not included");
    }

    @Test
    void testTypeContextMethodsDoNotIncludeBridgeMethods() {
        final TypeContext<BridgeImpl> bridge = TypeContext.of(BridgeImpl.class);
        final List<MethodContext<?, BridgeImpl>> methods = bridge.methods();
        for (final MethodContext<?, BridgeImpl> method : methods) {
            Assertions.assertFalse(method.method().isBridge());
        }
    }

    @Test
    void testTypeContextBridgeMethodsCanBeObtained() {
        final TypeContext<BridgeImpl> bridge = TypeContext.of(BridgeImpl.class);
        final List<MethodContext<?, BridgeImpl>> methods = bridge.bridgeMethods();
        Assertions.assertEquals(1, methods.size());
        Assertions.assertEquals(Object.class, methods.get(0).returnType().type());
        Assertions.assertTrue(methods.get(0).method().isBridge());
    }

    @Test
    void testLookupReturnsClassIfPresent() {
        final TypeContext<?> lookup = TypeContext.lookup("org.dockbox.hartshorn.core.types.ReflectTestType");
        Assertions.assertNotNull(lookup);
        Assertions.assertEquals(ReflectTestType.class, lookup.type());
    }

    @Test
    void testLookupReturnsNullIfAbsent() {
        final TypeContext<?> lookup = TypeContext.lookup("org.dockbox.hartshorn.util.AnotherClass");
        Assertions.assertEquals(TypeContext.VOID, lookup);
    }

    @Test
    void testHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(TypeContext.of(ReflectTestType.class).method("publicMethod", List.of(TypeContext.of(String.class))).present());
    }

    @Test
    void testHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(TypeContext.of(ReflectTestType.class).method("otherMethod").present());
    }

    @Test
    void testInstanceHasMethodIsTrueIfMethodExists() {
        Assertions.assertTrue(TypeContext.of(new ReflectTestType()).method("publicMethod", List.of(TypeContext.of(String.class))).present());
    }

    @Test
    void testInstanceHasMethodIsFalseIfMethodDoesNotExist() {
        Assertions.assertFalse(TypeContext.of(new ReflectTestType()).method("otherMethod").present());
    }

    @ParameterizedTest
    @MethodSource("nonVoidTypes")
    void testVoidIsFalseIfTypeIsNotVoid(final Class<?> type) {
        Assertions.assertFalse(TypeContext.of(type).isVoid());
    }

    @Test
    void testVoidIsTrueIfTypeIsVoid() {
        Assertions.assertTrue(TypeContext.of(Void.class).isVoid());
    }

    @Test
    void testVoidIsTrueIfTypeIsVoidPrimitive() {
        Assertions.assertTrue(TypeContext.of(void.class).isVoid());
    }

    @ParameterizedTest
    @MethodSource("fields")
    void testHasFieldReturnsTrue(final String field) {
        Assertions.assertTrue(TypeContext.of(ReflectTestType.class).field(field).present());
    }

    @ParameterizedTest
    @MethodSource("fields")
    void testFieldsConsumesAllFields(final String field) {
        boolean activated = false;
        final TypeContext<ReflectTestType> type = TypeContext.of(ReflectTestType.class);
        for (final FieldContext<?> fieldContext : type.fields()) {
            if (fieldContext.name().equals(field)) activated = true;
        }
        Assertions.assertTrue(activated);
    }

    @Test
    void testSetFieldUpdatesAccessorField() throws NoSuchFieldException {
        final Field fieldRef = ReflectTestType.class.getDeclaredField("accessorField");
        final ReflectTestType instance = new ReflectTestType();
        final FieldContext<?> field = FieldContext.of(fieldRef);
        field.set(instance, "newValue");

        Assertions.assertTrue(instance.activatedSetter());
    }

    @Test
    void testSetFieldUpdatesNormalField() throws NoSuchFieldException {
        final Field fieldRef = ReflectTestType.class.getDeclaredField("publicField");
        final ReflectTestType instance = new ReflectTestType();
        final FieldContext<?> field = FieldContext.of(fieldRef);
        field.set(instance, "newValue");

        Assertions.assertEquals("newValue", instance.publicField);
    }

    @Test
    void testAnnotatedFieldsIncludesStatic() {
        final List<FieldContext<?>> fields = TypeContext.of(ReflectTestType.class).fields(Demo.class);
        Assertions.assertEquals(2, fields.size());
        int statics = 0;
        for (final FieldContext<?> field : fields) {
            if (field.isStatic()) statics++;
        }
        Assertions.assertEquals(1, statics);
    }

    @Test
    void testAnnotatedConstructors() {
        final TypeContext<ReflectTestType> type = TypeContext.of(ReflectTestType.class);
        final List<ConstructorContext<ReflectTestType>> constructors = type.constructors(Demo.class);
        Assertions.assertEquals(1, constructors.size());
    }

    @Test
    void testIsProxyIsTrueIfTypeIsProxy() {
        final Class<?> type = new ProxyFactory().createClass();
        Assertions.assertTrue(TypeContext.of(type).isProxy());
    }

    @Test
    void testIsProxyIsFalseIfTypeIsNormal() {
        Assertions.assertFalse(TypeContext.of(ReflectTestType.class).isProxy());
    }

    @ParameterizedTest
    @MethodSource("primitiveValues")
    void testStringToPrimitive(final Class<?> type, final String value, final Object expected) throws TypeConversionException {
        final byte b = 0x0;
        final Object o = TypeContext.toPrimitive(TypeContext.of(type), value);
        Assertions.assertNotNull(o);
        Assertions.assertEquals(expected, o);
    }

    @Test
    void testAddVirtualAnnotation() {
        final TypeContext<String> string = TypeContext.of(String.class);
        Assertions.assertFalse(string.annotation(Demo.class).present());

        final Demo demo = new Demo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Demo.class;
            }
        };

        AnnotatedElementModifier.of(string).add(demo);

        final Result<Demo> annotation = string.annotation(Demo.class);
        Assertions.assertTrue(annotation.present());
        Assertions.assertSame(demo, annotation.get());
    }

    @Test
    void testRemoveVirtualAnnotation() {
        final TypeContext<String> string = TypeContext.of(String.class);
        final Demo demo = new Demo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Demo.class;
            }
        };
        final AnnotatedElementModifier<Class<String>> modifier = AnnotatedElementModifier.of(string);
        modifier.add(demo);
        Assertions.assertTrue(string.annotation(Demo.class).present());

        modifier.remove(Demo.class);
        Assertions.assertFalse(string.annotation(Demo.class).present());
    }

    @Test
    void testVirtualAnnotationToMethod() {
        final MethodContext<?, ReflectTests> method = TypeContext.of(ReflectTests.class).method("testVirtualAnnotationToMethod").get();
        final AnnotatedElementModifier<Method> modifier = AnnotatedElementModifier.of(method);
        final Demo demo = new Demo() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Demo.class;
            }
        };

        modifier.add(demo);
        Assertions.assertTrue(method.annotation(Demo.class).present());

        modifier.remove(Demo.class);
        Assertions.assertFalse(method.annotation(Demo.class).present());
    }

    @Test
    void testMethodInvokerCanBeChanged() {
        final Result<MethodContext<String, ReflectTests>> helloWorld = TypeContext.of(ReflectTests.class)
                .method("helloWorld")
                .map(method -> (MethodContext<String, ReflectTests>) method);
        Assertions.assertTrue(helloWorld.present());
        final MethodContext<String, ReflectTests> method = helloWorld.get();

        final Result<?> unmodifiedResult = method.invoke(this);
        Assertions.assertTrue(unmodifiedResult.present());
        Assertions.assertEquals("Hello World", unmodifiedResult.get());

        final MethodModifier<String, ReflectTests> modifier = MethodModifier.of(method);
        modifier.invoker((methodContext, instance, args) -> Result.of("Goodbye World"));

        final Result<String> modifiedResult = method.invoke(this);
        Assertions.assertTrue(modifiedResult.present());
        Assertions.assertEquals("Goodbye World", modifiedResult.get());
    }

    @Test
    void testMethodAccessorCanBeChanged() {
        final Result<MethodContext<String, ReflectTests>> helloWorld = TypeContext.of(ReflectTests.class)
                .method("privateHelloWorld")
                .map(method -> (MethodContext<String, ReflectTests>) method);
        Assertions.assertTrue(helloWorld.present());
        final MethodContext<String, ReflectTests> method = helloWorld.get();

        final Result<String> unmodifiedResult = method.invoke(this);
        Assertions.assertTrue(unmodifiedResult.absent());
        Assertions.assertTrue(unmodifiedResult.caught());
        final Throwable error = unmodifiedResult.error();
        Assertions.assertTrue(error instanceof IllegalAccessException);

        final MethodModifier<String, ReflectTests> modifier = MethodModifier.of(method);
        modifier.access(true);

        final Result<String> modifiedResult = method.invoke(this);
        Assertions.assertTrue(modifiedResult.present());
        Assertions.assertEquals("Hello World", modifiedResult.get());
    }

    private String privateHelloWorld() {
        return "Hello World";
    }

    public String helloWorld() {
        return "Hello World";
    }

    @Test
    void testRedefinedAnnotationsTakePriority() {
        final TypeContext<AnnotatedImpl> typeContext = TypeContext.of(AnnotatedImpl.class);
        final Result<Base> annotation = typeContext.annotation(Base.class);
        Assertions.assertTrue(annotation.present());
        Assertions.assertEquals("impl", annotation.get().value());
    }

    @Test
    public void genericTypeTests() {
        final ParameterContext<?> parameter = TypeContext.of(this).method("genericTestMethod", List.class)
                .get()
                .parameters()
                .get(0);

        final TypeContext<?> first = parameter.genericType();

        Assertions.assertTrue(first.is(List.class));
        Assertions.assertEquals(1, first.typeParameters().size());

        final TypeContext<?> second = first.typeParameters().get(0);
        Assertions.assertTrue(second.is(List.class));
        Assertions.assertEquals(1, second.typeParameters().size());

        final TypeContext<?> third = second.typeParameters().get(0);
        Assertions.assertTrue(third.is(String.class));
        Assertions.assertEquals(0, third.typeParameters().size());
    }

    public void genericTestMethod(final List<List<String>> nestedGeneric) { }

    public void methodWithWildcardUpperbounds(final List<? extends String> list) { }

    @Test
    void testWildcardsWithUpperBounds() {
        final ParameterContext<?> parameter = TypeContext.of(this).method("methodWithWildcardUpperbounds", List.class)
                .get()
                .parameters()
                .get(0);

        final TypeContext<?> first = parameter.genericType();

        Assertions.assertTrue(first.is(List.class));
        Assertions.assertEquals(1, first.typeParameters().size());

        final TypeContext<?> second = first.typeParameters().get(0);
        Assertions.assertTrue(second.is(String.class));
        Assertions.assertFalse(second instanceof WildcardTypeContext);
    }
}
