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

package org.dockbox.selene.core.util;

import org.dockbox.selene.core.annotations.Rejects;
import org.dockbox.selene.core.annotations.entity.Alias;
import org.dockbox.selene.core.annotations.entity.Ignore;
import org.dockbox.selene.core.annotations.entity.Property;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.annotations.extension.OwnedBy;
import org.dockbox.selene.core.exceptions.TypeRejectedException;
import org.dockbox.selene.core.extension.ExtensionManager;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.server.IntegratedExtension;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils.Provision;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public class ReflectionUtil {

    private static final Map<Class<?>, Class<?>> primitiveWrapperMap =
            SeleneUtils.COLLECTION.ofEntries(SeleneUtils.COLLECTION.entry(boolean.class, Boolean.class),
                    SeleneUtils.COLLECTION.entry(byte.class, Byte.class),
                    SeleneUtils.COLLECTION.entry(char.class, Character.class),
                    SeleneUtils.COLLECTION.entry(double.class, Double.class),
                    SeleneUtils.COLLECTION.entry(float.class, Float.class),
                    SeleneUtils.COLLECTION.entry(int.class, Integer.class),
                    SeleneUtils.COLLECTION.entry(long.class, Long.class),
                    SeleneUtils.COLLECTION.entry(short.class, Short.class));

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g. protected or private).
     * If the method does not exist, or throws a exception the error is wrapped in a {@link Exceptional}. Otherwise the
     * (nullable) return value is returned wrapped in a {@link Exceptional}.
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
    public <T> Exceptional<T> getMethodValue(Class<?> methodHolder, Object instance, String method, Class<T> expectedType, Class<?>[] argumentTypes, Object... args) {
        try {
            Method m = methodHolder.getDeclaredMethod(method, argumentTypes);
            if (!m.isAccessible()) m.setAccessible(true);
            T value = (T) m.invoke(instance, args);
            return Exceptional.ofNullable(value);
        } catch (ClassCastException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return Exceptional.of(e);
        }
    }

    @Contract("null, _ -> false; !null, null -> false")
    public <T> boolean isGenericInstanceOf(T instance, Class<?> type) {
        return null != instance && null != type && this.isAssignableFrom(type, instance.getClass());
    }

    /**
     * Returns true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code from}.
     *
     * <p>
     * Primitive wrappers include all JDK wrappers for native types (int, char, double, etc). E.g. all of the
     * following assignabilities return true:
     * <pre>{@code
     *          SeleneUtils.isAssignableFrom(int.class, Integer.class);
     *          SeleneUtils.isAssignableFrom(Integer.class, int.class);
     *          SeleneUtils.isAssignableFrom(int.class, int.class);
     *          SeleneUtils.isAssignableFrom(Number.class, Integer.class);
     *     }</pre>
     *
     * @param to
     *         The possible (super) type or primite wrapper of {@code from}
     * @param from
     *         The type to compare assignability against
     *
     * @return true if {@code to} is equal to-, a super type of, or a primitive wrapper of {@code from}
     * @see ReflectionUtil#isPrimitiveWrapperOf(Class, Class)
     */
    public boolean isAssignableFrom(Class<?> to, Class<?> from) {
        if (to.isAssignableFrom(from)) {
            return true;
        }
        if (from.isPrimitive()) {
            return this.isPrimitiveWrapperOf(to, from);
        }
        if (to.isPrimitive()) {
            return this.isPrimitiveWrapperOf(from, to);
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
    public boolean isPrimitiveWrapperOf(Class<?> targetClass, Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            throw new IllegalArgumentException("First argument has to be primitive type");
        }
        return primitiveWrapperMap.get(primitive) == targetClass;
    }

    /**
     * Gets all methods annotated by a given annotation, which match the given rule, inside a class. If the method is
     * not annotated with the given annotation, or does not match the given rule, it will not be returned. Also collects
     * methods from parent types of the class.
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
    public <A extends Annotation> Collection<Method> getAnnotedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule) {
        return this.getAnnotedMethods(clazz, annotation, rule, false);
    }

    /**
     * Gets all methods annotated by a given annotation, which match the given rule, inside a class. If the method is
     * not annotated with the given annotation, or does not match the given rule, it will not be returned. Also collects
     * methods from parent types of the class if {@code skipParents} is false.
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
    public <A extends Annotation> Collection<Method> getAnnotedMethods(Class<?> clazz, Class<A> annotation, Predicate<A> rule, boolean skipParents) {
        List<Method> annotatedMethods = SeleneUtils.COLLECTION.emptyList();
        for (Method method : SeleneUtils.COLLECTION.asList(skipParents ? clazz.getMethods() : clazz.getDeclaredMethods())) {
            if (!method.isAccessible()) method.setAccessible(true);
            if (method.isAnnotationPresent(annotation) && rule.test(method.getAnnotation(annotation))) {
                annotatedMethods.add(method);
            }
        }
        return SeleneUtils.COLLECTION.asUnmodifiableList(annotatedMethods);
    }

    /**
     * Gets types annotated with a given annotation, both classes and annotations. The prefix is typically a package.
     * If the annotation is present on a parent of the type, the highest level member will be included.
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
    public <A extends Annotation> Collection<Class<?>> getAnnotatedTypes(String prefix, Class<A> annotation) {
        return this.getAnnotatedTypes(prefix, annotation, false);
    }

    /**
     * Gets types annotated with a given annotation, both classes and annotations. The prefix is typically a package.
     * If the annotation is present on a parent of the type, it will only be included if {@code skipParents} is false.
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
    public <A extends Annotation> Collection<Class<?>> getAnnotatedTypes(String prefix, Class<A> annotation, boolean skipParents) {
        Reflections reflections = new Reflections(prefix);
        Set<Class<?>> types = reflections.getTypesAnnotatedWith(annotation, !skipParents);
        return SeleneUtils.COLLECTION.asList(types);
    }

    /**
     * Is not void boolean.
     *
     * @param type
     *         the type
     *
     * @return the boolean
     */
    public boolean isNotVoid(Class<?> type) {
        return !(type.equals(Void.class) || type == Void.TYPE);
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
    public boolean isEitherAssignableFrom(Class<?> to, Class<?> from) {
        return this.isAssignableFrom(from, to) || this.isAssignableFrom(to, from);
    }

    /**
     * Gets static fields.
     *
     * @param type
     *         the type
     *
     * @return the static fields
     */
    public Collection<Field> getStaticFields(Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        Collection<Field> staticFields = SeleneUtils.COLLECTION.emptyList();
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
    public Collection<? extends Enum<?>> getEnumValues(Class<?> type) {
        if (!type.isEnum()) return SeleneUtils.COLLECTION.emptyList();
        Collection<Enum<?>> constants = SeleneUtils.COLLECTION.emptyList();
        try {
            Field f = type.getDeclaredField("$VALUES");
            if (!f.isAccessible()) f.setAccessible(true);
            Object o = f.get(null);
            Enum<?>[] e = (Enum<?>[]) o;
            constants.addAll(Arrays.asList(e));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException | ClassCastException e) {
            Selene.log().warn("Error obtaining enum constants in " + type.getCanonicalName(), e);
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
    public <T extends Annotation> boolean isAnnotationPresentRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        return null != this.getAnnotationRecursively(method, annotationClass);
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
    public <T extends Annotation> T getAnnotationRecursively(Method method, Class<T> annotationClass)
            throws SecurityException {
        T result;
        if (null == (result = method.getAnnotation(annotationClass))) {
            final String name = method.getName();
            final Class<?>[] params = method.getParameterTypes();

            Class<?> declaringClass = method.getDeclaringClass();
            for (Class<?> supertype : this.getSupertypes(declaringClass)) {
                try {
                    Method m = supertype.getDeclaredMethod(name, params);

                    // Static method doesn't override
                    if (Modifier.isStatic(m.getModifiers())) break;

                    if (null != (result = m.getAnnotation(annotationClass))) break;
                } catch (NoSuchMethodException ignored) {
                    // Current class doesn't have this method
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
    public Collection<Class<?>> getSupertypes(Class<?> current) {
        Set<Class<?>> supertypes = SeleneUtils.COLLECTION.emptySet();
        Set<Class<?>> next = SeleneUtils.COLLECTION.emptySet();
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
            supertypes.addAll(this.getSupertypes(cls));
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
    public List<Method> getMethodsRecursively(Class<?> cls) throws SecurityException {
        try {
            Set<InternalMethodWrapper> set = SeleneUtils.COLLECTION.emptySet();
            Class<?> current = cls;
            do {
                Method[] methods = current.getDeclaredMethods();
                for (Method m : methods) {
                    // if there's already a method that is overriding the current method, add() will return false
                    set.add(new InternalMethodWrapper(m));
                }
            } while (Object.class != (current = current.getSuperclass()) && null != current);

            // Guava equivalent:       Lists.transform(set, w -> w.method);
            // Stream API equivalent:  set.stream().map(w -> w.method).collect(Collectors.toList());
            List<Method> result = SeleneUtils.COLLECTION.emptyList();
            for (InternalMethodWrapper methodWrapper : set) result.add(methodWrapper.method);
            return result;
        } catch (Throwable e) {
            return SeleneUtils.COLLECTION.emptyList();
        }
    }

    /**
     * Gets extension.
     *
     * @param type
     *         the type
     *
     * @return the extension
     */
    @Nullable
    public Extension getExtension(Class<?> type) {
        if (null == type) return null;
        if (type.equals(Selene.class)) return this.getExtension(SeleneUtils.INJECT.getInstance(IntegratedExtension.class).getClass());

        if (type.isAnnotationPresent(OwnedBy.class)) {
            OwnedBy owner = type.getAnnotation(OwnedBy.class);
            return this.getExtension(owner.value());
        }

        Extension extension = type.getAnnotation(Extension.class);
        extension = null != extension ? extension : this.getExtension(type.getSuperclass());
        if (null == extension)
            extension = SeleneUtils.INJECT.getInstanceSafe(ExtensionManager.class).map(em -> em.getHeader(type).orNull()).orNull();
        return extension;
    }

    /**
     * Run with extension t.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param function
     *         the function
     *
     * @return the t
     */
    @Nullable
    public <T> T runWithExtension(Class<?> type, Function<Extension, T> function) {
        Extension extension = this.getExtension(type);
        if (null != extension) return function.apply(extension);
        return null;
    }

    /**
     * Run with extension.
     *
     * @param type
     *         the type
     * @param consumer
     *         the consumer
     */
    public void runWithExtension(Class<?> type, Consumer<Extension> consumer) {
        Extension extension = this.getExtension(type);
        if (null != extension) consumer.accept(extension);
    }

    /**
     * Run with instance.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param consumer
     *         the consumer
     */
    public <T> void runWithInstance(Class<T> type, Consumer<T> consumer) {
        T instance = SeleneUtils.INJECT.getInstance(type);
        if (null != instance) consumer.accept(instance);
    }

    /**
     * Gets field property name.
     *
     * @param field
     *         the field
     *
     * @return the field property name
     */
    public String getFieldPropertyName(Field field) {
        return field.isAnnotationPresent(Property.class)
                ? field.getAnnotation(Property.class).value()
                : field.getName();
    }

    @Nullable
    public String getClassAlias(Class<?> type) {
        String className = null;
        if (type.isAnnotationPresent(Alias.class))
            className = type.getAnnotation(Alias.class).value();
        return className;
    }

    /**
     * Try create from map exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param map
     *         the map
     *
     * @return the exceptional
     */
    public <T> Exceptional<T> tryCreateFromMap(Class<T> type, Map<String, Object> map) {
        return this.tryCreateFromProcessed(type, key -> map.getOrDefault(key, null), true);
    }

    /**
     * Try create from processed exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     *
     * @return the exceptional
     */
    public <T> Exceptional<T> tryCreateFromProcessed(Class<T> type, Function<String, Object> valueCollector, boolean inject) {
        return this.tryCreate(type, valueCollector, inject, Provision.FIELD_NAME);
    }

    /**
     * Try create from raw exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     *
     * @return the exceptional
     */
    public <T> Exceptional<T> tryCreateFromRaw(Class<T> type, Function<Field, Object> valueCollector, boolean inject) {
        return this.tryCreate(type, valueCollector, inject, Provision.FIELD);
    }

    /**
     * Try create exceptional.
     *
     * @param <T>
     *         the type parameter
     * @param <A>
     *         the type parameter
     * @param type
     *         the type
     * @param valueCollector
     *         the value collector
     * @param inject
     *         the inject
     * @param provision
     *         the provision
     *
     * @return the exceptional
     */
    @SuppressWarnings("unchecked")
    public <T, A> Exceptional<T> tryCreate(Class<T> type, Function<A, Object> valueCollector, boolean inject, Provision provision) {
        T instance = inject ? SeleneUtils.INJECT.getInstance(type) : this.getInstance(type);
        if (null != instance)
            try {
                for (Field field : type.getDeclaredFields()) {
                    if (!field.isAccessible()) field.setAccessible(true);
                    if (field.isAnnotationPresent(Ignore.class)) continue;
                    Object value;
                    if (Provision.FIELD == provision) {
                        value = valueCollector.apply((A) field);
                    } else {
                        String fieldName = this.getFieldPropertyName(field);
                        value = valueCollector.apply((A) fieldName);
                    }
                    if (null == value) continue;

                    boolean useFieldDirect = true;
                    if (field.isAnnotationPresent(Property.class)) {
                        Property property = field.getAnnotation(Property.class);

                        //noinspection CallToSuspiciousStringMethod
                        if (!"".equals(property.setter()) && this.hasMethod(type, property.setter())) {
                            Class<?> parameterType = field.getType();
                            if (this.isNotVoid(property.accepts())) parameterType = property.accepts();

                            Method method = type.getMethod(property.setter(), parameterType);
                            method.invoke(instance, value);
                            useFieldDirect = false;
                        }
                    }

                    if (useFieldDirect && this.isAssignableFrom(field.getType(), value.getClass()))
                        field.set(instance, value);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassCastException e) {
                return Exceptional.of(e);
            }
        return Exceptional.ofNullable(instance);
    }

    /**
     * Gets instance.
     *
     * @param <T>
     *         the type parameter
     * @param clazz
     *         the clazz
     *
     * @return the instance
     */
    public <T> T getInstance(Class<T> clazz) {
        try {
            Constructor<T> ctor = clazz.getConstructor();
            return ctor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return SeleneUtils.INJECT.getInstance(clazz);
        }
    }

    /**
     * Returns true if a instance has a declared method of which the name equals the value of {@code method} and has no
     * parameters.
     *
     * @param instance
     *         The instance
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type of the instance has a declared method which matches {@code method}
     */
    public boolean hasMethod(Object instance, String method) {
        return this.hasMethod(instance.getClass(), method);
    }

    /**
     * Returns true if a type has a declared method of which the name equals the value of {@code method} and has no
     * parameters.
     *
     * @param type
     *         The type
     * @param method
     *         The name of the method to check for
     *
     * @return true if the type has a declared method which matches {@code method}
     */
    public boolean hasMethod(Class<?> type, @NonNls String method) {
        for (Method m : type.getDeclaredMethods()) {
            if (m.getName().equals(method)) return true;
        }
        return false;
    }

    /**
     * Attempts to get the return value of a method which may not be publicly accessible (e.g. protected or private).
     * If the method does not exist, or throws a exception the error is wrapped in a {@link Exceptional}. Otherwise the
     * (nullable) return value is returned wrapped in a {@link Exceptional}.
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
    public <T> Exceptional<T> getMethodValue(Object instance, String method, Class<T> expectedType, Object... args) {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        return this.getMethodValue(instance.getClass(), instance, method, expectedType, argTypes, args);
    }

    public boolean hasFieldRecursive(Class<?> type, String field) {
        Class<?> original = type;
        while (null != type) {
            try {
                if (field.contains("*") && !field.contains("?")) {
                    type.getDeclaredField(field);
                    return true;
                }
            } catch (ReflectiveOperationException e) {
                type = type.getSuperclass();
            }
        }
        return false;
    }

    public boolean rejects(Class<?> holder, Class<?> potentialReject) {
        return this.rejects(holder, potentialReject, false);
    }

    public boolean rejects(Class<?> holder, Class<?> potentialReject, boolean throwIfRejected) {
        if (holder.isAnnotationPresent(Rejects.class)) {
            Rejects rejects = holder.getAnnotation(Rejects.class);
            boolean rejected = false;
            for (Class<?> rejectedType : rejects.value())
                if (potentialReject.isAssignableFrom(rejectedType)) rejected = true;
            if (rejected && throwIfRejected) throw new TypeRejectedException(potentialReject, holder);
            return rejected;
        }
        return false;
    }

    public void forEachFieldIn(Class<?> type, BiConsumer<Class<?>, Field> consumer) {
        for (Field declaredField : type.getDeclaredFields()) {
            consumer.accept(type, declaredField);
        }
        if (null != type.getSuperclass())
            this.forEachFieldIn(type.getSuperclass(), consumer);
    }


}
