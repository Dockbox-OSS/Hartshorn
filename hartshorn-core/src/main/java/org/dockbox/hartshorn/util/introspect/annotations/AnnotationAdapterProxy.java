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

package org.dockbox.hartshorn.util.introspect.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A proxy implementation of {@link Annotation} which allows for the creation of
 * a composite annotation from multiple annotations of the 'same' type. This
 * correctly looks up the values of the annotation in the order of the annotations
 * provided. If multiple annotations provide the same value, the first one is
 * used.
 *
 * <p>Implementation note: this class is an internal implementation detail of
 * {@link VirtualHierarchyAnnotationLookup}, and is not intended for direct use.
 * It is public so that it can be used for inspection of the composite annotation,
 * but the constructor is only intended to be called by the {@link VirtualHierarchyAnnotationLookup}.
 *
 * @param <A> The type of annotation this proxy represents
 */
public class AnnotationAdapterProxy<A extends Annotation> implements InvocationHandler {

    private final Annotation actual;
    private final Class<A> targetAnnotationClass;
    private final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy;
    private final AnnotationLookup owner;
    private final Map<String, Option<Object>> methodsCache = new ConcurrentHashMap<>();

    AnnotationAdapterProxy(final Annotation actual, final Class<A> targetAnnotationClass, final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy, final AnnotationLookup owner) {
        this.actual = actual;
        this.targetAnnotationClass = targetAnnotationClass;
        this.actualAnnotationHierarchy = actualAnnotationHierarchy;
        this.owner = owner;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) {
        if (method.getDeclaringClass() == AnnotationAdapter.class) {
            return this.actual;
        }

        if ("hashCode".equals(method.getName())) {
            return this.owner.unproxy(this.actual).hashCode();
        }

        if ("equals".equals(method.getName()) && method.getParameters().length == 1) {
            if (args[0] instanceof Annotation) {
                return this.actual.equals(this.owner.unproxy((Annotation) args[0]));
            }
            else {
                return this.actual.equals(args[0]);
            }
        }

        Option<Object> cachedField = this.methodsCache.get(method.getName());
        if (cachedField == null) {
            cachedField = this.searchInHierarchy(this.actual, this.targetAnnotationClass, this.actualAnnotationHierarchy, method);
            this.methodsCache.put(method.getName(), cachedField);
        }
        return cachedField.orNull();
    }

    /**
     * Returns the original annotation backing this proxy.
     * @return The original annotation
     */
    public Annotation actual() {
        return this.actual;
    }

    /**
     * Returns the target annotation class for this proxy.
     * @return The target annotation class
     */
    public Class<A> targetAnnotationClass() {
        return this.targetAnnotationClass;
    }

    /**
     * Returns the actual annotation hierarchy for this proxy.
     * @return The actual annotation hierarchy
     */
    public LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy() {
        return this.actualAnnotationHierarchy;
    }

    /**
     * Returns the owner of this proxy. The owner is expected to be a {@link VirtualHierarchyAnnotationLookup},
     * but this is not enforced.
     * @return The owner of this proxy
     */
    public AnnotationLookup owner() {
        return this.owner;
    }

    private Option<Object> searchInHierarchy(final Annotation actual, final Class<? extends Annotation> targetAnnotationClass, final Collection<Class<? extends Annotation>> hierarchy, final Method proxyMethod) {
        final String name = proxyMethod.getName();
        try {
            final Method method = actual.annotationType().getMethod(name);
            this.checkAliasType(proxyMethod, method);
            return Option.of(this.safeInvokeAnnotationMethod(method, actual));
        }
        catch (final NoSuchMethodException e) {
            // search for AliasFor in same annotation type
            final Option<Object> method = this.searchAlias(actual, targetAnnotationClass, proxyMethod, name);
            if (method.present()) return method;

            // search in super annotation type
            final Option<Object> defaultValue = this.searchSuper(hierarchy, proxyMethod, name);
            if (defaultValue.present()) return defaultValue;

            try {
                final Method targetAnnotationMethod = targetAnnotationClass.getMethod(name);
                this.checkAliasType(proxyMethod, targetAnnotationMethod);
                return Option.of(targetAnnotationMethod.getDefaultValue());
            }
            catch (final NoSuchMethodException noSuchMethodException) {
                throw new RuntimeException(e);
            }
        }
    }

    @NonNull
    private Option<Object> searchSuper(final Collection<Class<? extends Annotation>> hierarchy, final Method proxyMethod, final String name) {
        for (final Class<? extends Annotation> klass : hierarchy) {
            try {
                final Method klassMethod = klass.getMethod(name);
                if (klassMethod != null) {
                    this.checkAliasType(proxyMethod, klassMethod);
                    final Object defaultValue = klassMethod.getDefaultValue();
                    if (defaultValue != null) return Option.of(defaultValue);
                }
            } catch (final NoSuchMethodException ignored) {
                // Do not break yet, we might find it in a super class
            }

            final Annotation[] annotationsOnCurrentAnnotationClass = klass.getAnnotations();
            for (final Annotation annotationOnCurrentAnnotationClass : annotationsOnCurrentAnnotationClass) {
                if (hierarchy.contains(annotationOnCurrentAnnotationClass.annotationType())) {
                    try {
                        final Method method = annotationOnCurrentAnnotationClass.annotationType().getMethod(name);
                        this.checkAliasType(proxyMethod, method);
                        return Option.of(this.safeInvokeAnnotationMethod(method, annotationOnCurrentAnnotationClass));
                    }
                    catch (final NoSuchMethodException ignored) {
                        break;
                    }
                }
            }
        }
        return Option.empty();
    }

    @NonNull
    private Option<Object> searchAlias(final Annotation actual, final Class<? extends Annotation> targetAnnotationClass, final Method proxyMethod, final String name) {
        for (final Method method : actual.annotationType().getMethods()) {
            final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
            if (aliasFor == null) continue;

            if ((aliasFor.target() == AliasFor.DefaultThis.class || aliasFor.target() == targetAnnotationClass) && name.equals(aliasFor.value())) {
                this.checkAliasType(proxyMethod, method);
                return Option.of(this.safeInvokeAnnotationMethod(method, actual));
            }
        }
        return Option.empty();
    }

    private void checkAliasType(final Method expected, final Method actual) {
        if (expected.getReturnType() != actual.getReturnType()) {
            throw new IllegalArgumentException("Attribute " + actual.getName() + " in " + actual.getDeclaringClass().getSimpleName() + " has different return type than " + expected.getName() + " in " + expected.getDeclaringClass().getSimpleName());
        }
    }

    private Object safeInvokeAnnotationMethod(final Method method, final Annotation annotation) {
        try {
            method.setAccessible(true);
            final Object out = method.invoke(annotation);
            method.setAccessible(false);
            return out;
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
