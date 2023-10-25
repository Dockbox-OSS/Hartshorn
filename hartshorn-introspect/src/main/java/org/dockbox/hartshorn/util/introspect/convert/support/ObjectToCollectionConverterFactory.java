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

import java.lang.reflect.Array;
import java.util.Collection;

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

    private record ObjectToCollectionConverter<O extends Collection<?>>(Converter<Object[], O> helperConverter)
            implements Converter<Object, O> {

        @Override
            public O convert(@Nullable Object source) {
                assert source != null;
                Object[] array = (Object[]) Array.newInstance(source.getClass(), 1);
                array[0] = source;
                return this.helperConverter.convert(array);
            }
        }
}
