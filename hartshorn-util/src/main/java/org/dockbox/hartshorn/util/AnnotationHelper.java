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
import org.dockbox.hartshorn.util.annotations.AliasFor;
import org.dockbox.hartshorn.util.annotations.CompositeOf;
import org.dockbox.hartshorn.util.annotations.Extends;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Offers fine-grained control over annotation composition and inheritance. This utility
 * class is forked from <a href="https://github.com/blindpirate/annotation-magic">AnnotationMagic</a>.
 */
final class AnnotationHelper {
    /**
     * The annotation-magic-lookup is a relatively expensive operation, so we'd better
     * cache the result as much as possible. In case you don't want the cached value to
     * stay in memory forever, you can pass a customized cache implementation, like LRU
     * cache, to the constructor.
     */
    private static final Map<Object, Exceptional<Object>> cache = HartshornUtils.emptyConcurrentMap();

    public static <A extends Annotation> A getOneOrNull(AnnotatedElement method, Class<A> targetAnnotationClass) {
        return assertZeroOrOne(getAllOrEmpty(method, targetAnnotationClass), method);
    }

    public static <A extends Annotation> List<A> getAllOrEmpty(AnnotatedElement method, Class<A> targetAnnotationClass) {
        return getCached(Arrays.asList(1, method, targetAnnotationClass),
                () -> getAnnotations(method.getAnnotations(), targetAnnotationClass));
    }

    public static boolean isAnnotationPresent(AnnotatedElement targetMethod, Class<? extends Annotation> annotationClass) {
        return !getAllOrEmpty(targetMethod, annotationClass).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private static <T> T getCached(List<Object> keys, Supplier<T> supplier) {
        Exceptional<Object> ret = cache.get(keys);
        if (ret == null) {
            ret = Exceptional.of(supplier::get);
            cache.put(keys, ret);
        }
        ret.rethrow();
        return (T) ret.orNull();
    }

    private static <A extends Annotation> List<A> getAnnotations(Annotation[] annotations, Class<A> targetClass) {
        return Stream.of(annotations)
                .flatMap(AnnotationHelper::expandAnnotation)
                .map(annotation -> examineAnnotation(annotation, targetClass))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private static int indexOf(Annotation[] annotations, Class<? extends Annotation> targetAnnotation) {
        for (int i = 0; i < annotations.length; ++i) {
            if (annotations[i].annotationType() == targetAnnotation) {
                return i;
            }
        }
        return -1;
    }

    private static Stream<Annotation> expandAnnotation(Annotation annotation) {
        Annotation[] annotationsOnTargetAnnotationType = annotation.annotationType().getAnnotations();

        int compositeOfIndex = indexOf(annotationsOnTargetAnnotationType, CompositeOf.class);
        int extendsIndex = indexOf(annotationsOnTargetAnnotationType, Extends.class);

        if (compositeOfIndex == -1) {
            return Stream.of(annotation);
        }

        LinkedList<Annotation> result = Stream.of(((CompositeOf) annotationsOnTargetAnnotationType[compositeOfIndex]).value())
                .map(klass -> (Annotation) Proxy.newProxyInstance(klass.getClassLoader(), new Class[]{klass}, new AnnotationInvocationHandler(klass, annotation))).collect(Collectors.toCollection(LinkedList::new));
        if (extendsIndex != -1) {
            if (extendsIndex < compositeOfIndex) {
                result.addFirst(annotation);
            } else {
                result.add(annotation);
            }
        }
        return result.stream();
    }

    /*
     * Walk along `@Extends` annotation hierarchy to get all annotationsWith.
     */
    protected static LinkedHashSet<Class<? extends Annotation>> getAnnotationHierarchy(Class<? extends Annotation> klass) {
        Class<? extends Annotation> currentClass = klass;
        LinkedHashSet<Class<? extends Annotation>> hierarchy = new LinkedHashSet<>();
        while (currentClass != null) {
            if (!hierarchy.add(currentClass)) {
                throw new IllegalArgumentException("Annotation hierarchy circular inheritance detected: " + currentClass);
            }
            currentClass = getSuperAnnotationOrNull(currentClass);
        }

        return hierarchy;
    }

    private static <A extends Annotation> A assertZeroOrOne(List<A> annotations, Object target) {
        if (annotations == null) return null;

        if (annotations.size() > 1) {
            throw new IllegalArgumentException("Found more than one annotation on " + target + ":\n"
                    + annotations.stream().map(Annotation::toString).collect(Collectors.joining("\n")));
        }

        return annotations.isEmpty() ? null : annotations.get(0);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Annotation> A examineAnnotation(Annotation actual, Class<A> targetAnnotationClass) {
        actual = getActualAnnotationBehindProxy(actual);
        // Two passes:
        // 1. scan all annotation hierarchy classes
        // 2. construct a proxy with all information (probably overridden by sub annotationsWith)
        LinkedHashSet<Class<? extends Annotation>> hierarchy = getAnnotationHierarchy(actual.annotationType());

        if (!hierarchy.contains(targetAnnotationClass)) {
            return null;
        }

        return (A) Proxy.newProxyInstance(targetAnnotationClass.getClassLoader(), new Class[]{targetAnnotationClass, AnnotationAdapter.class},
                new AnnotationAdapterProxy<>(actual, targetAnnotationClass, hierarchy));
    }

    private static Annotation getActualAnnotationBehindProxy(Annotation annotation) {
        if (annotation instanceof AnnotationAdapter) {
            return ((AnnotationAdapter) annotation).getActualAnnotation();
        } else {
            return annotation;
        }
    }

    public static <A extends Annotation> Annotation actual(A annotation) {
        final InvocationHandler handler = Proxy.getInvocationHandler(annotation);
        if (handler instanceof AnnotationAdapterProxy<?> proxy) {
            return proxy.actual;
        }
        return annotation;
    }

    public static boolean instanceOf(Annotation annotation, Class<? extends Annotation> type) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        return getCached(Arrays.asList(4, annotationType), () -> getAnnotationHierarchy(annotationType)).contains(type);
    }

    interface AnnotationAdapter {
        Annotation getActualAnnotation();
    }

    private static class AnnotationAdapterProxy<A extends Annotation> implements InvocationHandler {
        private final Annotation actual;
        private final Class<A> targetAnnotationClass;
        private final LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy;
        private final Map<String, Exceptional<Object>> methodsCache = new ConcurrentHashMap<>();

        AnnotationAdapterProxy(Annotation actual, Class<A> targetAnnotationClass, LinkedHashSet<Class<? extends Annotation>> actualAnnotationHierarchy) {
            this.actual = actual;
            this.targetAnnotationClass = targetAnnotationClass;
            this.actualAnnotationHierarchy = actualAnnotationHierarchy;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == AnnotationAdapter.class) {
                return this.actual;
            }

            if ("hashCode".equals(method.getName())) {
                return getActualAnnotationBehindProxy(this.actual).hashCode();
            }

            if ("equals".equals(method.getName()) && method.getParameters().length == 1) {
                if (args[0] instanceof Annotation) {
                    return this.actual.equals(getActualAnnotationBehindProxy((Annotation) args[0]));
                } else {
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

    private static Exceptional<Object> searchInHierarchy(Annotation actual, Class<? extends Annotation> targetAnnotationClass, Collection<Class<? extends Annotation>> hierarchy, String name) {
        try {
            Method method = actual.annotationType().getMethod(name);
            return Exceptional.of(safeInvokeAnnotationMethod(method, actual));
        } catch (NoSuchMethodException e) {
            // search for AliasFor in same annotation type
            for (Method method : actual.annotationType().getMethods()) {
                AliasFor aliasFor = method.getAnnotation(AliasFor.class);
                if (aliasFor != null && (aliasFor.target() == AliasFor.DefaultThis.class || aliasFor.target() == targetAnnotationClass) && name.equals(aliasFor.value())) {
                    // Bingo! We found it!
                    return Exceptional.of(safeInvokeAnnotationMethod(method, actual));
                }
            }

            // search in super annotation type
            for (Class<? extends Annotation> klass : hierarchy) {
                Annotation[] annotationsOnCurrentAnnotationClass = klass.getAnnotations();
                for (Annotation annotationOnCurrentAnnotationClass : annotationsOnCurrentAnnotationClass) {
                    if (hierarchy.contains(annotationOnCurrentAnnotationClass.annotationType())) {
                        try {
                            Method method = annotationOnCurrentAnnotationClass.annotationType().getMethod(name);
                            return Exceptional.of(safeInvokeAnnotationMethod(method, annotationOnCurrentAnnotationClass));
                        } catch (NoSuchMethodException ignored) {
                            break;
                        }
                    }
                }
            }
            try {
                Method method = targetAnnotationClass.getMethod(name);
                return Exceptional.of(method.getDefaultValue());
            } catch (NoSuchMethodException noSuchMethodException) {
                throw new RuntimeException(e);
            }
        }
    }

    private static Object safeInvokeAnnotationMethod(Method method, Annotation annotation) {
        try {
            return method.invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends Annotation> getSuperAnnotationOrNull(Class<? extends Annotation> currentClass) {
        Extends extendsAnnotation = currentClass.getAnnotation(Extends.class);
        return extendsAnnotation == null ? null : extendsAnnotation.value();
    }

    private static class AnnotationInvocationHandler implements InvocationHandler {
        final Map<String, Object> cache;
        private final Class<?> klass;
        private final Annotation annotation;

        public AnnotationInvocationHandler(Class<?> klass, Annotation annotation) {
            this.klass = klass;
            this.annotation = annotation;
            this.cache = new HashMap<>();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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

            for (Method methodInCompositeAnnotation : this.annotation.annotationType().getMethods()) {
                AliasFor aliasFor = methodInCompositeAnnotation.getAnnotation(AliasFor.class);
                if (aliasFor != null && (this.isDirectAlias(aliasFor, method) || this.isIndirectAlias(aliasFor, method))) {
                    result = methodInCompositeAnnotation.invoke(this.annotation);
                    this.cache.put(method.getName(), result);
                    return result;
                }
            }

            try {
                Object ret = this.klass.getMethod(method.getName()).getDefaultValue();
                if (ret == null) {
                    throw new IllegalStateException("Can't invoke " + this.klass.getName() + "." + method.getName() + "() on composite annotation " + this.annotation);
                }
                return ret;
            } catch (NoSuchMethodError e) {
                throw new IllegalStateException("Can't invoke " + this.klass.getName() + "." + method.getName() + "() on composite annotation " + this.annotation, e);
            }
        }

        private boolean isIndirectAlias(AliasFor aliasFor, Method methodBeingInvoked) {
            if (aliasFor.target() != this.klass) {
                return false;
            }
            AliasFor redirect = methodBeingInvoked.getAnnotation(AliasFor.class);
            return redirect != null && redirect.target() == AliasFor.DefaultThis.class && redirect.value().equals(aliasFor.value());
        }

        private boolean isDirectAlias(AliasFor aliasFor, Method methodBeingInvoked) {
            return aliasFor.target() == this.klass && aliasFor.value().equals(methodBeingInvoked.getName());
        }
    }
}
