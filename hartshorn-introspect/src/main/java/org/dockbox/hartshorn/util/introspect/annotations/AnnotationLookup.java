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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;

/**
 * Look-up utility to find annotations on a given {@link AnnotatedElement}, and to construct virtual
 * hierarchies of annotations. This is useful for cases where an annotation is not directly present
 * on an element, but an extension of it is.
 *
 * <p>An annotation extends another annotation if it is annotated with {@link Extends}. This is
 * commonly used for component stereotypes, where a stereotype is defined as an annotation that
 * extends another annotation.
 *
 * @see Extends
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface AnnotationLookup {

    /**
     * Returns the annotation of the given type on the given element, or {@code null} if it is not
     * present. If there are multiple composite annotations matching the given type, a
     * {@link DuplicateAnnotationCompositeException} is thrown, as this is not allowed. Multiple
     * composites cannot be safely preferred or deferred, risking the loss of information.
     *
     * @param element The element to find the annotation on
     * @param annotationType The type of the annotation to find
     * @return The annotation of the given type on the given element, or {@code null} if it is not present.
     * @param <A> The type of the annotation
     * @throws DuplicateAnnotationCompositeException If there are multiple composite annotations matching the given type
     */
    <A extends Annotation> A find(AnnotatedElement element, Class<A> annotationType) throws DuplicateAnnotationCompositeException;

    /**
     * Returns all (composite) annotations of the given type on the given element, or an empty list if
     * none are present. Unlike {@link #find(AnnotatedElement, Class)}, this method will return all
     * composite annotations of the given type, even if there are multiple. This will never yield a
     * {@link DuplicateAnnotationCompositeException}, as duplicates are allowed.
     *
     * @param element The element to find the annotations on
     * @param annotationType The type of the annotations to find
     * @return All (composite) annotations of the given type on the given element, or an empty list if none are present.
     * @param <A> The type of the annotation
     */
    <A extends Annotation> List<A> findAll(AnnotatedElement element, Class<A> annotationType);

    /**
     * Returns the backing {@link Annotation} of a given {@link Annotation}. If the given annotation
     * is not a composite annotation, it is returned as-is. If it is a composite annotation, the
     * underlying base annotation is returned if it exists. Typically, this indicates the annotation
     * is a proxy instance, though this is not required and can be decided on by the implementation of
     * this interface.
     *
     * @param annotation The annotation to get the backing annotation of
     * @return The backing annotation of the given annotation
     */
    Annotation unproxy(Annotation annotation);

    /**
     * Returns the linear hierarchy of the given annotation. This is the list of annotations which the
     * given annotation extends, in order of precedence. The first element in the list is the given
     * annotation, and the last element is the base annotation. As annotations can only extend one
     * other annotation, the last element is always the base annotation.
     *
     * @param type The annotation to get the hierarchy of
     * @return The linear hierarchy of the given annotation
     */
    SequencedSet<Class<? extends Annotation>> annotationHierarchy(Class<? extends Annotation> type);
}
