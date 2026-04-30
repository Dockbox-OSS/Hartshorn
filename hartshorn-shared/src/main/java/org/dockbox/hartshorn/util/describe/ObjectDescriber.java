/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.util.describe;

import org.dockbox.hartshorn.util.collections.MultiMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.stream.StreamSupport;

/**
 * A utility class to describe objects in a pre-defined style. This class is useful for
 * standardizing the way objects are described in logs or other output.
 *
 * @param <T> the type of the object to describe
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class ObjectDescriber<T> {

    private final T object;
    private final ObjectDescriptionStyle style;
    private final SequencedMap<String, Object> fields = new LinkedHashMap<>();

    private ObjectDescriber(T object, ObjectDescriptionStyle style) {
        this.object = object;
        this.style = style;
    }

    /**
     * Creates a new {@link ObjectDescriber} for the given object, using the default
     * {@link HartshornObjectDescriptionStyle}.
     *
     * @param object the object to describe
     * @param <T> the type of the object to describe
     *
     * @return a new {@link ObjectDescriber} for the given object
     */
    public static <T> ObjectDescriber<T> of(T object) {
        return new ObjectDescriber<>(object, HartshornObjectDescriptionStyle.INSTANCE);
    }

    /**
     * Creates a new {@link ObjectDescriber} for the given object, using the given
     * {@link ObjectDescriptionStyle}.
     *
     * @param object the object to describe
     * @param style the style to use for describing the object
     * @param <T> the type of the object to describe
     *
     * @return a new {@link ObjectDescriber} for the given object
     */
    public static <T> ObjectDescriber<T> of(T object, ObjectDescriptionStyle style) {
        return new ObjectDescriber<>(object, style);
    }

    /**
     * Adds a field to the description of the object. The field will be described using the given
     * name and value.
     *
     * @param name the name of the field
     * @param value the value of the field
     *
     * @return this {@link ObjectDescriber} instance
     */
    public ObjectDescriber<T> field(String name, Object value) {
        this.fields.put(name, value);
        return this;
    }

    /**
     * Describes the object using the style and fields that have been added to this
     * {@link ObjectDescriber}. Always includes the type name of objects in their descriptions (if
     * applicable).
     *
     * @return the description of the object
     */
    public String describe() {
        return this.describe(true);
    }

    /**
     * Describes the object using the style and fields that have been added to this
     * {@link ObjectDescriber}.
     *
     * @param includeTypeName whether the type of the described object should be included in the
     * description
     *
     * @return the description of the object
     */
    public String describe(boolean includeTypeName) {
        StringBuilder builder = new StringBuilder();
        this.style.describeObjectStart(builder, this.object, includeTypeName);

        List<String> fieldNames = List.copyOf(this.fields.sequencedKeySet());
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            Object fieldValue = this.fields.get(fieldName);
            this.style.describeField(
                builder,
                this.object,
                fieldName,
                describeValue(fieldValue, includeTypeName)
            );

            if (i < fieldNames.size() - 1) {
                this.style.describeFieldSeparator(builder, this.object);
            }
        }

        this.style.describeObjectEnd(builder, this.object);
        return builder.toString();
    }

    private String describeArrayLikeValue(Collection<?> elements, boolean includeTypeName) {
        StringBuilder builder = new StringBuilder();
        this.style.describeArrayStart(builder, elements, elements.size(), includeTypeName);
        int i = 0;
        for (Object element : elements) {
            String value = describeValue(element, includeTypeName);
            this.style.describeArrayElement(builder, elements, i, value);
            if (i < elements.size() - 1) {
                this.style.describeArrayElementSeparator(builder, elements, i);
            }
            i++;
        }
        this.style.describeArrayEnd(builder, elements);
        return builder.toString();
    }

    private String describeMapLikeValue(Map<?, ?> map, boolean includeTypeName) {
        ObjectDescriber<Map<?, ?>> describer = ObjectDescriber.of(map, this.style);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = describeValue(entry.getKey(), includeTypeName);
            String value = describeValue(entry.getValue(), includeTypeName);
            describer.field(key, value);
        }
        return describer.describe(includeTypeName);
    }

    private String describeMultiMapLikeValue(MultiMap<?, ?> multiMap, boolean includeTypeName) {
        ObjectDescriber<MultiMap<?, ?>> describer = ObjectDescriber.of(multiMap, this.style);
        for (Map.Entry<?, ? extends Collection<?>> entry : multiMap.entrySet()) {
            String key = describeValue(entry.getKey(), includeTypeName);
            String value = describeArrayLikeValue(entry.getValue(), includeTypeName);
            describer.field(key, value);
        }
        return describer.describe(includeTypeName);
    }

    private String describeValue(Object value, boolean includeTypeName) {
        return switch (value) {
            case null -> "null";
            case String string -> "\"%s\"".formatted(string);
            // DescribeAsObject is a marker interface for objects that should be described using
            // their own toString() method, rather than a custom case below. E.g. BindingHierarchy
            // is an Iterable, but should be described using its own toString() method.
            case DescribeAsObject describeAsObject -> String.valueOf(describeAsObject);
            case Map<?, ?> map -> this.describeMapLikeValue(map, includeTypeName);
            // MultiMap does not extend Map, so we need to handle it separately
            case MultiMap<?, ?> multiMap -> this.describeMultiMapLikeValue(
                multiMap,
                includeTypeName
            );
            case Iterable<?> iterable -> {
                List<?> elements = StreamSupport.stream(iterable.spliterator(), false).toList();
                yield this.describeArrayLikeValue(elements, includeTypeName);
            }
            // Class can have an 'interface' or 'class' prefix, which isn't useful here
            case Class<?> clazz -> clazz.getName();
            default -> {
                if (value.getClass().isArray()) {
                    List<Object> elements = Arrays.stream((Object[]) value).toList();
                    yield this.describeArrayLikeValue(elements, includeTypeName);
                }
                else {
                    // For other objects (including primitives), we simply convert them to a string
                    yield String.valueOf(value);
                }
            }
        };
    }
}
