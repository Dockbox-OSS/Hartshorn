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

package org.dockbox.hartshorn.core.context.element;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 * AnnotatedElementModifier is a helper class that allows to modify an {@link AnnotatedElementContext} by adding or removing
 * virtual annotations. This does not modify the actual {@link AnnotatedElement} itself, but only the context.
 *
 * @param <A> The type of the annotated element represented by the modified {@link AnnotatedElementContext}.
 * @see AnnotatedElement
 * @author Guus Lieben
 * @since 22.1
 */
public final class AnnotatedElementModifier<A extends AnnotatedElement> implements ElementModifier<AnnotatedElementContext<A>> {

    private final AnnotatedElementContext<A> element;

    private AnnotatedElementModifier(final AnnotatedElementContext<A> element) {
        this.element = element;
    }

    public AnnotatedElementContext<A> element() {
        return this.element;
    }

    public static <A extends AnnotatedElement> AnnotatedElementModifier<A> of(final AnnotatedElementContext<A> element) {
        return new AnnotatedElementModifier<>(element);
    }

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
