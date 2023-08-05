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
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.collections.BiMap;
import org.dockbox.hartshorn.util.introspect.ElementModifiersIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.TypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionElementModifiersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionModifierCarrierView;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionClassTypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeFieldsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionTypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.reflect.ReflectionParameterizedTypeParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.view.PackageView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReflectionTypeView<T> extends ReflectionAnnotatedElementView implements ReflectionModifierCarrierView, TypeView<T> {

    private static final BiMap<Class<?>, Class<?>> WRAPPERS = BiMap.ofEntries(
            Map.entry(Boolean.class, boolean.class),
            Map.entry(Byte.class, byte.class),
            Map.entry(Character.class, char.class),
            Map.entry(Double.class, double.class),
            Map.entry(Float.class, float.class),
            Map.entry(Integer.class, int.class),
            Map.entry(Long.class, long.class),
            Map.entry(Short.class, short.class)
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

    private final Introspector introspector;
    private final Class<T> type;
    private final ParameterizedType parameterizedType;

    private List<T> enumConstants;
    private TypeView<?> parent;
    private TypeView<?> genericParent;
    private List<TypeView<?>> interfaces;
    private List<TypeView<?>> genericInterfaces;
    private List<TypeView<? extends T>> permittedSubclasses;
    private Option<TypeView<?>> elementType;

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
        return this.isInterface() || this.modifiers().isAbstract();
    }

    @Override
    public boolean isFinal() {
        return this.modifiers().isFinal();
    }

    @Override
    public boolean isStatic() {
        return this.modifiers().isStatic();
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
    public boolean isSealed() {
        return this.type.isSealed();
    }

    @Override
    public boolean isNonSealed() {
        return this.superClass().isSealed() && !this.isSealed() && !this.isFinal();
    }

    @Override
    public boolean isPermittedSubclass() {
        if (this.superClass().isSealed()) {
            return this.superClass().permittedSubclasses().contains(this);
        }
        return false;
    }

    @Override
    public boolean isPermittedSubclass(final Class<?> subclass) {
        if (this.isSealed()) {
            return this.permittedSubclasses().stream()
                    .anyMatch(permittedSubclass -> permittedSubclass.type().equals(subclass));
        }
        return false;
    }

    @Override
    public List<TypeView<? extends T>> permittedSubclasses() {
        if (this.permittedSubclasses == null) {
            final List<TypeView<? extends T>> list = new ArrayList<>();
            for(final Class<?> permittedSubclass : this.type.getPermittedSubclasses()) {
                final TypeView<?> introspect = this.introspector.introspect(permittedSubclass);
                final TypeView<T> adjustedType = TypeUtils.adjustWildcards(introspect, TypeView.class);
                list.add(adjustedType);
            }
            this.permittedSubclasses = list;
        }
        return this.permittedSubclasses;
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
    public List<TypeView<?>> genericInterfaces() {
        if (this.genericInterfaces == null) {
            this.genericInterfaces = Arrays.stream(this.type().getGenericInterfaces())
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.genericInterfaces;
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
    public TypeView<?> genericSuperClass() {
        if (this.genericParent == null) {
            final Type genericSuperclass = this.type().getGenericSuperclass();
            if (genericSuperclass != null) {
                this.genericParent = this.introspector.introspect(genericSuperclass);
            }
            else {
                this.genericParent = this.superClass();
            }
        }
        return this.genericParent;
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
            this.typeParametersIntrospector = this.parameterizedType == null
                    ? new ReflectionClassTypeParametersIntrospector(this, this.introspector)
                    : new ReflectionParameterizedTypeParametersIntrospector<>(this, this.parameterizedType, this.introspector);
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
        return WRAPPERS.inverse().get(primitive) == targetClass;
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
            //noinspection unchecked
            return (T) PRIMITIVE_DEFAULTS.getOrDefault(this.type(), null);
        } else {
            final Class<?> primitive = WRAPPERS.get(this.type());
            if (primitive == null) return null;
            else {
                //noinspection unchecked
                return (T) PRIMITIVE_DEFAULTS.getOrDefault(primitive, null);
            }
        }
    }

    @Override
    public T cast(final Object object) {
        if (object == null) return null;
        // Do not use .cast here, getOrDefault causes boxing so we get e.g. Integer instead of int. Explicit cast
        // unboxes it correctly, but .cast will yield a ClassCastException.
        if (this.isInstance(object)) //noinspection unchecked
            return (T) object;
        else {
            final String targetType = this.qualifiedName();
            final String objectType = object.getClass().getCanonicalName();
            throw new ClassCastException("Cannot cast '%s' to '%s'".formatted(objectType, targetType));
        }
    }

    @Override
    public PackageView packageInfo() {
        return new ReflectionPackageView(this.introspector, this.type.getPackage());
    }

    @Override
    public TypeView<?> rawType() {
        if (this.parameterizedType == null) {
            return this;
        }
        else {
            return this.introspector.introspect(this.parameterizedType.getRawType());
        }
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

    @Override
    public ElementModifiersIntrospector modifiers() {
        return new ReflectionElementModifiersIntrospector(this.type.getModifiers());
    }
}
