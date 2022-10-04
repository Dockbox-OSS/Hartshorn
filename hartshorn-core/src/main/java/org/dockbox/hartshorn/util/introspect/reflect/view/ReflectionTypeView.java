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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.Tuple;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectionTypeView<T> extends ReflectionAnnotatedElementView implements ReflectionModifierCarrierView, TypeView<T> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = CollectionUtilities.ofEntries(
            Tuple.of(boolean.class, Boolean.class),
            Tuple.of(byte.class, Byte.class),
            Tuple.of(char.class, Character.class),
            Tuple.of(double.class, Double.class),
            Tuple.of(float.class, Float.class),
            Tuple.of(int.class, Integer.class),
            Tuple.of(long.class, Long.class),
            Tuple.of(short.class, Short.class)
    );
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = CollectionUtilities.ofEntries(
            Tuple.of(boolean.class, false),
            Tuple.of(byte.class, 0),
            Tuple.of(char.class, '\u0000'),
            Tuple.of(double.class, 0.0d),
            Tuple.of(float.class, 0.0f),
            Tuple.of(int.class, 0),
            Tuple.of(long.class, 0L),
            Tuple.of(short.class, 0)
    );
    private static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVE = CollectionUtilities.ofEntries(
            Tuple.of(Boolean.class, boolean.class),
            Tuple.of(Byte.class, byte.class),
            Tuple.of(Character.class, char.class),
            Tuple.of(Double.class, double.class),
            Tuple.of(Float.class, float.class),
            Tuple.of(Integer.class, int.class),
            Tuple.of(Long.class, long.class),
            Tuple.of(Short.class, short.class)
    );

    private final Introspector introspector;
    private final Class<T> type;
    private final ParameterizedType parameterizedType;

    private List<T> enumConstants;
    private TypeView<?> parent;
    private List<TypeView<?>> interfaces;
    private Result<TypeView<?>> elementType;
    private Tristate isProxy = Tristate.UNDEFINED;

    private TypeMethodsIntrospector<T> methodsIntrospector;
    private TypeFieldsIntrospector<T> fieldsIntrospector;
    private TypeConstructorsIntrospector<T> constructorsIntrospector;
    private TypeParametersIntrospector typeParametersIntrospector;

    public ReflectionTypeView(final Introspector introspector, final Class<T> type) {
        this(introspector, type, null);
    }

    public ReflectionTypeView(final Introspector introspector, final ParameterizedType parameterizedType) {
        this(introspector, (Class<T>) parameterizedType.getRawType(), parameterizedType);
    }

    private ReflectionTypeView(final Introspector introspector, final Class<T> type, final ParameterizedType parameterizedType) {
        super(introspector);
        this.introspector = introspector;
        this.type = type;
        this.parameterizedType = parameterizedType;
    }

    private void checkProxy() {
        if (this.isProxy()) ExceptionHandler.unchecked(new ApplicationException("Cannot collect metadata of proxied type '%s'".formatted(this.qualifiedName())));
    }

    @Override
    protected AnnotatedElement annotatedElement() {
        return this.type;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public boolean isVoid() {
        return Void.TYPE.equals(this.type) || Void.class.equals(this.type);
    }

    @Override
    public boolean isAnonymous() {
        return this.type.isAnonymousClass();
    }

    @Override
    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    @Override
    public boolean isEnum() {
        return this.type.isEnum();
    }

    @Override
    public boolean isAnnotation() {
        return this.type.isAnnotation();
    }

    @Override
    public boolean isInterface() {
        return this.type.isInterface();
    }

    @Override
    public boolean isAbstract() {
        return this.isInterface() || Modifier.isAbstract(this.type.getModifiers());
    }

    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.type.getModifiers());
    }

    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.type.getModifiers());
    }

    @Override
    public boolean isArray() {
        return this.type.isArray();
    }

    @Override
    public boolean isProxy() {
        if (this.isProxy == Tristate.UNDEFINED) {
            final boolean proxy = this.introspector.applicationContext().environment().isProxy(this.type);
            this.isProxy = Tristate.valueOf(proxy);
        }
        return this.isProxy.booleanValue();
    }

    @Override
    public boolean isWildcard() {
        return false;
    }

    @Override
    public boolean isDeclaredIn(final String prefix) {
        return this.type.getPackageName().startsWith(prefix);
    }

    @Override
    public boolean isInstance(final Object object) {
        if (object == null) return false;
        if (this.type.isInstance(object)) return true;

        if (this.isPrimitiveWrapper(object.getClass(), this.type)) return true;
        return this.isPrimitiveWrapper(this.type, object.getClass());
    }

    @Override
    public List<TypeView<?>> interfaces() {
        if (this.interfaces == null) {
            this.checkProxy();
            this.interfaces = Arrays.stream(this.type().getInterfaces())
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.interfaces;
    }

    @Override
    public TypeView<?> superClass() {
        if (this.parent == null) {
            this.checkProxy();
            final Class<? super T> parent = this.type().getSuperclass();
            this.parent = this.introspector.introspect((Class<?>) Objects.requireNonNullElse(parent, Void.class));
        }
        return this.parent;
    }

    @Override
    public TypeMethodsIntrospector<T> methods() {
        if (this.methodsIntrospector == null) {
            this.methodsIntrospector = new ReflectionTypeMethodsIntrospector<>(this.introspector, this);
        }
        return this.methodsIntrospector;
    }

    @Override
    public TypeFieldsIntrospector<T> fields() {
        if (this.fieldsIntrospector == null) {
            this.fieldsIntrospector = new ReflectionTypeFieldsIntrospector<>(this.introspector, this);
        }
        return this.fieldsIntrospector;
    }

    @Override
    public TypeConstructorsIntrospector<T> constructors() {
        if (this.constructorsIntrospector == null) {
            this.constructorsIntrospector = new ReflectionTypeConstructorsIntrospector<>(this.type, this.introspector);
        }
        return this.constructorsIntrospector;
    }

    @Override
    public TypeParametersIntrospector typeParameters() {
        if (this.typeParametersIntrospector == null) {
            this.typeParametersIntrospector = new ReflectionTypeParametersIntrospector<>(this, this.parameterizedType, this.introspector);
        }
        return this.typeParametersIntrospector;
    }

    @Override
    public boolean isParentOf(final Class<?> type) {
        return this.compareHierarchy(this.type, type);
    }

    @Override
    public boolean isChildOf(final Class<?> type) {
        return this.compareHierarchy(type, this.type);
    }

    private boolean compareHierarchy(final Class<?> parent, final Class<?> child) {
        this.checkProxy();

        if (null == child || null == parent) return false;
        if (child == parent || child.equals(parent)) return true;

        if (parent.isAssignableFrom(child)) {
            return true;
        }
        if (parent.isPrimitive()) {
            return this.isPrimitiveWrapper(child, parent);
        }
        if (child.isPrimitive()) {
            return this.isPrimitiveWrapper(parent, child);
        }
        return false;
    }

    private boolean isPrimitiveWrapper(final Class<?> targetClass, final Class<?> primitive) {
        if (!primitive.isPrimitive()) {
            return false;
        }
        return PRIMITIVE_WRAPPERS.get(primitive) == targetClass;
    }

    @Override
    public boolean is(final Class<?> type) {
        return this.type.equals(type);
    }

    @Override
    public String name() {
        return this.type.getSimpleName();
    }

    @Override
    public String qualifiedName() {
        return this.type.getCanonicalName();
    }

    @Override
    public Result<TypeView<?>> elementType() {
        if (this.elementType == null) {
            this.checkProxy();
            this.elementType = this.isArray()
                    ? Result.of(this.introspector.introspect(this.type().getComponentType()))
                    : Result.of(new IllegalArgumentException("The introspected type must be an array to look up its element type"));
        }
        return this.elementType;
    }

    @Override
    public List<T> enumConstants() {
        if (this.enumConstants == null) {
            this.checkProxy();
            if (!this.isEnum()) this.enumConstants = List.of();
            else {
                this.enumConstants = List.of(this.type().getEnumConstants());
            }
        }
        return this.enumConstants;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T defaultOrNull() {
        // Do not use .cast here, getOrDefault causes boxing so we get e.g. Integer instead of int. Explicit cast
        // unboxes it correctly, but .cast will yield a ClassCastException.
        if (this.isPrimitive()) {
            return (T) PRIMITIVE_DEFAULTS.getOrDefault(this.type(), null);
        } else {
            final Class<?> primitive = WRAPPERS_TO_PRIMITIVE.get(this.type());
            if (primitive == null) return null;
            else return (T) PRIMITIVE_DEFAULTS.getOrDefault(primitive, null);
        }
    }

    @Override
    public T cast(final Object object) {
        if (object == null) return null;
        if (this.isInstance(object)) return this.type.cast(object);
        else throw new ClassCastException("Cannot cast '%s' to '%s'".formatted(object, this.type));
    }

    @Override
    public int modifiers() {
        return this.type.getModifiers();
    }
}
