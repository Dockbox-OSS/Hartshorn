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

package org.dockbox.hartshorn.data.jackson;

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
        final Annotated annotated = this.annotated("name");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector(null);
        final PropertyName name = introspector.findNameForSerialization(annotated);
        final String simpleName = name.getSimpleName();
        Assertions.assertEquals("firstName", simpleName);
    }

    private Annotated annotated(final String name) throws NoSuchFieldException {
        final Field field = SampleElement.class.getDeclaredField(name);
        final TypeResolutionContext context = new Empty(TypeFactory.defaultInstance());
        final AnnotationMap map = new AnnotationMap();
        for (final Annotation annotation : field.getAnnotations()) {
            map.add(annotation);
        }
        return new AnnotatedField(context, field, map);
    }

    @Test
    void testDefaultNameForSerialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("other");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector(null);
        final PropertyName name = introspector.findNameForSerialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }

    @Test
    void testPropertyNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("name");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector(null);
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        final String simpleName = name.getSimpleName();
        Assertions.assertEquals("firstName", simpleName);
    }

    @Test
    void testDefaultNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("other");
        final PropertyAliasIntrospector introspector = new PropertyAliasIntrospector(null);
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }
}
