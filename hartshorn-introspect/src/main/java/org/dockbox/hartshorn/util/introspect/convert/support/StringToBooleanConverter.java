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
 * Converts a {@link String} to a {@link Boolean}.
 *
 * @see Boolean#parseBoolean(String)
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class StringToBooleanConverter implements Converter<String, Boolean> {

    @Override
    public Boolean convert(@Nullable String input) {
        if (input != null) {
            return Boolean.parseBoolean(input);
        }
        return null;
    }
}
