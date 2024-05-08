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
import java.util.Objects;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;

/**
 * Converts a {@link Collection} to another {@link Collection} by adding all elements of the source collection to the
 * target collection. The target collection is created using the {@link DefaultValueProvider} of the target type.
 *
 * @see CollectionDefaultValueProviderFactory
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class CollectionToCollectionConverterFactory implements ConverterFactory<Collection<?>, Collection<?>> {

    private final DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory;

    public CollectionToCollectionConverterFactory(Introspector introspector) {
        this(new CollectionDefaultValueProviderFactory(introspector).withDefaults());
    }

    public CollectionToCollectionConverterFactory(DefaultValueProviderFactory<Collection<?>> defaultValueProviderFactory) {
        this.defaultValueProviderFactory = defaultValueProviderFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Collection<?>, O> create(Class<O> targetType) {
        return new CollectionToCollectionConverter<>(this.defaultValueProviderFactory.create(targetType), targetType);
    }

    /**
     * Converts a {@link Collection} to another {@link Collection} by adding all elements of the source collection to the
     * target collection. The target collection is created using the given {@link DefaultValueProvider} for the target type.
     *
     * @param <O> the target type
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    public static class CollectionToCollectionConverter<O extends Collection<?>> implements Converter<Collection<?>, O> {

        private final DefaultValueProvider<O> defaultValueProvider;
        private final Class<O> targetType;

        public CollectionToCollectionConverter(DefaultValueProvider<O> defaultValueProvider, Class<O> targetType) {
            this.defaultValueProvider = defaultValueProvider;
            this.targetType = targetType;
        }

        @Override
        public O convert(Collection<?> source) {
            //noinspection unchecked
            Collection<Object> collection = (Collection<Object>) this.defaultValueProvider.defaultValue();
            Objects.requireNonNull(collection).addAll(source);
            return this.targetType.cast(collection);
        }
    }
}
