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

package org.dockbox.hartshorn.util.introspect.convert;

/**
 * A pair of types that can be converted between. This is used as a key in maps of converters.
 *
 * @param sourceType the source type
 * @param targetType the target type
 *
 * @see GenericConverter#convertibleTypes()
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public record ConvertibleTypePair(Class<?> sourceType, Class<?> targetType) {

    public ConvertibleTypePair {
        if (sourceType == null) {
            throw new IllegalArgumentException("Source type must not be null");
        }
        if (targetType == null) {
            throw new IllegalArgumentException("Target type must not be null");
        }
    }

    /**
     * Create a new null-safe {@link ConvertibleTypePair} from the given source and target type. Null-safe
     * in this context means that any given {@code null} source type will be replaced with {@link Null#TYPE}.
     *
     * @param sourceType the source type
     * @param targetType the target type
     * @return a new {@link ConvertibleTypePair} instance
     */
    public static ConvertibleTypePair of(Class<?> sourceType, Class<?> targetType) {
        // Default value providers take 'Null' (null) as input, so we need to ensure we consistently use Null.TYPE
        // in the ConvertibleTypePair to avoid unnecessary conversions.
        if (sourceType == null || sourceType == void.class || sourceType == Void.class) {
            return new ConvertibleTypePair(Null.TYPE, targetType);
        }
        return new ConvertibleTypePair(sourceType, targetType);
    }
}
