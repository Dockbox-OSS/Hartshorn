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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

public class CollectionToArrayConverter implements GenericConverter {

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Collection.class, Object[].class));
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable final Object source, @NonNull final Class<I> sourceType, @NonNull final Class<O> targetType) {
        assert source != null;
        assert targetType.isArray();

        final Collection<?> collection = (Collection<?>) source;
        final Class<?> arrayType = targetType.componentType();
        //noinspection unchecked
        final O array = (O) Array.newInstance(arrayType, collection.size());
        return collection.toArray((Object[]) array);
    }
}
