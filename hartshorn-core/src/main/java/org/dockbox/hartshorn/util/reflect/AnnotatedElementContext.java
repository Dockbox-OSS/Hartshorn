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

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.Result;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AnnotatedElementContext is a context that holds the annotations of an annotated element.
 *
 * @param <A> The type of the annotated element
 * @see AnnotatedElement
 * @author Guus Lieben
 * @since 21.5
 */
public abstract class AnnotatedElementContext<A extends AnnotatedElement> extends DefaultContext implements QualifiedElement {

    /**
     * Non-final to allow the default lookup to be replaced with a custom one. As {@link AnnotatedElementContext}
     * is a static context, and not attached to an active {@link org.dockbox.hartshorn.application.context.ApplicationContext},
     * this is the only way to replace the default lookup (as DI is not possible).
     */
    @SuppressWarnings("FieldMayBeFinal")
    private static AnnotationLookup ANNOTATION_LOOKUP = new VirtualHierarchyAnnotationLookup();

    private Map<Class<?>, Annotation> annotationCache;

    /**
     * Returns the annotations of the annotated element.
     *
     * @return The annotations of the annotated element.
     */
    public Set<Annotation> annotations() {
        return new HashSet<>(this.validate().values());
    }

    /**
     * Returns the annotations of the annotated element, when the annotation is annotated with the given
     * annotation.
     * <pre>{@code
     * @ParentAnnotation
     * public @interface MyAnnotation { }
     * }</pre>
     * <pre>{@code
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * <p>The following code will return the annotation {@code @MyAnnotation} of the class {@code MyClass}.
     * <pre>{@code
     * AnnotatedElementContext<MyClass> context = AnnotatedElementContext.of(MyClass.class);
     * Set<Annotation> annotations = context.annotations(MyAnnotation.class);
     * }</pre>
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotations of the annotated element, when the annotation is annotated with the given
     * @see #annotationsWith(Class)
     */
    public <T extends Annotation> Set<Annotation> annotationsWith(final TypeContext<T> annotation) {
        return this.annotationsWith(annotation.type());
    }

    /**
     * Returns the annotations of the annotated element, when the annotation is annotated with the given
     * annotation.
     * <pre>{@code
     * @ParentAnnotation
     * public @interface MyAnnotation { }
     * }</pre>
     * <pre>{@code
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * <p>The following code will return the annotation {@code @MyAnnotation} of the class {@code MyClass}.
     * <pre>{@code
     * AnnotatedElementContext<MyClass> context = AnnotatedElementContext.of(MyClass.class);
     * Set<Annotation> annotations = context.annotations(MyAnnotation.class);
     * }</pre>
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotations of the annotated element, when the annotation is annotated with the given
     * @see #annotationsWith(TypeContext)
     */
    public <T extends Annotation> Set<Annotation> annotationsWith(final Class<T> annotation) {
        return this.annotations().stream()
                .filter(a -> TypeContext.of(a.annotationType()).annotation(annotation).present())
                .collect(Collectors.toSet());
    }

    /**
     * Returns the annotation associated with the given annotation {@link TypeContext}. If the annotation is
     * not present, {@link Result#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotated element associated with the given {@link TypeContext}.
     * @see #annotation(Class)
     */
    public <T extends Annotation> Result<T> annotation(final TypeContext<T> annotation) {
        return this.annotation(annotation.type());
    }

    /**
     * Returns the annotation associated with the given annotation type. If the annotation is not present,
     * {@link Result#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotation associated with the given annotation type.
     * @see #annotation(TypeContext)
     */
    public <T extends Annotation> Result<T> annotation(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Result.empty();

        final Map<Class<?>, Annotation> annotations = this.validate();
        if (annotations.containsKey(annotation))
            return Result.of(() -> (T) annotations.get(annotation));

        final T virtual = ANNOTATION_LOOKUP.find(this.element(), annotation);
        if (virtual != null) annotations.put(annotation, virtual);
        return Result.of(virtual);
    }

    public <T extends Annotation> Set<T> annotations(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Collections.emptySet();
        final List<T> annotations = ANNOTATION_LOOKUP.findAll(this.element(), annotation);
        return Set.copyOf(annotations);
    }

    /**
     * Returns the annotation cache. If the cache is not present, a new cache is created. This method may be
     * overridden to provide a custom cache implementation.
     *
     * @return The annotation cache.
     */
    protected Map<Class<?>, Annotation> validate() {
        if (this.annotationCache == null) {
            this.annotationCache = new ConcurrentHashMap<>();
            for (final Annotation annotation : this.element().getAnnotations()) {
                this.annotationCache.put(annotation.annotationType(), annotation);
            }
        }
        return this.annotationCache;
    }

    /**
     * Returns the annotated element represented by this context.
     *
     * @return The annotated element represented by this context.
     */
    protected abstract A element();

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final AnnotatedElementContext<?> that = (AnnotatedElementContext<?>) o;
        return this.element().equals(that.element());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.element());
    }
}
