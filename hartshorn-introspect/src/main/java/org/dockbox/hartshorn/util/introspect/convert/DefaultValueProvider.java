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

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A specialized {@link Converter} to handle {@code null} values. This is useful when implementing
 * default values for complex objects.
 *
 * @param <T> the target type
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface DefaultValueProvider<T> extends Converter<Null, T> {

    @Override
    default @Nullable T convert(@Nullable Null input) {
        assert input == null;
        return this.defaultValue();
    }

    /**
     * Returns the default value to use when the input is {@code null}. This method should only
     * be called through {@link #convert(Null)}, and serves purely as a convenience method.
     *
     * @return the default value to use when the input is {@code null}
     */
    @Nullable T defaultValue();
}
