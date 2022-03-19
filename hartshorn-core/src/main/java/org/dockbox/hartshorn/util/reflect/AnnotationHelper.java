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

package org.dockbox.hartshorn.util.reflect;

import org.dockbox.hartshorn.util.Exceptional;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Offers fine-grained control over annotation composition and inheritance. This utility
 * class is forked from <a href="https://github.com/blindpirate/annotation-magic">AnnotationMagic</a>.
 */
public final class AnnotationHelper {

    /**
     * The annotation-magic-lookup is a relatively expensive operation, so we'd better
     * cache the result as much as possible.
     */
    private static final Map<Object, Exceptional<Object>> cache = new ConcurrentHashMap<>();

    private AnnotationHelper() {}

    public static <A extends Annotation> A oneOrNull(final AnnotatedElement element, final Class<A> targetAnnotationClass) {
        return assertZeroOrOne(allOrEmpty(element, targetAnnotationClass), element);
    }

    private static <A extends Annotation> A assertZeroOrOne(final List<A> annotations, final Object target) {
        if (annotations == null) return null;

        if (annotations.size() > 1) {
            throw new IllegalArgumentException("Found more than one annotation on " + target + ":\n"
                    + annotations.stream().map(Annotation::toString).collect(Collectors.joining("\n")));
        }

        return annotations.isEmpty() ? null : annotations.get(0);
    }

    public static <A extends Annotation> List<A> allOrEmpty(final AnnotatedElement element, final Class<A> targetAnnotationClass) {
        return cached(Arrays.asList(1, element, targetAnnotationClass),
                () -> annotations(element.getAnnotations(), targetAnnotationClass));
    }

    private static <T> T cached(final List<Object> keys, final Supplier<T> supplier) {
        Exceptional<Object> ret = cache.get(keys);
        if (ret == null) {
            ret = Exceptional.of(supplier::get);
            cache.put(keys, ret);
        }
        ret.rethrowUnchecked();
        return (T) ret.orNull();
    }

    private static <A extends Annotation> List<A> annotations(final Annotation[] annotations, final Class<A> targetClass) {
        return Stream.of(annotations)
                .map(annotation -> examineAnnotation(annotation, targetClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static <A extends Annotation> A examineAnnotation(Annotation actual, final Class<A> targetAnnotationClass) {
        actual = actualAnnotationBehindProxy(actual);
        // Two passes:
        // 1. scan all annotation hierarchy classes
        // 2. construct a proxy with all information (probably overridden by sub annotationsWith)
        final LinkedHashSet<Class<? extends Annotation>> hierarchy = annotationHierarchy(actual.annotationType());

        if (!hierarchy.contains(targetAnnotationClass)) {
            return null;
        }

        return (A) Proxy.newProxyInstance(AnnotationHelper.class.getClassLoader(), new Class[]{ targetAnnotationClass, AnnotationAdapter.class },
                new AnnotationAdapterProxy<>(actual, targetAnnotationClass, hierarchy));
    }

    private static Annotation actualAnnotationBehindProxy(final Annotation annotation) {
        if (annotation instanceof AnnotationAdapter) {
            return ((AnnotationAdapter) annotation).actualAnnotation();
        }
        else {
            return annotation;
        }
    }

    /*
     * Walk along `@Extends` annotation hierarchy to get all annotationsWith.
     */
    public static LinkedHashSet<Class<? extends Annotation>> annotationHierarchy(final Class<? extends Annotation> klass) {
        Class<? extends Annotation> currentClass = klass;
        final LinkedHashSet<Class<? extends Annotation>> hierarchy = new LinkedHashSet<>();
        while (currentClass != null) {
            if (!hierarchy.add(currentClass)) {
                throw new IllegalArgumentException("Annotation hierarchy circular inheritance detected: " + currentClass);
            }
            currentClass = superAnnotationOrNull(currentClass);
        }

        return hierarchy;
    }

    private static Class<? extends Annotation> superAnnotationOrNull(final Class<? extends Annotation> currentClass) {
        final Extends extendsAnnotation = currentClass.getAnnotation(Extends.class);
        return extendsAnnotation == null ? null : extendsAnnotation.value();
    }

    public static boolean annotationPresent(final AnnotatedElement element, final Class<? extends Annotation> annotationClass) {
        return !allOrEmpty(element, annotationClass).isEmpty();
    }

    public static <A extends Annotation> Annotation actual(final A annotation) {
        final InvocationHandler handler = Proxy.getInvocationHandler(annotation);
        if (handler instanceof AnnotationAdapterProxy<?> proxy) {
            return proxy.actual;
        }
        return annotation;
    }

    public static boolean instanceOf(final Annotation annotation, final Class<? extends Annotation> type) {
        final Class<? extends Annotation> annotationType = annotation.annotationType();
        return cached(Arrays.asList(4, annotationType), () -> annotationHierarchy(annotationType)).contains(type);
    }

    private static Exceptional<Object> searchInHierarchy(final Annotation actual, final Class<? extends Annotation> targetAnnotationClass, final Collection<Class<? extends Annotation>> hierarchy, final String name) {
        try {
            final Method method = actual.annotationType().getMethod(name);
            return Exceptional.of(safeInvokeAnnotationMethod(method, actual));
        }
        catch (final NoSuchMethodException e) {
            // search for AliasFor in same annotation type
            for (final Method method : actual.annotationType().getMethods()) {
                final AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor != null && (aliasFor.target() == AliasFor.DefaultThis.class || aliasFor.target() == targetAnnotationClass) && name.equals(aliasFor.value())) {
                    // Bingo! We found it!
                    return Exceptional.of(safeInvokeAnnotationMethod(method, actual));
                }
            }

            // search in super annotation type
            for (final Class<? extends Annotation> klass : hierarchy) {
                try {
                    final Method klassMethod = klass.getMethod(name);
                    if (klassMethod != null) {
                        final Object defaultValue = klassMethod.getDefaultValue();
                        if (defaultValue != null) return Exceptional.of(defaultValue);
                    }
                } catch (final NoSuchMethodException ignored) {
                    // Do not break yet, we might find it in a super class
                }

                final Annotation[] annotationsOnCurrentAnnotationClass = klass.getAnnotations();
                for (final Annotation annotationOnCurrentAnnotationClass : annotationsOnCurrentAnnotationClass) {
                    if (hierarchy.contains(annotationOnCurrentAnnotationClass.annotationType())) {
                        try {
                            final Method method = annotationOnCurrentAnnotationClass.annotationType().getMethod(name);
                            return Exceptional.of(safeInvokeAnnotationMethod(method, annotationOnCurrentAnnotationClass));
                        }
                        catch (final NoSuchMethodException ignored) {
                            break;
                        }
                    }
                }
            }
            try {
                final Method method = targetAnnotationClass.getMethod(name);
                return Exceptional.of(method.getDefaultValue());
            }
            catch (final NoSuchMethodException noSuchMethodException) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Object safeInvokeAnnotationMethod(final Method method, final Annotation annotation) {
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

    @FunctionalInterface
    public interface AnnotationAdapter {
        Annotation actualAnnotation();
    }

    private static class AnnotationAdapterProxy<A extends Annotation> implements InvocationHandler {
        private final Annotation actual;
        private final Class<A> targetAnnotationClass;
        private final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy;
        private final Map<String, Exceptional<Object>> methodsCache = new ConcurrentHashMap<>();

        AnnotationAdapterProxy(final Annotation actual, final Class<A> targetAnnotationClass, final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy) {
            this.actual = actual;
            this.targetAnnotationClass = targetAnnotationClass;
            this.actualAnnotationHierarchy = actualAnnotationHierarchy;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) {
            if (method.getDeclaringClass() == AnnotationAdapter.class) {
                return this.actual;
            }

            if ("hashCode".equals(method.getName())) {
                return actualAnnotationBehindProxy(this.actual).hashCode();
            }

            if ("equals".equals(method.getName()) && method.getParameters().length == 1) {
                if (args[0] instanceof Annotation) {
                    return this.actual.equals(actualAnnotationBehindProxy((Annotation) args[0]));
                }
                else {
                    return this.actual.equals(args[0]);
                }
            }

            Exceptional<Object> cachedField = this.methodsCache.get(method.getName());
            if (cachedField == null) {
                cachedField = searchInHierarchy(this.actual, this.targetAnnotationClass, this.actualAnnotationHierarchy, method.getName());
                this.methodsCache.put(method.getName(), cachedField);
            }
            return cachedField.orNull();
        }
    }

    public static class AnnotationInvocationHandler implements InvocationHandler {

        final Map<String, Object> cache;
        private final Class<?> klass;
        private final Annotation annotation;

        public AnnotationInvocationHandler(final Class<?> klass, final Annotation annotation) {
            this.klass = klass;
            this.annotation = annotation;
            this.cache = new HashMap<>();
        }

        public Annotation annotation() {
            return this.annotation;
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            if ("annotationType".equals(method.getName())) {
                return this.klass;
            }
            if ("toString".equals(method.getName())) {
                return "@" + this.klass;
            }
            Object result = this.cache.get(method.getName());
            if (result != null) {
                return result;
            }

            for (final Method methodInCompositeAnnotation : this.annotation.annotationType().getMethods()) {
                final AliasFor aliasFor = methodInCompositeAnnotation.getAnnotation(AliasFor.class);
                if (aliasFor != null && (this.directAlias(aliasFor, method) || this.indirectAlias(aliasFor, method))) {
                    result = methodInCompositeAnnotation.invoke(this.annotation);
                    this.cache.put(method.getName(), result);
                    return result;
                }
            }

            try {
                final Object ret = this.klass.getMethod(method.getName()).getDefaultValue();
                if (ret == null) {
                    throw new IllegalStateException("Can't invoke " + this.klass.getName() + "." + method.getName() + "() on composite annotation " + this.annotation);
                }
                return ret;
            }
            catch (final NoSuchMethodError e) {
                throw new IllegalStateException("Can't invoke " + this.klass.getName() + "." + method.getName() + "() on composite annotation " + this.annotation, e);
            }
        }

        private boolean directAlias(final AliasFor aliasFor, final Method methodBeingInvoked) {
            return aliasFor.target() == this.klass && aliasFor.value().equals(methodBeingInvoked.getName());
        }

        private boolean indirectAlias(final AliasFor aliasFor, final Method methodBeingInvoked) {
            if (aliasFor.target() != this.klass) {
                return false;
            }
            final AliasFor redirect = methodBeingInvoked.getAnnotation(AliasFor.class);
            return redirect != null && redirect.target() == AliasFor.DefaultThis.class && redirect.value().equals(aliasFor.value());
        }
    }
}
