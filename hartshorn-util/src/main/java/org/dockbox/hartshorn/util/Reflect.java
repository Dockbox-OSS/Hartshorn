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
import org.dockbox.hartshorn.api.entity.annotations.Entity;
import org.dockbox.hartshorn.api.entity.annotations.Property;
import org.dockbox.hartshorn.util.exceptions.EnumException;
import org.dockbox.hartshorn.util.exceptions.FieldAccessException;
import org.dockbox.hartshorn.util.exceptions.NotPrimitiveException;
import org.dockbox.hartshorn.util.exceptions.TypeConversionException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javassist.util.proxy.ProxyFactory;

@SuppressWarnings({ "unused", "OverlyComplexClass" })
public final class Reflect {

    private static final Map<String, Reflections> reflectedPrefixes = HartshornUtils.emptyConcurrentMap();
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = HartshornUtils.ofEntries(
            HartshornUtils.entry(boolean.class, Boolean.class),
            HartshornUtils.entry(byte.class, Byte.class),
            HartshornUtils.entry(char.class, Character.class),
            HartshornUtils.entry(double.class, Double.class),
            HartshornUtils.entry(float.class, Float.class),
            HartshornUtils.entry(int.class, Integer.class),
            HartshornUtils.entry(long.class, Long.class),
            HartshornUtils.entry(short.class, Short.class));

    private static final Map<?, Function<String, ?>> nativeFromStringSuppliers = HartshornUtils.ofEntries(
            HartshornUtils.entry(boolean.class, Boolean::valueOf),
            HartshornUtils.entry(byte.class, Byte::valueOf),
            HartshornUtils.entry(char.class, s -> s.charAt(0)),
            HartshornUtils.entry(double.class, Double::valueOf),
            HartshornUtils.entry(float.class, Float::valueOf),
            HartshornUtils.entry(int.class, Integer::valueOf),
            HartshornUtils.entry(long.class, Long::valueOf),
            HartshornUtils.entry(short.class, Short::valueOf)
    );

    private static final List<Class<?>> nativeSupportedTypes = HartshornUtils.asList(
            boolean.class, byte.class, short.class,
            int.class, long.class, float.class, double.class,
            byte[].class, int[].class, long[].class,
            String.class, List.class, Map.class);

    private static final String serverClassName = "org.dockbox.hartshorn.server.Server";

    private static Lookup LOOKUP;

    private Reflect() {}

    @SuppressWarnings("unchecked")
    public static <T> Exceptional<T> fieldValue(Class<?> fieldHolder, Object instance, String field, Class<T> expectedType) {
        try {
            Field declaredField = fieldHolder.getDeclaredField(field);
            declaredField.setAccessible(true);
            return (Exceptional<T>) fieldValue(declaredField, instance);
        }
        catch (ClassCastException | ReflectiveOperationException e) {
            return Exceptional.of(e);
        }
    }

    public static Exceptional<?> fieldValue(Field field, Object instance) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(Property.class)) {
            Property property = field.getAnnotation(Property.class);
            if (!property.getter().equals("")) {
                return runMethod(instance, property.getter(), field.getType());
            }
        }
        try {
            return Exceptional.of(field.get(instance));
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            return Exceptional.of(e);
        }
    }

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g.
     * protected or private). If the method does not exist, or throws a exception the caught is wrapped
     * in a {@link Exceptional}. Otherwise the (nullable) return value is returned wrapped in a {@link
     * Exceptional}.
     *
     * @param <T>
     *         The type of the expected return value
     * @param instance
     *         The instance to call the method on
     * @param method
     *         The method to call
     * @param expectedType
     *         The type of the expected return value
     * @param args
     *         The arguments which are provided to the method call
     *
     * @return The result of the method call, wrapped in {@link Exceptional}
     */
    public static <T> Exceptional<T> runMethod(Object instance, String method, Class<T> expectedType, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return Reflect.runMethod(
                instance.getClass(), instance, method, expectedType, argTypes, args);
    }

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g.
     * protected or private). If the method does not exist, or throws a exception the caught is wrapped
     * in a {@link Exceptional}. Otherwise the (nullable) return value is returned wrapped in a {@link
     * Exceptional}.
     *
     * @param <T>
     *         The type of the expected return value
     * @param methodHolder
     *         The type to call the method on
     * @param instance
     *         The instance to call the method with
     * @param method
     *         The method to call
     * @param expectedType
     *         The type of the expected return value
     * @param argumentTypes
     *         The types of the arguments, used to collect the appropriate method
     * @param args
     *         The arguments which are provided to the method call
     *
     * @return The result of the method call, wrapped in {@link Exceptional}
     */
    @SuppressWarnings("unchecked")
    public static <T> Exceptional<T> runMethod(
            Class<?> methodHolder,
            Object instance,
            String method,
            Class<T> expectedType,
            Class<?>[] argumentTypes,
            Object... args
    ) {
        try {
            Method m = methodHolder.getDeclaredMethod(method, argumentTypes);
            m.setAccessible(true);
            T value = (T) m.invoke(instance, args);
            return Exceptional.of(value);
        }
        catch (ClassCastException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            return Exceptional.of(e);
        }
    }

    @Contract("null, _ -> false; !null, null -> false")
    public static <T> boolean isGenericInstanceOf(T instance, Class<?> type) {
        return null != instance && Reflect.assignableFrom(type, instance.getClass());
    }

    /**
     * Returns true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code
     * from}.
     *
     * <p>Primitive wrappers include all JDK wrappers for native types (int, char, double, etc). E.g.
     * all of the following assignabilities return true:
     *
     * <pre>{@code
     * HartshornUtils.assignableFrom(int.class, Integer.class);
     * HartshornUtils.assignableFrom(Integer.class, int.class);
     * HartshornUtils.assignableFrom(int.class, int.class);
     * HartshornUtils.assignableFrom(Number.class, Integer.class);
     *
     * }</pre>
     *
     * @param to
     *         The possible (super) type or primite wrapper of {@code from}
     * @param from
     *         The type to compare assignability against
     *
     * @return true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code
     *         from}
     * @see Reflect#isPrimitiveWrapper(Class, Class)
     */
    public static boolean assignableFrom(Class<?> to, Class<?> from) {
        if (null == to || null == from) return false;
        //noinspection ConstantConditions
        if (to == from || to.equals(from)) return true;

        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (from.isPrimitive()) {
            return Reflect.isPrimitiveWrapper(to, from);
        }
        if (to.isPrimitive()) {
            return Reflect.isPrimitiveWrapper(from, to);
        }
        return false;
    }

    /**
     * Returns true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     *
     * @param targetClass
     *         The primitive wrapper (e.g. Integer)
     * @param primitive
     *         The primitive type (e.g. int)
     *
     * @return true if {@code targetClass} is a primitive wrapper of {@code primitive}.
     */
    public static boolean isPrimitiveWrapper(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("First argument has to be primitive type");
        }
        return primitiveWrapperMap.get(primitive) == targetClass;
    }

    @NotNull
    @Unmodifiable
    public static <A extends Annotation> Collection<Method> annotatedMethods(Class<?> clazz, Class<A> annotation) {
        return Reflect.annotatedMethods(clazz, annotation, t -> true);
    }

    /**
     * Gets all methods annotated by a given annotation, which match the given rule, inside a class.
     * If the method is not decorated with the given annotation, or does not match the given rule, it
     * will not be returned. Also collects methods from parent types of the class.
     *
     * @param <A>
     *         The annotation constraint
     * @param clazz
     *         The class to scan for methods
     * @param annotation
     *         The annotation expected to be present on one or more methods
     * @param rule
     *         The additional rule for methods to match
     *
     * @return All methods annotated by a given annotation, which match the given rule.
     */
    @NotNull
    @Unmodifiable
    public static <A extends Annotation> Collection<Method> annotatedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule) {
        return Reflect.annotatedMethods(clazz, annotation, rule, false);
    }

    /**
     * Gets all methods annotated by a given annotation, which match the given rule, inside a class.
     * If the method is not decorated with the given annotation, or does not match the given rule, it
     * will not be returned. Also collects methods from parent types of the class if {@code
     * skipParents} is false.
     *
     * @param <A>
     *         The annotation constraint
     * @param clazz
     *         The class to scan for methods
     * @param annotation
     *         The annotation expected to be present on one or more methods
     * @param rule
     *         The additional rule for methods to match
     * @param skipParents
     *         Whether or not to include methods in parent types
     *
     * @return All methods annotated by a given annotation, which match the given rule.
     */
    @NotNull
    @Unmodifiable
    public static <A extends Annotation> Collection<Method> annotatedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule, boolean skipParents) {
        List<Method> annotatedMethods = HartshornUtils.emptyList();
        for (Method method : HartshornUtils.asList(skipParents ? clazz.getMethods() : clazz.getDeclaredMethods())) {
            // TODO: Confirm relevance
            // if (!method.isAccessible()) method.setAccessible(true);
            if (method.isAnnotationPresent(annotation) && rule.test(method.getAnnotation(annotation))) {
                annotatedMethods.add(method);
            }
        }
        return HartshornUtils.asUnmodifiableList(annotatedMethods);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotations. The prefix is
     * typically a package. If the annotation is present on a parent of the type, the highest level
     * member will be included.
     *
     * @param <A>
     *         The annotation constraint
     * @param prefix
     *         The package prefix
     * @param annotation
     *         The annotation expected to be present on one or more types
     *
     * @return The annotated types
     */
    public static <A extends Annotation> Collection<Class<?>> annotatedTypes(String prefix, Class<A> annotation) {
        return Reflect.annotatedTypes(prefix, annotation, false);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotations. The prefix is
     * typically a package. If the annotation is present on a parent of the type, it will only be
     * included if {@code skipParents} is false.
     *
     * @param <A>
     *         The annotation constraint
     * @param prefix
     *         The package prefix
     * @param annotation
     *         The annotation expected to be present on one or more types
     * @param skipParents
     *         Whether or not to include the type if supertypes are annotated
     *
     * @return The annotated types
     */
    public static <A extends Annotation> Collection<Class<?>> annotatedTypes(String prefix, Class<A> annotation, boolean skipParents) {
        Reflections reflections = reflectionsFromPrefix(prefix);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation, !skipParents);
        return HartshornUtils.asList(types);
    }

    private static Reflections reflectionsFromPrefix(String prefix) {
        if (!reflectedPrefixes.containsKey(prefix)) {
            reflectedPrefixes.put(prefix, new Reflections(prefix));
        }
        return reflectedPrefixes.get(prefix);
    }

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist
     * for the given type, and empty list is returned.
     *
     * @param prefix
     *         The package prefix
     * @param parent
     *         The parent type to scan for subclasses
     * @param <T>
     *         The type of the parent
     *
     * @return The list of sub-types, or a empty list
     */
    public static <T> Collection<Class<? extends T>> subTypes(String prefix, Class<T> parent) {
        Reflections reflections = reflectionsFromPrefix(prefix);
        Set<Class<? extends T>> subTypes = reflections.getSubTypesOf(parent);
        return HartshornUtils.asList(subTypes);
    }

    /**
     * Is either assignable from boolean.
     *
     * @param to
     *         the to
     * @param from
     *         the from
     *
     * @return the boolean
     */
    public static boolean eitherAssignsFrom(Class<?> to, Class<?> from) {
        return Reflect.assignableFrom(from, to) || Reflect.assignableFrom(to, from);
    }

    /**
     * Gets static fields.
     *
     * @param type
     *         the type
     *
     * @return the static fields
     */
    public static Collection<Field> staticFields(Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        Collection<Field> staticFields = HartshornUtils.emptyList();
        for (Field field : declaredFields) {
            if (Modifier.isStatic(field.getModifiers())) {
                staticFields.add(field);
            }
        }
        return staticFields;
    }

    /**
     * Gets enum values.
     *
     * @param type
     *         the type
     *
     * @return the enum values
     */
    public static Collection<? extends Enum<?>> enumValues(Class<?> type) {
        if (!type.isEnum()) return HartshornUtils.emptyList();
        Collection<Enum<?>> constants = HartshornUtils.emptyList();
        try {
            Field f = type.getDeclaredField("$VALUES");
            f.setAccessible(true);
            Object o = f.get(null);
            Enum<?>[] e = (Enum<?>[]) o;
            constants.addAll(Arrays.asList(e));
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassCastException e) {
            throw new EnumException("Cannot access enum " + type.getCanonicalName(), e);
        }
        return constants;
    }

    /**
     * Is annotation present recursively boolean.
     *
     * @param <T>
     *         the type parameter
     * @param method
     *         the method
     * @param annotationClass
     *         the annotation class
     *
     * @return the boolean
     * @throws SecurityException
     *         the security exception
     */
    public static <T extends Annotation> boolean hasAnnotation(Method method, Class<T> annotationClass) throws SecurityException {
        return null != Reflect.annotation(method, annotationClass);
    }

    /**
     * Gets annotation recursively.
     *
     * @param <T>
     *         the type parameter
     * @param method
     *         the method
     * @param annotationClass
     *         the annotation class
     *
     * @return the annotation recursively
     * @throws SecurityException
     *         the security exception
     */
    public static <T extends Annotation> T annotation(Method method, Class<T> annotationClass) throws SecurityException {
        T result;
        if (null == (result = method.getAnnotation(annotationClass))) {
            final String name = method.getName();
            final Class<?>[] params = method.getParameterTypes();

            Class<?> declaringClass = method.getDeclaringClass();
            for (Class<?> supertype : Reflect.superTypes(declaringClass)) {
                try {
                    Method m = supertype.getDeclaredMethod(name, params);

                    // Static method doesn't override
                    if (Modifier.isStatic(m.getModifiers())) break;

                    if (null != (result = m.getAnnotation(annotationClass))) break;
                }
                catch (NoSuchMethodException ignored) {
                    // Current class doesn't have Reflect method
                }
            }
        }
        return result;
    }

    /**
     * Gets supertypes.
     *
     * @param current
     *         the current
     *
     * @return the supertypes
     */
    public static Collection<Class<?>> superTypes(Class<?> current) {
        Set<Class<?>> supertypes = HartshornUtils.emptySet();
        Set<Class<?>> next = HartshornUtils.emptySet();
        Class<?> superclass = current.getSuperclass();

        if (Object.class != superclass && null != superclass) {
            supertypes.add(superclass);
            next.add(superclass);
        }

        for (Class<?> interfaceClass : current.getInterfaces()) {
            supertypes.add(interfaceClass);
            next.add(interfaceClass);
        }

        for (Class<?> cls : next) {
            supertypes.addAll(Reflect.superTypes(cls));
        }

        return supertypes;
    }

    /**
     * Gets methods recursively.
     *
     * @param cls
     *         the cls
     *
     * @return the methods recursively
     * @throws SecurityException
     *         the security exception
     */
    public static List<Method> methods(Class<?> cls) throws SecurityException {
        try {
            Set<InternalMethodWrapper> set = HartshornUtils.emptySet();
            Class<?> current = cls;
            do {
                Method[] methods = current.getDeclaredMethods();
                for (Method m : methods) {
                    // if there's already a method that is overriding the current method, add() will return
                    // false
                    set.add(new InternalMethodWrapper(m));
                }
            }
            while (Object.class != (current = current.getSuperclass()) && null != current);

            // Guava equivalent:       Lists.transform(set, w -> w.method);
            // Stream API equivalent:  set.stream().map(w -> w.method).toList();
            List<Method> result = HartshornUtils.emptyList();
            for (InternalMethodWrapper methodWrapper : set) result.add(methodWrapper.method);
            return result;
        }
        catch (Throwable e) {
            return HartshornUtils.emptyList();
        }
    }

    public static boolean serverAvailable() {
        return lookup(serverClassName) != null;
    }

    public static Class<?> lookup(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets field property name.
     *
     * @param field
     *         the field
     *
     * @return the field property name
     */
    public static String fieldName(Field field) {
        if (field.isAnnotationPresent(Property.class)) {
            String name = field.getAnnotation(Property.class).value();
            if (!"".equals(name)) return name;
        }
        return field.getName();
    }

    private static <T> boolean canUseSetter(Class<T> type, T instance, Field field, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (field.isAnnotationPresent(Property.class)) {
            Property property = field.getAnnotation(Property.class);

            if (!property.setter().isEmpty() && Reflect.hasMethod(type, property.setter())) {
                Class<?> parameterType = field.getType();
                if (Reflect.isNotVoid(property.accepts())) parameterType = property.accepts();

                Method method = type.getMethod(property.setter(), parameterType);
                method.invoke(instance, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if a type has a declared method of which the name equals the value of {@code
     * method} and has no parameters.
     *
     * @param type
     *         The type
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type has a declared method which matches {@code method}
     */
    public static boolean hasMethod(Class<?> type, @NonNls String method) {
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(method)) return true;
        }
        return false;
    }

    /**
     * Is not void boolean.
     *
     * @param type
     *         the type
     *
     * @return the boolean
     */
    public static boolean isNotVoid(Class<?> type) {
        return !(type.equals(Void.class) || type == Void.TYPE);
    }

    /**
     * Returns true if a instance has a declared method of which the name equals the value of {@code
     * method} and has no parameters.
     *
     * @param instance
     *         The instance
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type of the instance has a declared method which matches {@code method}
     */
    public static boolean hasMethod(Object instance, String method) {
        return Reflect.hasMethod(instance.getClass(), method);
    }

    public static boolean hasField(Class<?> type, String field) {
        Class<?> original = type;
        while (null != type) {
            try {
                type.getDeclaredField(field);
                return true;
            }
            catch (ReflectiveOperationException e) {
                type = type.getSuperclass();
            }
        }
        return false;
    }

    public static boolean serializable(AnnotatedElement holder, Class<?> potentialReject, boolean throwIfRejected) {
        if (holder.isAnnotationPresent(Entity.class)) {
            Entity rejects = holder.getAnnotation(Entity.class);
            if (!rejects.serializable()) {
                if (throwIfRejected) {
                    String name = "Generic annotated element";
                    if (holder instanceof Class) {
                        name = ((Class<?>) holder).getSimpleName();
                    }
                    throw new RuntimeException(name + " cannot be serialized");
                }
                else return false;
            }
        }
        return true;
    }

    public static void fields(Class<?> type, BiConsumer<Class<?>, Field> consumer) {
        for (Field declaredField : type.getDeclaredFields()) {
            consumer.accept(type, declaredField);
        }
        if (null != type.getSuperclass()) Reflect.fields(type.getSuperclass(), consumer);
    }

    public static void set(Field field, Object to, Object value) {
        try {
            if (field.isAnnotationPresent(Property.class)) {
                Property property = field.getAnnotation(Property.class);
                if (!"".equals(property.setter())) {
                    Method setter = to.getClass().getDeclaredMethod(property.setter(), value.getClass());
                    setter.setAccessible(true);
                    setter.invoke(to, value);
                    return;
                }
            }
            field.setAccessible(true);
            field.set(to, value);
        }
        catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new FieldAccessException("Cannot access field " + field.getName(), e);
        }
    }

    public static Collection<Field> annotatedFields(Class<?> type, Class<? extends Annotation> annotation) {
        Collection<Field> fields = new ArrayList<>();
        for (Field declaredField : type.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(annotation)) fields.add(declaredField);
        }
        if (type.getSuperclass() != null) fields.addAll(annotatedFields(type.getSuperclass(), annotation));
        return fields;
    }

    public static <T> Collection<Constructor<T>> annotatedConstructors(Class<T> type, Class<? extends Annotation> annotation) {
        Collection<Constructor<T>> constructors = HartshornUtils.emptyList();
        //noinspection unchecked
        for (Constructor<T> constructor : (Constructor<T>[]) type.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(annotation)) constructors.add(constructor);
        }
        return constructors;
    }

    public static boolean isNative(Class<?> type) {
        boolean supportedType = false;
        for (Class<?> nbtSupportedType : nativeSupportedTypes) {
            if (assignableFrom(nbtSupportedType, type)) {
                supportedType = true;
                break;
            }
        }
        return supportedType;
    }

    public static Class<?> getServerClass() {
        return lookup(serverClassName);
    }

    @SuppressWarnings("unchecked")
    public static <T> Exceptional<T> runMethod(
            Class<?> methodHolder,
            Object instance,
            String method,
            Class<T> expectedType) {
        try {
            Method m = methodHolder.getDeclaredMethod(method);
            m.setAccessible(true);
            T value = (T) m.invoke(instance);
            return Exceptional.of(value);
        }
        catch (ClassCastException
                | NoSuchMethodException
                | InvocationTargetException
                | IllegalAccessException e) {
            return Exceptional.of(e);
        }
    }

    public static boolean isProxy(Object o) {
        return o != null && (ProxyFactory.isProxyClass(o.getClass()) || Proxy.isProxyClass(o.getClass()));
    }

    public static Lookup getTrustedLookup(Class<?> in) {
        if (Reflect.LOOKUP == null) {
            try {
                final Lookup original = MethodHandles.lookup();
                final Field internal = Lookup.class.getDeclaredField("IMPL_LOOKUP");
                internal.setAccessible(true);
                Reflect.LOOKUP = (Lookup) internal.get(original);
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Could not access trusted lookup", e);
            }
        }
        return Reflect.LOOKUP.in(in);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T primitiveFromString(Class<?> type, String value) throws TypeConversionException {
        try {
            if (type.isEnum()) {
                return (T) Enum.valueOf((Class<? extends Enum>) type, String.valueOf(value).toUpperCase());
            }
            else {
                if (!type.isPrimitive()) {
                    for (Entry<Class<?>, Class<?>> entry : primitiveWrapperMap.entrySet()) {
                        if (isPrimitiveWrapper(type, entry.getKey())) type = entry.getKey();
                    }
                }
                if (!type.isPrimitive()) throw new NotPrimitiveException(type);
                else {
                    Function<String, ?> converter = nativeFromStringSuppliers.get(type);
                    return (T) converter.apply(value);
                }
            }
        }
        catch (Throwable t) {
            throw new TypeConversionException(type, value);
        }
    }

    public static List<Field> fieldsWithSuper(Class<?> type, Class<?> superType) {
        Set<Field> fields = HartshornUtils.emptySet();
        for (Field staticField : staticFields(type)) {
            if (Reflect.assignableFrom(superType, staticField.getType())) fields.add(staticField);
        }
        fields(type, ($, field) -> {
            if (Reflect.assignableFrom(superType, field.getType())) fields.add(field);
        });
        return HartshornUtils.asUnmodifiableList(fields);
    }

    public static List<Annotation> annotatedAnnotations(Class<?> type, Class<? extends Annotation> annotation) {
        List<Annotation> annotations = HartshornUtils.emptyList();
        for (Annotation typeAnnotation : type.getAnnotations()) {
            if (typeAnnotation.annotationType().isAnnotationPresent(annotation)) {
                annotations.add(typeAnnotation);
            }
        }
        return annotations;
    }

    public static boolean isAbstract(Class<?> type) {
        return type.isInterface() || Modifier.isAbstract(type.getModifiers());
    }

    public static Exceptional<Type> genericTypeParameter(Class<?> type, int index) {
        return Exceptional.of(() -> {
            Type superClass = type.getGenericSuperclass();
            if (superClass instanceof Class<?>) {
                return null;
            }
            return ((ParameterizedType) superClass).getActualTypeArguments()[index];
        });
    }
}
