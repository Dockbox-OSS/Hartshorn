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

import java.util.Collection;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Converts an {@link Option} to a {@link Collection}. If the {@link Option} is empty, an empty {@link Collection} is
 * returned. Otherwise, a {@link Collection} containing the value of the {@link Option} is returned.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class OptionToCollectionConverterFactory implements ConverterFactory<Option<?>, Collection<?>> {

    private final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory;

    public OptionToCollectionConverterFactory(Introspector introspector) {
        this(new CollectionDefaultValueProviderFactory(introspector).withDefaults());
    }

    public OptionToCollectionConverterFactory(DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory) {
        this.defaultValueProviderFactory = defaultValueProviderFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Option<?>, O> create(Class<O> targetType) {
        return input -> {
            //noinspection unchecked
            Collection<Object> collection = (Collection<Object>) this.defaultValueProviderFactory.create(targetType).defaultValue();
            input.peek(collection::add);
            return targetType.cast(collection);
        };
    }
}
