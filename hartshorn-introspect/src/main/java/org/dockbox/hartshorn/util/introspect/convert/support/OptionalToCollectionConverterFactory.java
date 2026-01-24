/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Collection;
import java.util.Optional;

/**
 * Converts an {@link Optional} to a {@link Collection}. If the {@link Optional} is empty, the
 * resulting {@link Collection} will be empty. If the {@link Optional} contains a value, the
 * resulting {@link Collection} will contain that value.
 *
 * <p>This converter is implemented as a composition of {@link OptionalToOptionConverter} and
 * {@link OptionToCollectionConverterFactory}.
 *
 * @param helperOptionalToOptionConverter The helper {@link Converter} to convert from
 * {@link Optional} to {@link Option}
 * @param helperOptionToCollectionConverterFactory The helper {@link ConverterFactory} to convert
 * from {@link Option} to {@link Collection}
 *
 * @see OptionalToOptionConverter
 * @see OptionToCollectionConverterFactory
 *
 * @since 0.5.0
 * 
 * @author Guus Lieben
 */
public record OptionalToCollectionConverterFactory(
    Converter<Optional<?>, Option<?>> helperOptionalToOptionConverter,
    ConverterFactory<Option<?>, Collection<?>> helperOptionToCollectionConverterFactory
) implements ConverterFactory<Optional<?>, Collection<?>> {

    public OptionalToCollectionConverterFactory(Introspector introspector) {
        this(new OptionalToOptionConverter(), new OptionToCollectionConverterFactory(introspector));
    }

    @Override
    public <O extends Collection<?>> Converter<Optional<?>, O> create(Class<O> targetType) {
        Converter<Option<?>, O> optionToCollectionConverter = this
            .helperOptionToCollectionConverterFactory.create(targetType);
        return (input, _) -> {
            Option<?> option = this.helperOptionalToOptionConverter.convert(input);
            return optionToCollectionConverter.convert(option);
        };
    }
}
