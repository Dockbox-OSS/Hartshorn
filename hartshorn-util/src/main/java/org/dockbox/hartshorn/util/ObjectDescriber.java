/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

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
     * Creates a new {@link ObjectDescriber} for the given object, using the default {@link
     * HartshornObjectDescriptionStyle}.
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
     * Creates a new {@link ObjectDescriber} for the given object, using the given {@link
     * ObjectDescriptionStyle}.
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
     * Describes the object using the style and fields that have been added to this {@link
     * ObjectDescriber}.
     *
     * @return the description of the object
     */
    public String describe() {
        StringBuilder builder = new StringBuilder();
        this.style.describeStart(builder, this.object);

        List<String> fieldNames = List.copyOf(fields.sequencedKeySet());
        for(int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            this.style.describeField(builder, this.object, fieldName, this.fields.get(fieldName));

            if(i < fieldNames.size() - 1) {
                this.style.describeFieldSeparator(builder, this.object);
            }
        }

        this.style.describeEnd(builder, this.object);
        return builder.toString();
    }
}
