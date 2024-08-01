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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.Converter;

/**
 * Converts any object to a {@link String} by invoking {@link String#valueOf(Object)}.
 *
 * @see String#valueOf(Object)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ObjectToStringConverter implements Converter<Object, String> {

    @Override
    public @Nullable String convert(@Nullable Object input) {
        return String.valueOf(input);
    }
}
