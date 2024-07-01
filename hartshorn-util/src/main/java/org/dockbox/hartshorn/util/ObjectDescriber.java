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

public final class ObjectDescriber<T> {

    private final T object;
    private final ObjectDescriptionStyle style;
    private final SequencedMap<String, Object> fields = new LinkedHashMap<>();

    private ObjectDescriber(T object, ObjectDescriptionStyle style) {
        this.object = object;
        this.style = style;
    }

    public static <T> ObjectDescriber<T> of(T object) {
        return new ObjectDescriber<>(object, HartshornObjectDescriptionStyle.INSTANCE);
    }

    public static <T> ObjectDescriber<T> of(T object, ObjectDescriptionStyle style) {
        return new ObjectDescriber<>(object, style);
    }

    public ObjectDescriber<T> field(String name, Object value) {
        this.fields.put(name, value);
        return this;
    }

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
