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

package test.org.dockbox.hartshorn.config.jackson;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotationMap;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.dockbox.hartshorn.config.jackson.introspect.HartshornJacksonAnnotationIntrospector;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import jakarta.inject.Inject;

@HartshornTest(includeBasePackages = false)
public class PropertyAliasIntrospectorTests {

    @Inject
    private Introspector introspector;

    @Test
    void testPropertyNameForSerialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("name");
        final AnnotationIntrospector introspector = new HartshornJacksonAnnotationIntrospector(this.introspector);
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
        final AnnotationIntrospector introspector = new HartshornJacksonAnnotationIntrospector(this.introspector);
        final PropertyName name = introspector.findNameForSerialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }

    @Test
    void testPropertyNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("name");
        final AnnotationIntrospector introspector = new HartshornJacksonAnnotationIntrospector(this.introspector);
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        final String simpleName = name.getSimpleName();
        Assertions.assertEquals("firstName", simpleName);
    }

    @Test
    void testDefaultNameForDeserialization() throws NoSuchFieldException {
        final Annotated annotated = this.annotated("other");
        final AnnotationIntrospector introspector = new HartshornJacksonAnnotationIntrospector(this.introspector);
        final PropertyName name = introspector.findNameForDeserialization(annotated);
        // No explicit property name defined
        Assertions.assertNull(name);
    }
}
