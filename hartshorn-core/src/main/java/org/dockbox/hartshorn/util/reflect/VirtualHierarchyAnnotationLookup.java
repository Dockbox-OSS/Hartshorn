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

import org.dockbox.hartshorn.util.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class VirtualHierarchyAnnotationLookup implements AnnotationLookup {

    private static final Map<HierarchyKey, Result<Object>> cache = new ConcurrentHashMap<>();

    @Override
    public <A extends Annotation> A find(final AnnotatedElement element, final Class<A> annotationType) throws DuplicateAnnotationCompositeException {
        final List<A> allInHierarchy = this.findAll(element, annotationType);
        if (allInHierarchy.size() > 1) {
            throw new DuplicateAnnotationCompositeException(element, allInHierarchy);
        }
        return allInHierarchy.isEmpty() ? null : allInHierarchy.get(0);
    }

    @Override
    public <A extends Annotation> List<A> findAll(final AnnotatedElement element, final Class<A> annotationType) {
        final HierarchyKey key = new HierarchyKey(element, annotationType);
        return this.fromCache(key, () -> this.annotationsOnElement(element, annotationType));
    }

    protected <T> T fromCache(final HierarchyKey key, final Supplier<T> supplier) {
        Result<Object> ret = cache.get(key);
        if (ret == null) {
            ret = Result.of(supplier::get);
            cache.put(key, ret);
        }
        ret.rethrowUnchecked();
        return (T) ret.get();
    }

    protected <A extends Annotation> List<A> annotationsOnElement(final AnnotatedElement element, final Class<A> annotationType) {
        return Arrays.stream(element.getAnnotations())
                .map(annotation -> this.examineAnnotation(annotation, annotationType))
                .filter(Objects::nonNull)
                .toList();
    }

    protected <A extends Annotation> A examineAnnotation(Annotation actual, final Class<A> targetAnnotationClass) {
        actual = this.unproxy(actual);
        final LinkedHashSet<Class<? extends Annotation>> hierarchy = this.annotationHierarchy(actual.annotationType());

        if (!hierarchy.contains(targetAnnotationClass)) return null;

        final InvocationHandler adapter = new AnnotationAdapterProxy<>(actual, targetAnnotationClass, hierarchy, this);
        final Class<?>[] interfaces = { targetAnnotationClass, AnnotationAdapter.class };
        final Object proxy = Proxy.newProxyInstance(
                VirtualHierarchyAnnotationLookup.class.getClassLoader(),
                interfaces,
                adapter);

        return targetAnnotationClass.cast(proxy);
    }

    @Override
    public Annotation unproxy(final Annotation annotation) {
        if (annotation instanceof AnnotationAdapter) {
            return ((AnnotationAdapter) annotation).actualAnnotation();
        }
        return annotation;
    }

    @Override
    public LinkedHashSet<Class<? extends Annotation>> annotationHierarchy(final Class<? extends Annotation> klass) {
        Class<? extends Annotation> currentClass = klass;
        final LinkedHashSet<Class<? extends Annotation>> hierarchy = new LinkedHashSet<>();
        while (currentClass != null) {
            if (!hierarchy.add(currentClass)) {
                throw new CircularHierarchyException(currentClass);
            }
            currentClass = superAnnotationOrNull(currentClass);
        }

        return hierarchy;
    }

    private static Class<? extends Annotation> superAnnotationOrNull(final Class<? extends Annotation> currentClass) {
        final Extends extendsAnnotation = currentClass.getAnnotation(Extends.class);
        return extendsAnnotation == null ? null : extendsAnnotation.value();
    }

    private static class HierarchyKey {
        private final AnnotatedElement element;
        private final Class<? extends Annotation> annotationType;

        public HierarchyKey(final AnnotatedElement element, final Class<? extends Annotation> annotationType) {
            this.element = element;
            this.annotationType = annotationType;
        }

        public AnnotatedElement element() {
            return this.element;
        }

        public Class<? extends Annotation> annotationType() {
            return this.annotationType;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final HierarchyKey that = (HierarchyKey) o;
            return Objects.equals(this.element, that.element) && Objects.equals(this.annotationType, that.annotationType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.element, this.annotationType);
        }
    }
}
