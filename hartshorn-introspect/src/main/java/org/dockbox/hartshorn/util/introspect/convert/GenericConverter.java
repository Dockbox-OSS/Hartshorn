package org.dockbox.hartshorn.util.introspect.convert;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

public interface GenericConverter {

    Set<ConvertibleTypePair> convertibleTypes();

    <I, O> @Nullable Object convert(@NonNull Object source, @NonNull Class<I> sourceType, @NonNull Class<O> targetType);

}
