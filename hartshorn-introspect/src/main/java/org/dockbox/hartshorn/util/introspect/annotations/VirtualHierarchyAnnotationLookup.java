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

package org.dockbox.hartshorn.util.introspect.annotations;

import org.dockbox.hartshorn.util.option.Option;

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

    private static final Map<HierarchyKey, Option<Object>> cache = new ConcurrentHashMap<>();

    @Override
    public <A extends Annotation> A find(AnnotatedElement element, Class<A> annotationType) throws DuplicateAnnotationCompositeException {
        List<A> allInHierarchy = this.findAll(element, annotationType);
        if (allInHierarchy.size() > 1) {
            throw new DuplicateAnnotationCompositeException(element, allInHierarchy);
        }
        return allInHierarchy.isEmpty() ? null : allInHierarchy.get(0);
    }

    @Override
    public <A extends Annotation> List<A> findAll(AnnotatedElement element, Class<A> annotationType) {
        HierarchyKey key = new HierarchyKey(element, annotationType);
        return this.fromCache(key, () -> this.annotationsOnElement(element, annotationType));
    }

    protected <T> T fromCache(HierarchyKey key, Supplier<T> supplier) {
        Option<Object> ret = cache.get(key);
        if (ret == null) {
            ret = Option.of(supplier::get);
            cache.put(key, ret);
        }
        return (T) ret.get();
    }

    protected <A extends Annotation> List<A> annotationsOnElement(AnnotatedElement element, Class<A> annotationType) {
        return Arrays.stream(element.getAnnotations())
                .map(annotation -> this.examineAnnotation(annotation, annotationType))
                .filter(Objects::nonNull)
                .toList();
    }

    protected <A extends Annotation> A examineAnnotation(Annotation actual, Class<A> targetAnnotationClass) {
        actual = this.unproxy(actual);
        LinkedHashSet<Class<? extends Annotation>> hierarchy = this.annotationHierarchy(actual.annotationType());

        if (!hierarchy.contains(targetAnnotationClass)) {
            return null;
        }

        InvocationHandler adapter = new AnnotationAdapterProxy<>(actual, targetAnnotationClass, hierarchy, this);
        Class<?>[] interfaces = { targetAnnotationClass, AnnotationAdapter.class };
        Object proxy = Proxy.newProxyInstance(
                VirtualHierarchyAnnotationLookup.class.getClassLoader(),
                interfaces,
                adapter);

        return targetAnnotationClass.cast(proxy);
    }

    @Override
    public Annotation unproxy(Annotation annotation) {
        if (annotation instanceof AnnotationAdapter) {
            return ((AnnotationAdapter) annotation).actualAnnotation();
        }
        return annotation;
    }

    @Override
    public LinkedHashSet<Class<? extends Annotation>> annotationHierarchy(Class<? extends Annotation> type) {
        Class<? extends Annotation> currentClass = type;
        LinkedHashSet<Class<? extends Annotation>> hierarchy = new LinkedHashSet<>();
        while (currentClass != null) {
            if (!hierarchy.add(currentClass)) {
                throw new CircularHierarchyException(currentClass);
            }
            currentClass = superAnnotationOrNull(currentClass);
        }

        return hierarchy;
    }

    private static Class<? extends Annotation> superAnnotationOrNull(Class<? extends Annotation> currentClass) {
        Extends extendsAnnotation = currentClass.getAnnotation(Extends.class);
        return extendsAnnotation == null ? null : extendsAnnotation.value();
    }
}
