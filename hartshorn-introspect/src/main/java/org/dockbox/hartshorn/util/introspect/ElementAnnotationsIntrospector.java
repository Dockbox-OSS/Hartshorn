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

package org.dockbox.hartshorn.util.introspect;

import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * An introspector to provide access to the annotations of an element. this is typically provided
 * by a {@link org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView} implementation.
 *
 * <p>Implementations may choose to support virtual annotation hierarchies following the {@link
 * org.dockbox.hartshorn.util.introspect.annotations.VirtualHierarchyAnnotationLookup}. This allows
 * for meta-annotations to be used to annotate annotations, and have those meta-annotations be
 * considered when searching for annotations.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ElementAnnotationsIntrospector {

    /**
     * Returns all annotations that are present on the element.
     *
     * @return all annotations that are present on the element
     */
    Set<Annotation> all();

    /**
     * Returns all annotations that are present on the element, and that
     * are annotated with the provided annotation.
     *
     * @param annotation the annotation to find annotations for
     * @return all annotations that are annotated with the provided annotation
     */
    Set<Annotation> annotedWith(Class<? extends Annotation> annotation);

    /**
     * Returns whether the element is annotated with the provided annotation. If the implementation
     * of this introspector supports virtual annotation hierarchies, the annotation hierarchy is
     * considered when searching for the annotation.
     *
     * @param annotation the annotation to check for
     * @return {@code true} if the element is annotated with the provided annotation, {@code false} otherwise
     */
    boolean has(Class<? extends Annotation> annotation);

    /**
     * Returns whether the element is annotated with any of the provided annotations. If the
     * implementation of this introspector supports virtual annotation hierarchies, the annotation
     * hierarchy is considered when searching for the annotations.
     *
     * @param annotations the annotations to check for
     * @return {@code true} if the element is annotated with any of the provided annotations, {@code false} otherwise
     */
    default boolean hasAny(Class<? extends Annotation>... annotations) {
        return this.hasAny(Set.of(annotations));
    }

    /**
     * Returns whether the element is annotated with any of the provided annotations. If the
     * implementation of this introspector supports virtual annotation hierarchies, the annotation
     * hierarchy is considered when searching for the annotations.
     *
     * @param annotations the annotations to check for
     * @return {@code true} if the element is annotated with any of the provided annotations, {@code false} otherwise
     */
    boolean hasAny(Set<Class<? extends Annotation>> annotations);

    /**
     * Returns whether the element is annotated with all the provided annotations. If the
     * implementation of this introspector supports virtual annotation hierarchies, the annotation
     * hierarchy is considered when searching for the annotations.
     *
     * @param annotations the annotations to check for
     * @return {@code true} if the element is annotated with all the provided annotations, {@code false} otherwise
     */
    default boolean hasAll(Class<? extends Annotation>... annotations) {
        return this.hasAll(Set.of(annotations));
    }

    /**
     * Returns whether the element is annotated with all the provided annotations. If the
     * implementation of this introspector supports virtual annotation hierarchies, the annotation
     * hierarchy is considered when searching for the annotations.
     *
     * @param annotations the annotations to check for
     * @return {@code true} if the element is annotated with all the provided annotations, {@code false} otherwise
     */
    boolean hasAll(Set<Class<? extends Annotation>> annotations);

    /**
     * Returns the instance of the annotation of the provided type, if present on the element. If
     * the implementation of this introspector supports virtual annotation hierarchies, the
     * annotation hierarchy is considered when searching for the annotation.
     *
     * @param annotation the annotation to find
     * @return the instance of the annotation of the provided type, if present on the element
     * @param <T> the type of the annotation
     */
    <T extends Annotation> Option<T> get(Class<T> annotation);

    /**
     * Returns all instances of the annotation of the provided type, if present on the element. If
     * the implementation of this introspector supports virtual annotation hierarchies, the
     * annotation hierarchy is considered when searching for the annotation.
     *
     * @param annotation the annotation to find
     * @return all instances of the annotation of the provided type, if present on the element
     * @param <T> the type of the annotation
     */
    <T extends Annotation> Set<T> all(Class<T> annotation);

    /**
     * Returns the number of annotations that are present on the element.
     *
     * @return the number of annotations that are present on the element
     */
    int count();
}
