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

package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.AnnotatedElementContext;

import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PropertyAliasIntrospector extends JacksonAnnotationIntrospector {

    private ApplicationContext context;

    @Override
    public PropertyName findNameForSerialization(final Annotated a) {
        return this.findName(a, super::findNameForSerialization);
    }

    @Override
    public PropertyName findNameForDeserialization(final Annotated a) {
        return this.findName(a, super::findNameForDeserialization);
    }

    private PropertyName findName(final Annotated a, final Function<Annotated, PropertyName> defaultValue) {
        final AnnotatedElement annotated = a.getAnnotated();
        if (annotated != null) {
            final AnnotatedElementContext<?> context = new AnnotatedElementContext<>() {
                @Override
                public String qualifiedName() {
                    return a.getName();
                }

                @Override
                protected AnnotatedElement element() {
                    return annotated;
                }
            };

            final Exceptional<Property> annotation = context.annotation(Property.class);
            if (annotation.present()) {
                return new PropertyName(annotation.get().value());
            }
        }
        return defaultValue.apply(a);
    }
}
