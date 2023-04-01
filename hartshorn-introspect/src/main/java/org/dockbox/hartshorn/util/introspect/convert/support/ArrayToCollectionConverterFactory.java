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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

import java.util.Arrays;
import java.util.Collection;

public class ArrayToCollectionConverterFactory implements ConverterFactory<Object[], Collection<?>> {

    private final CollectionFactory collectionFactory;

    public ArrayToCollectionConverterFactory(final Introspector introspector) {
        this.collectionFactory = new CollectionFactory(introspector);
    }

    @Override
    public <O extends Collection<?>> Converter<Object[], O> create(final Class<O> targetType) {
        return new ArrayToCollectionConverter<>(targetType);
    }

    private class ArrayToCollectionConverter<O extends Collection<?>> implements Converter<Object[], O> {

        private final Class<O> targetType;

        public ArrayToCollectionConverter(final Class<O> targetType) {
            this.targetType = targetType;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public O convert(final Object @Nullable [] source) {
            assert source != null;
            final Class<?> componentType = source.getClass().getComponentType();
            final CollectionFactory collectionFactory = ArrayToCollectionConverterFactory.this.collectionFactory;
            final Collection collection = collectionFactory.createCollection(this.targetType, componentType, source.length);
            collection.addAll(Arrays.asList(source));
            return (O) collection;
        }
    }
}
