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
