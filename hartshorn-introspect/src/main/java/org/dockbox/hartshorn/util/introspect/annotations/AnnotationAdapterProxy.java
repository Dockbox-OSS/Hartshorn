/*
 * Copyright 2019-2024 the original author or authors.
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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedSet;
import java.util.concurrent.ConcurrentHashMap;

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
 *
 * @since 0.4.11
 *
 * @author Guus Lieben
 */
public class AnnotationAdapterProxy<A extends Annotation> implements InvocationHandler, AnnotationAdapter {

    private final Annotation actual;
    private final Class<A> targetAnnotationClass;
    private final SequencedSet<Class<? extends Annotation>> actualAnnotationHierarchy;
    private final AnnotationLookup owner;
    private final Map<String, Option<Object>> methodsCache = new ConcurrentHashMap<>();

    AnnotationAdapterProxy(Annotation actual, Class<A> targetAnnotationClass, SequencedSet<Class<? extends Annotation>> actualAnnotationHierarchy, AnnotationLookup owner) {
        this.actual = actual;
        this.targetAnnotationClass = targetAnnotationClass;
        this.actualAnnotationHierarchy = actualAnnotationHierarchy;
        this.owner = owner;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.getDeclaringClass() == AnnotationAdapter.class) {
            return this.actual;
        }

        if ("hashCode".equals(method.getName())) {
            return Objects.hash(
                    this.owner.unproxy(this.actual),
                    this.targetAnnotationClass
            );
        }

        if ("equals".equals(method.getName()) && method.getParameters().length == 1) {
            if (args[0] instanceof Annotation annotation) {
                return this.targetAnnotationClass == annotation.annotationType() && this.actual.equals(this.owner.unproxy(annotation));
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
    public SequencedSet<Class<? extends Annotation>> actualAnnotationHierarchy() {
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

    private Option<Object> searchInHierarchy(Annotation actual, Class<? extends Annotation> targetAnnotationClass, Collection<Class<? extends Annotation>> hierarchy, Method proxyMethod) {
        String name = proxyMethod.getName();
        try {
            Method method = actual.annotationType().getMethod(name);
            this.checkAliasType(proxyMethod, method);
            return Option.of(this.safeInvokeAnnotationMethod(method, actual));
        }
        catch (NoSuchMethodException e) {
            // search for AliasFor in same annotation type
            Option<Object> method = this.searchAlias(actual, targetAnnotationClass, proxyMethod, name);
            if (method.present()) {
                return method;
            }

            // search in super annotation type
            Option<Object> defaultValue = this.searchSuper(hierarchy, proxyMethod, name);
            if (defaultValue.present()) {
                return defaultValue;
            }

            try {
                Method targetAnnotationMethod = targetAnnotationClass.getMethod(name);
                this.checkAliasType(proxyMethod, targetAnnotationMethod);
                return Option.of(targetAnnotationMethod.getDefaultValue());
            }
            catch (NoSuchMethodException noSuchMethodException) {
                throw new RuntimeException(e);
            }
        }
    }

    @NonNull
    private Option<Object> searchSuper(Collection<Class<? extends Annotation>> hierarchy, Method proxyMethod, String name) {
        for (Class<? extends Annotation> klass : hierarchy) {
            try {
                Method klassMethod = klass.getMethod(name);
                this.checkAliasType(proxyMethod, klassMethod);
                Object defaultValue = klassMethod.getDefaultValue();
                if (defaultValue != null) {
                    return Option.of(defaultValue);
                }
            } catch (NoSuchMethodException ignored) {
                // Do not break yet, we might find it in a super class
            }

            Annotation[] annotationsOnCurrentAnnotationClass = klass.getAnnotations();
            for (Annotation annotationOnCurrentAnnotationClass : annotationsOnCurrentAnnotationClass) {
                if (hierarchy.contains(annotationOnCurrentAnnotationClass.annotationType())) {
                    try {
                        Method method = annotationOnCurrentAnnotationClass.annotationType().getMethod(name);
                        this.checkAliasType(proxyMethod, method);
                        return Option.of(this.safeInvokeAnnotationMethod(method, annotationOnCurrentAnnotationClass));
                    }
                    catch (NoSuchMethodException ignored) {
                        break;
                    }
                }
            }
        }
        return Option.empty();
    }

    @NonNull
    private Option<Object> searchAlias(Annotation actual, Class<? extends Annotation> targetAnnotationClass, Method proxyMethod, String name) {
        for (Method method : actual.annotationType().getMethods()) {
            Option<Object> result = searchAttributeAlias(actual, targetAnnotationClass, proxyMethod, name, method);

            if(result.present()) {
                return result;
            }
        }
        return Option.empty();
    }

    private Option<Object> searchAttributeAlias(Annotation actual, Class<? extends Annotation> targetAnnotationClass, Method proxyMethod, String name,
            Method method) {
        AttributeAlias attributeAlias = method.getAnnotation(AttributeAlias.class);
        if (attributeAlias == null) {
            return Option.empty();
        }

        if ((attributeAlias.target() == Void.class || attributeAlias.target() == targetAnnotationClass) && name.equals(attributeAlias.value())) {
            this.checkAliasType(proxyMethod, method);
            return Option.of(this.safeInvokeAnnotationMethod(method, actual));
        }
        return Option.empty();
    }

    private void checkAliasType(Method expected, Method actual) {
        if (expected.getReturnType() != actual.getReturnType()) {
            throw new IllegalArgumentException("Attribute " + actual.getName() + " in " + actual.getDeclaringClass().getSimpleName() + " has different return type than " + expected.getName() + " in " + expected.getDeclaringClass().getSimpleName());
        }
    }

    private Object safeInvokeAnnotationMethod(Method method, Annotation annotation) {
        try {
            method.setAccessible(true);
            Object out = method.invoke(annotation);
            method.setAccessible(false);
            return out;
        }
        catch (Exception e) {
            throw new ApplicationRuntimeException(e);
        }
    }

    @Override
    public Annotation actualAnnotation() {
        return this.actual;
    }
}
