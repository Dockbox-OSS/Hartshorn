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

import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.AnnotationHelper;
import org.dockbox.hartshorn.core.context.DefaultContext;
import org.dockbox.hartshorn.core.HartshornUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AnnotatedElementContext<A extends AnnotatedElement> extends DefaultContext implements QualifiedElement {

    private Map<Class<?>, Annotation> annotations;

    public List<Annotation> annotations() {
        this.validate();
        return HartshornUtils.asUnmodifiableList(this.annotations.values());
    }

    public <T extends Annotation> Exceptional<T> annotation(final Class<T> annotation) {
        this.validate();
        if (this.annotations.containsKey(annotation))
            return Exceptional.of(() -> (T) this.annotations.get(annotation));

        final T oneOrNull = AnnotationHelper.oneOrNull(this.element(), annotation);
        if (oneOrNull != null) this.annotations.put(annotation, oneOrNull);
        return Exceptional.of(oneOrNull);
    }

    private void validate() {
        if (this.annotations == null) {
            this.annotations = HartshornUtils.emptyConcurrentMap();
            for (final Annotation annotation : this.element().getAnnotations()) {
                this.annotations.put(annotation.getClass(), annotation);
            }
        }
    }

    protected abstract A element();
}
