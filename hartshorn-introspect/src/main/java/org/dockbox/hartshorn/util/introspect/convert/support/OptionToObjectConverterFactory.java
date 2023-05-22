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

package org.dockbox.hartshorn.util.introspect.convert.support;

import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.option.Option;

public class OptionToObjectConverterFactory implements ConverterFactory<Option<?>, Object>, ConditionalConverter {

    @Override
    public <O> Converter<Option<?>, O> create(final Class<O> targetType) {
        return input -> input.map(result -> (O) result).orNull();
    }

    @Override
    public boolean canConvert(final Object source, final Class<?> targetType) {
        if (source instanceof Option<?> option) {
            final Object value = option.orNull();

            if (value == null) return true;
            return TypeUtils.isAssignable(value.getClass(), targetType);
        }
        return false;
    }
}
