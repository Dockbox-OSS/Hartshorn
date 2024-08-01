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

import java.lang.reflect.Array;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.convert.Converter;
import org.dockbox.hartshorn.util.introspect.convert.ConverterFactory;

/**
 * Converts an object to a {@link Collection}. If the object is {@code null}, an empty collection is returned.
 * Otherwise, the object is wrapped in a collection.
 *
 * <p>This converter delegates to a {@link ArrayToCollectionConverterFactory} to convert arrays to collections.
 * This converter only creates an array of length 1 and delegates to the helper converter.
 *
 * @see ArrayToCollectionConverterFactory
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ObjectToCollectionConverterFactory implements ConverterFactory<Object, Collection<?>> {

    private final ConverterFactory<Object[], Collection<?>> helperConverterFactory;

    public ObjectToCollectionConverterFactory(Introspector introspector) {
        this(new ArrayToCollectionConverterFactory(introspector));
    }

    public ObjectToCollectionConverterFactory(ConverterFactory<Object[], Collection<?>> helperConverterFactory) {
        this.helperConverterFactory = helperConverterFactory;
    }

    @Override
    public <O extends Collection<?>> Converter<Object, O> create(Class<O> targetType) {
        Converter<Object[], O> converter = ObjectToCollectionConverterFactory.this.helperConverterFactory.create(targetType);
        return new ObjectToCollectionConverter<>(converter);
    }

    /**
     * Converts an object to a {@link Collection}. If the object is {@code null}, an empty collection is returned.
     * Otherwise, the object is wrapped in a collection.
     *
     * @param helperConverter the converter that is used to convert an array to a collection
     * @param <O> the type of the collection
     *
     * @since 0.5.0
     *
     * @author Guus Lieben
     */
    private record ObjectToCollectionConverter<O extends Collection<?>>(
            Converter<Object[], O> helperConverter
    ) implements Converter<Object, O> {

        @Override
            public O convert(@Nullable Object source) {
                Object[] array;
                if (source != null) {
                    array = (Object[]) Array.newInstance(source.getClass(), 1);
                    array[0] = source;
                }
                else {
                    array = new Object[0];
                }
                return this.helperConverter.convert(array);
            }
        }
}
