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

package org.dockbox.hartshorn.persistence.jackson;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.entity.annotations.Property;
import org.dockbox.hartshorn.util.Reflect;

import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public class PropertyAliasIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public PropertyName findNameForSerialization(Annotated a) {
        return this.findName(a, super::findNameForSerialization);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated a) {
        return this.findName(a, super::findNameForDeserialization);
    }

    private PropertyName findName(Annotated a, Function<Annotated, PropertyName> defaultValue) {
        final AnnotatedElement annotated = a.getAnnotated();
        
        if (annotated != null) {
            final Exceptional<Property> annotation = Reflect.annotation(annotated, Property.class);
            if (annotation.present()) {
                return new PropertyName(annotation.get().value());
            }
        }
        return defaultValue.apply(a);
    }
}
