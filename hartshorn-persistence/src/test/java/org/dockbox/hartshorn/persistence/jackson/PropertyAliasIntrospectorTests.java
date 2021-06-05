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
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class PropertyAliasIntrospectorTests {

    @Test
    void testPropertyNameForSerialization() throws NoSuchFieldException {
        final Annotated annotated = this.getAnnotated("name");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector();
        final PropertyName name = introspector.findNameForSerialization(annotated);
        final String simpleName = name.getSimpleName();
        Assertions.assertEquals("firstName", simpleName);
    }

    @Test
    void testDefaultNameForSerialization() throws NoSuchFieldException {
        final Annotated annotated = this.getAnnotated("other");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector();
        final PropertyName name = introspector.findNameForSerialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }

    @Test
    void testPropertyNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.getAnnotated("name");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector();
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        final String simpleName = name.getSimpleName();
        Assertions.assertEquals("firstName", simpleName);
    }

    @Test
    void testDefaultNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.getAnnotated("other");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector();
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }

    private Annotated getAnnotated(String name) throws NoSuchFieldException {
        final Field field = SampleElement.class.getDeclaredField(name);
        TypeResolutionContext context = new Empty(TypeFactory.defaultInstance());
        AnnotationMap map = new AnnotationMap();
        for (Annotation annotation : field.getAnnotations()) {
            map.add(annotation);
        }
        return new AnnotatedField(context, field, map);
    }
}
