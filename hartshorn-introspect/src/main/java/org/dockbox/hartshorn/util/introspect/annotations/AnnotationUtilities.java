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

package org.dockbox.hartshorn.util.introspect.annotations;

import org.dockbox.hartshorn.util.option.Option;

/**
 * Utility class for working with annotations.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class AnnotationUtilities {

    /**
     * Checks if a given type is a stereotype of another type. This method will recursively check the hierarchy of
     * stereotypes to determine if the given type is a stereotype of the provided type.
     *
     * @param type The type to check
     * @param stereotype The stereotype to check against
     * @return {@code true} if the type is a stereotype of the provided stereotype, {@code false} otherwise
     */
    public static boolean isStereotypeOf(Class<?> type, Class<?> stereotype) {
        return Option.of(type.getAnnotation(Extends.class)).test(annotation -> {
            return annotation.value() == stereotype || isStereotypeOf(annotation.value(), stereotype);
        });
    }
}
