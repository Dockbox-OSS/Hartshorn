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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.AnnotationHelper;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AnnotatedElementContext is a context that holds the annotations of an annotated element.
 *
 * <p>The context is a map of annotation type to annotation instance. The context is thread-safe.
 *
 * @param <A> The type of the annotated element
 * @author Guus Lieben
 * @since 21.5
 */
public abstract class AnnotatedElementContext<A extends AnnotatedElement> extends DefaultContext implements QualifiedElement {

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
     * @see #annotations(Class)
     */
    public <T extends Annotation> Set<Annotation> annotations(final TypeContext<T> annotation) {
        return this.annotations(annotation.type());
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
     * @see #annotations(TypeContext)
     */
    public <T extends Annotation> Set<Annotation> annotations(final Class<T> annotation) {
        return this.annotations().stream()
                .filter(a -> TypeContext.of(a.annotationType()).annotation(annotation).present())
                .collect(Collectors.toSet());
    }

    /**
     * Returns the annotation associated with the given annotation {@link TypeContext}. If the annotation is
     * not present, {@link Exceptional#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotated element associated with the given {@link TypeContext}.
     * @see #annotation(Class)
     */
    public <T extends Annotation> Exceptional<T> annotation(final TypeContext<T> annotation) {
        return this.annotation(annotation.type());
    }

    /**
     * Returns the annotation associated with the given annotation type. If the annotation is not present,
     * {@link Exceptional#empty()} is returned.
     *
     * @param annotation The annotation type
     * @param <T> The type of the annotation
     * @return The annotation associated with the given annotation type.
     * @see #annotation(TypeContext)
     */
    public <T extends Annotation> Exceptional<T> annotation(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return Exceptional.empty();

        final Map<Class<?>, Annotation> annotations = this.validate();
        if (annotations.containsKey(annotation))
            return Exceptional.of(() -> (T) annotations.get(annotation));

        final T oneOrNull = AnnotationHelper.oneOrNull(this.element(), annotation);
        if (oneOrNull != null) annotations.put(annotation, oneOrNull);
        return Exceptional.of(oneOrNull);
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
