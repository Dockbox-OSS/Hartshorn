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

import java.util.Collection;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.introspect.convert.ConditionalConverter;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

public class CollectionToObjectConverter implements GenericConverter, ConditionalConverter {

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Collection.class, Object.class));
    }

    @Override
    public boolean canConvert(Object source, Class<?> targetType) {
        return source instanceof Collection<?> collection
                && collection.size() == 1
                && CollectionUtilities.first(collection).getClass().isAssignableFrom(targetType);
    }

    @Override
    public @Nullable <I, O> Object convert(@Nullable Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType) {
        assert source != null;
        Collection<?> collection = (Collection<?>) source;
        return CollectionUtilities.first(collection);
    }
}
