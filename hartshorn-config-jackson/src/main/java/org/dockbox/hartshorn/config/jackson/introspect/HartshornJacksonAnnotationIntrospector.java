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

package org.dockbox.hartshorn.config.jackson.introspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.dockbox.hartshorn.config.annotations.IgnoreProperty;
import org.dockbox.hartshorn.config.jackson.JacksonIntrospectionException;
import org.dockbox.hartshorn.util.introspect.annotations.Property;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;

/**
 * A {@link JacksonAnnotationIntrospector} that supports the {@link Property} and {@link IgnoreProperty} annotations.
 * This introspector is used by the {@link org.dockbox.hartshorn.config.jackson.StandardJacksonObjectMapperConfigurator}
 * to configure new {@link com.fasterxml.jackson.databind.ObjectMapper} instances.
 *
 * @see Property
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public class HartshornJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    private final Introspector introspector;

    public HartshornJacksonAnnotationIntrospector(Introspector introspector) {
        this.introspector = introspector;
    }

    @Override
    public JavaType refineSerializationType(MapperConfig<?> config, Annotated annotated, JavaType baseType) throws JsonMappingException {
        if (annotated.hasAnnotation(Property.class)) {
            Property property = annotated.getAnnotation(Property.class);
            TypeFactory typeFactory = config.getTypeFactory();
            JavaType type = baseType;

            // Property type
            Class<?> valueClass = this._classIfExplicit(property.type());
            if (valueClass != null) {
                if (type.hasRawClass(valueClass)) {
                    type = type.withStaticTyping();
                }
                else {
                    type = this.refineAssignableType(typeFactory, type, valueClass);
                }
            }

            // Key type - for container-like types
            if (type.isMapLikeType()) {
                JavaType keyType = type.getKeyType();
                Class<?> keyClass = this._classIfExplicit(property.key());
                if (keyClass != null) {
                    if (keyType.hasRawClass(keyClass)) {
                        type = type.withStaticTyping();
                    }
                    else {
                        keyType = this.refineAssignableType(typeFactory, keyType, keyClass);
                    }
                    type = ((MapLikeType) type).withKeyType(keyType);
                }
            }

            // Content type - for container-like types
            JavaType contentType = type.getContentType();
            if (contentType != null) {
                Class<?> contentClass = this._classIfExplicit(property.content());
                if (contentClass != null) {
                    if (contentType.hasRawClass(contentClass)) {
                        contentType = contentType.withStaticTyping();
                    }
                    else {
                        contentType = this.refineAssignableType(typeFactory, contentType, contentClass);
                    }
                    type = type.withContentType(contentType);
                }
            }

            return type;
        }
        return super.refineSerializationType(config, annotated, baseType);
    }

    private JavaType refineAssignableType(TypeFactory typeFactory, JavaType javaType, Class<?> type) throws JacksonIntrospectionException {
        Class<?> rawClass = javaType.getRawClass();
        if (type.isAssignableFrom(rawClass)) {
            return typeFactory.constructGeneralizedType(javaType, type);
        }
        else if (rawClass.isAssignableFrom(type)) {
            return typeFactory.constructSpecializedType(javaType, type);
        }
        else {
            throw new JacksonIntrospectionException(null, "Cannot refine property content type from " + rawClass.getName() + " to " + type.getName());
        }
    }

    @Override
    public JavaType refineDeserializationType(MapperConfig<?> config, Annotated annotated, JavaType baseType) throws JsonMappingException {
        if (annotated.hasAnnotation(Property.class)) {
            Property property = annotated.getAnnotation(Property.class);
            TypeFactory typeFactory = config.getTypeFactory();
            JavaType type = baseType;

            // Property type
            Class<?> valueClass = this._classIfExplicit(property.type());
            if (valueClass != null && !type.hasRawClass(valueClass)) {
                type = typeFactory.constructSpecializedType(type, valueClass);
            }

            // Key type - for container-like types
            if (type.isMapLikeType()) {
                JavaType keyType = type.getKeyType();
                // Filters void types, including default used in RefineProperty
                Class<?> keyClass = this._classIfExplicit(property.key());
                if (keyClass != null) {
                    JavaType specializedType = typeFactory.constructSpecializedType(keyType, keyClass);
                    type = ((MapLikeType) type).withKeyType(specializedType);
                }
            }

            // Content type - for container-like types
            JavaType contentType = type.getContentType();
            if (contentType != null) {
                // Filters void types, including default used in RefineProperty
                Class<?> contentClass = this._classIfExplicit(property.content());
                if (contentClass != null) {
                    JavaType specializedType = typeFactory.constructSpecializedType(contentType, contentClass);
                    type = type.withContentType(specializedType);
                }
            }

            return type;
        }
        return super.refineDeserializationType(config, annotated, baseType);
    }

    @Override
    public PropertyName findNameForSerialization(Annotated annotated) {
        return this.findName(annotated, super::findNameForSerialization);
    }

    @Override
    public PropertyName findNameForDeserialization(Annotated annotated) {
        return this.findName(annotated, super::findNameForDeserialization);
    }

    private PropertyName findName(Annotated annotated, Function<Annotated, PropertyName> defaultValue) {
        AnnotatedElement element = annotated.getAnnotated();
        if (element != null) {
            ElementAnnotationsIntrospector introspector = this.introspector.introspect(element);
            Option<Property> annotation = introspector.get(Property.class);
            if (annotation.present()) {
                return new PropertyName(annotation.get().name());
            }
        }
        return defaultValue.apply(annotated);
    }

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        if (this.introspector.introspect(member.getAnnotated()).has(IgnoreProperty.class)) {
            return true;
        }
        return super.hasIgnoreMarker(member);
    }
}
