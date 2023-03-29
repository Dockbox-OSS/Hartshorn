package org.dockbox.hartshorn.util.introspect.convert.support;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.util.introspect.convert.ConversionService;
import org.dockbox.hartshorn.util.introspect.convert.ConvertibleTypePair;
import org.dockbox.hartshorn.util.introspect.convert.GenericConverter;

import java.lang.reflect.Array;
import java.util.Set;

public class ArrayToObjectConverter implements GenericConverter {

    private final ConversionService conversionService;

    public ArrayToObjectConverter(final ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public Set<ConvertibleTypePair> convertibleTypes() {
        return Set.of(ConvertibleTypePair.of(Object[].class, Object.class));
    }

    @Override
    public @Nullable <I, O> Object convert(final @Nullable Object source, final @NonNull Class<I> sourceType, final @NonNull Class<O> targetType) {
        if (sourceType.isArray()) {
            final Class<?> componentType = sourceType.getComponentType();
            final Object firstElement = Array.get(source, 0);
            if (componentType.isAssignableFrom(targetType)) {
                return firstElement;
            }
            else {
                return this.conversionService.convert(firstElement, targetType);
            }
        }
        return null;
    }
}
