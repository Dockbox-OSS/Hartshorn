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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ReflectionElementAnnotationsIntrospector implements ElementAnnotationsIntrospector {

    private final Introspector introspector;
    private final AnnotatedElement element;
    private final AnnotationLookup annotationLookup;
    private Map<Class<?>, Annotation> annotationCache;

    public ReflectionElementAnnotationsIntrospector(final ReflectionIntrospector introspector, final AnnotatedElement element) {
        this.introspector = introspector;
        this.element = element;
        // Could be instantiated early during application startup, so we don't want to use component provision here. This
        // does limit it to only being able to be overridden through the application factory.
        this.annotationLookup = introspector.annotationLookup();
    }

    protected Map<Class<?>, Annotation> annotationCache() {
        if (this.annotationCache == null) {
            this.annotationCache = new ConcurrentHashMap<>();
            for (final Annotation annotation : this.element.getAnnotations()) {
                this.annotationCache.put(annotation.annotationType(), annotation);
            }
        }
        return this.annotationCache;
    }

    @Override
    public Set<Annotation> all() {
        return Set.copyOf(this.annotationCache().values());
    }

    @Override
    public Set<Annotation> annotedWith(final Class<? extends Annotation> annotation) {
        return this.all().stream()
                .filter(presentAnnotation -> this.introspector.introspect(presentAnnotation.annotationType()).annotations().has(annotation))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean has(final Class<? extends Annotation> annotation) {
        return this.get(annotation).present();
    }

    @Override
    public boolean hasAny(final Set<Class<? extends Annotation>> annotations) {
        return annotations.stream().anyMatch(this::has);
    }

    @Override
    public boolean hasAll(final Set<Class<? extends Annotation>> annotations) {
        return annotations.stream().allMatch(this::has);
    }

    @Override
    public <T extends Annotation> Option<T> get(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Option.empty();

        final Map<Class<?>, Annotation> annotations = this.annotationCache();
        if (annotations.containsKey(annotation))
            return Option.of(() -> annotation.cast(annotations.get(annotation)));

        final T virtual = this.annotationLookup.find(this.element, annotation);
        if (virtual != null) annotations.put(annotation, virtual);
        return Option.of(virtual);
    }

    @Override
    public <T extends Annotation> Set<T> all(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Collections.emptySet();
        final List<T> annotations = this.annotationLookup.findAll(this.element, annotation);
        return Set.copyOf(annotations);
    }

    @Override
    public int count() {
        return this.annotationCache().size();
    }
}
