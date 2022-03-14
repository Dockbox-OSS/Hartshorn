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
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import org.dockbox.hartshorn.core.annotations.Property;
import org.dockbox.hartshorn.core.context.element.AnnotatedElementContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

public class PropertyAliasIntrospector extends JacksonAnnotationIntrospector {

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
