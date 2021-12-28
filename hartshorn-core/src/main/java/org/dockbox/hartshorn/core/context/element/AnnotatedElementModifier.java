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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AnnotatedElementModifier is a helper class that allows to modify an {@link AnnotatedElementContext} by adding or removing
 * virtual annotations. This does not modify the actual {@link AnnotatedElement} itself, but only the context.
 *
 * @param <A> The type of the annotated element represented by the modified {@link AnnotatedElementContext}.
 *
 * @author Guus Lieben
 * @since 22.1
 */
@AllArgsConstructor(staticName = "of")
public final class AnnotatedElementModifier<A extends AnnotatedElement> implements ElementModifier<AnnotatedElementContext<A>> {

    @Getter
    private final AnnotatedElementContext<A> element;

    /**
     * Adds a virtual annotation to the {@link AnnotatedElementContext}.
     *
     * @param annotation The annotation to add.
     * @param <T> The type of the annotation.
     */
    public <T extends Annotation> void add(final T annotation) {
        if (annotation == null) return;
        this.element().validate().put(annotation.annotationType(), annotation);
    }

    /**
     * Removes a virtual annotation from the {@link AnnotatedElementContext}. This may either be a virtual annotation which was
     * added by {@link #add(Annotation)} or a real annotation which was already present on the {@link AnnotatedElement}.
     *
     * @param annotation The annotation to remove.
     * @param <T> The type of the annotation.
     */
    public <T extends Annotation> void remove(final Class<T> annotation) {
        if (!annotation.isAnnotation()) return;
        this.element().validate().remove(annotation);
    }
}
