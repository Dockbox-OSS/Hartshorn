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

package org.dockbox.hartshorn.util.introspect.reflect.view;

import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectionTypeView<T> extends ReflectionAnnotatedElementView<T> implements ReflectionModifierCarrierView, TypeView<T> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.ofEntries(
            Map.entry(boolean.class, Boolean.class),
            Map.entry(byte.class, Byte.class),
            Map.entry(char.class, Character.class),
            Map.entry(double.class, Double.class),
            Map.entry(float.class, Float.class),
            Map.entry(int.class, Integer.class),
            Map.entry(long.class, Long.class),
            Map.entry(short.class, Short.class)
    );
    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = Map.ofEntries(
            Map.entry(boolean.class, false),
            Map.entry(byte.class, 0),
            Map.entry(char.class, '\u0000'),
            Map.entry(double.class, 0.0d),
            Map.entry(float.class, 0.0f),
            Map.entry(int.class, 0),
            Map.entry(long.class, 0L),
            Map.entry(short.class, 0)
    );
    private static final Map<Class<?>, Class<?>> WRAPPERS_TO_PRIMITIVE = Map.ofEntries(
            Map.entry(Boolean.class, boolean.class),
            Map.entry(Byte.class, byte.class),
            Map.entry(Character.class, char.class),
            Map.entry(Double.class, double.class),
            Map.entry(Float.class, float.class),
            Map.entry(Integer.class, int.class),
            Map.entry(Long.class, long.class),
            Map.entry(Short.class, short.class)
    );

    private final Introspector introspector;
    private final Class<T> type;
    private final ParameterizedType parameterizedType;

    private List<T> enumConstants;
    private TypeView<?> parent;
    private List<TypeView<?>> interfaces;
    private Option<TypeView<?>> elementType;
    private Tristate isProxy = Tristate.UNDEFINED;

    private TypeMethodsIntrospector<T> methodsIntrospector;
    private TypeFieldsIntrospector<T> fieldsIntrospector;
    private TypeConstructorsIntrospector<T> constructorsIntrospector;
    private TypeParametersIntrospector typeParametersIntrospector;

    public ReflectionTypeView(final ReflectionIntrospector introspector, final Class<T> type) {
        this(introspector, type, null);
    }

    public ReflectionTypeView(final ReflectionIntrospector introspector, final ParameterizedType parameterizedType) {
        this(introspector, (Class<T>) parameterizedType.getRawType(), parameterizedType);
    }

    private ReflectionTypeView(final ReflectionIntrospector introspector, final Class<T> type, final ParameterizedType parameterizedType) {
        super(introspector);
        this.introspector = introspector;
        this.type = type;
        this.parameterizedType = parameterizedType;
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
    public boolean isRecord() {
        return this.type.isRecord();
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
            this.interfaces = Arrays.stream(this.type().getInterfaces())
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.interfaces;
    }

    @Override
    public TypeView<?> superClass() {
        if (this.parent == null) {
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
    public Option<TypeView<?>> elementType() throws IllegalArgumentException {
        if (this.elementType == null) {
            if (this.isArray()) this.elementType = Option.of(this.introspector.introspect(this.type().getComponentType()));
            else throw new IllegalArgumentException("The introspected type must be an array to look up its element type");
        }
        return this.elementType;
    }

    @Override
    public List<T> enumConstants() {
        if (this.enumConstants == null) {
            if (!this.isEnum()) this.enumConstants = List.of();
            else {
                this.enumConstants = List.of(this.type().getEnumConstants());
            }
        }
        return this.enumConstants;
    }

    @Override
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
        // Do not use .cast here, getOrDefault causes boxing so we get e.g. Integer instead of int. Explicit cast
        // unboxes it correctly, but .cast will yield a ClassCastException.
        if (this.isInstance(object)) return (T) object;
        else throw new ClassCastException("Cannot cast '%s' to '%s'".formatted(object, this.type));
    }

    @Override
    public int modifiers() {
        return this.type.getModifiers();
    }

    @Override
    public PackageView packageInfo() {
        return new ReflectionPackageView(this.introspector, this.type.getPackage());
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        collector.property("name").write(this.name());
        collector.property("package").write(this.packageInfo().name());

        final TypeParametersIntrospector typeParameters = this.typeParameters();
        if (typeParameters.count() > 0) {
            collector.property("typeParameters").write(typeParameters.all().toArray(Reportable[]::new));
        }
    }
}
