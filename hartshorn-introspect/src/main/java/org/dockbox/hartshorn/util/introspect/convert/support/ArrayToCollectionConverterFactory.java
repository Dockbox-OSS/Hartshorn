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
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProvider;
import org.dockbox.hartshorn.util.introspect.convert.DefaultValueProviderFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Converts any array to a {@link Collection} of the same type, containing only the objects in the array. The
 * {@link Collection} is created using a {@link CollectionDefaultValueProviderFactory} and supports any {@link
 * Collection} implementation that has a default constructor.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ArrayToCollectionConverterFactory implements ConverterFactory<Object[], Collection<?>> {

    private final DefaultValueProviderFactory<Collection<?>> collectionFactory;

    public ArrayToCollectionConverterFactory(Introspector introspector) {
        this.collectionFactory = new CollectionDefaultValueProviderFactory(introspector).withDefaults();
    }

    @Override
    public <O extends Collection<?>> Converter<Object[], O> create(Class<O> targetType) {
        return new ArrayToCollectionConverter<>(this.collectionFactory.create(targetType));
    }

    /**
     * Converts any array to a {@link Collection} of the same type, containing only the objects in the array. The
     * {@link Collection} is created using the provided {@link DefaultValueProvider}.
     *
     * @param helperProvider the {@link DefaultValueProvider} that is used to create the {@link Collection}
     * @param <O> the type of the {@link Collection}
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    private record ArrayToCollectionConverter<O extends Collection<?>>(DefaultValueProvider<O> helperProvider)
            implements Converter<Object[], O> {

        @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public O convert(Object @Nullable [] source) {
                assert source != null;
                Collection collection = this.helperProvider.defaultValue();
                Objects.requireNonNull(collection).addAll(Arrays.asList(source));
                return (O) collection;
            }
        }
}
